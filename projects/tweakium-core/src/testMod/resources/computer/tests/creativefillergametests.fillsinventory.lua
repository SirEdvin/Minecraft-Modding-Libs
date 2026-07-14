local filler = assert(peripheral.find("creative_filler"), "Creative Filler is missing")
local target
for _, name in ipairs(peripheral.getNames()) do
    if peripheral.getType(name) == "minecraft:barrel" then target = name end
end
assert(target, "Target inventory is missing")

filler.put("item", target, "minecraft:stone", 1)

assert(not pcall(filler.put, "invalid", target, "minecraft:stone"))
assert(not pcall(filler.put, "item", "missing", "minecraft:stone"))
assert(not pcall(filler.put, "item", filler, "minecraft:stone"))
assert(not pcall(filler.put, "item", target, "minecraft:missing"))
