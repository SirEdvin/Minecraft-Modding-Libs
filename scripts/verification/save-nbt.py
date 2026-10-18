#!/usr/bin/env python3
"""Read-only, dependency-free NBT/Anvil inspection for the R6 save fixture.

Only reads unencrypted local files. No world writing or data fixing is provided.
"""
import gzip
import io
from pathlib import Path
import struct
import zlib


class Reader:
    def __init__(self, data):
        self.stream = io.BytesIO(data)

    def take(self, size):
        value = self.stream.read(size)
        if len(value) != size:
            raise ValueError("Truncated NBT")
        return value

    def number(self, fmt):
        return struct.unpack(">" + fmt, self.take(struct.calcsize(">" + fmt)))[0]

    def string(self):
        # Fixture strings are ASCII; surrogateescape preserves unexpected bytes.
        return self.take(self.number("H")).decode("utf-8", errors="surrogateescape")

    def length(self):
        length = self.number("i")
        if not 0 <= length <= 16_777_216:
            raise ValueError("Invalid/unbounded NBT length")
        return length

    def payload(self, kind, depth=0):
        if depth > 128:
            raise ValueError("NBT nesting limit")
        if kind in range(1, 7):
            return self.number({1: "b", 2: "h", 3: "i", 4: "q", 5: "f", 6: "d"}[kind])
        if kind == 8:
            return self.string()
        if kind in (7, 11, 12):
            fmt = {7: "b", 11: "i", 12: "q"}[kind]
            return [self.number(fmt) for _ in range(self.length())]
        if kind == 9:
            child = self.number("B")
            return [self.payload(child, depth + 1) for _ in range(self.length())]
        if kind == 10:
            value = {}
            while True:
                child = self.number("B")
                if child == 0:
                    return value
                name = self.string()
                if name in value:
                    raise ValueError("Duplicate compound key")
                value[name] = self.payload(child, depth + 1)
        raise ValueError(f"Unsupported NBT tag {kind}")

    def root(self):
        kind = self.number("B")
        if kind != 10:
            raise ValueError("Expected named compound NBT root")
        self.string()
        result = self.payload(kind)
        if self.stream.read(1):
            raise ValueError("Trailing NBT bytes")
        return result


def level(world):
    return Reader(gzip.decompress((world / "level.dat").read_bytes())).root()


def chunk(world, x=0, z=0):
    relative = Path("region") / f"r.{x // 32}.{z // 32}.mca"
    candidates = [world / relative, world / "dimensions/minecraft/overworld" / relative]
    existing = [p for p in candidates if p.is_file()]
    if len(existing) != 1:
        raise ValueError(f"Expected exactly one region layout, found {existing}")
    data = existing[0].read_bytes()
    entry = ((x % 32) + (z % 32) * 32) * 4
    location = int.from_bytes(data[entry:entry + 4], "big")
    offset, sectors = location >> 8, location & 255
    if not offset or not sectors:
        raise ValueError("Fixture chunk missing")
    start = offset * 4096
    length = int.from_bytes(data[start:start + 4], "big")
    if not 1 < length <= sectors * 4096 - 4 or start + 4 + length > len(data):
        raise ValueError("Invalid Anvil chunk length")
    compression = data[start + 4]
    raw = data[start + 5:start + 4 + length]
    if compression == 1:
        raw = gzip.decompress(raw)
    elif compression == 2:
        raw = zlib.decompress(raw)
    elif compression != 3:
        raise ValueError(f"Unsupported Anvil compression {compression}")
    return Reader(raw).root()


def block_at(data, x, y, z):
    section = next(s for s in data["sections"] if s["Y"] == y // 16)
    states = section["block_states"]
    palette = states["palette"]
    if len(palette) == 1:
        return palette[0]
    bits = max(4, (len(palette) - 1).bit_length())
    per_long = 64 // bits
    index = (y % 16) * 256 + (z % 16) * 16 + x % 16
    packed = states["data"][index // per_long] & ((1 << 64) - 1)
    return palette[(packed >> ((index % per_long) * bits)) & ((1 << bits) - 1)]
