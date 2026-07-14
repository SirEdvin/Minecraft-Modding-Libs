local filler = assert(peripheral.find("creative_filler"), "Creative Filler is missing")
local target = assert(peripheral.find("minecraft:barrel"), "Target barrel is missing")
assert(not next(target.list()), "Target barrel must be empty before filling")

filler.put("item", peripheral.getName(target), "minecraft:stone", 1)

local inventory = target.list()
local stone = assert(inventory[1], "Target barrel was not filled")
assert(stone.name == "minecraft:stone", "Expected stone, got " .. stone.name)
assert(stone.count == 1, "Expected one stone, got " .. stone.count)
for slot in pairs(inventory) do assert(slot == 1, "Expected only slot 1 to be filled") end

assert(not pcall(filler.put, "invalid", peripheral.getName(target), "minecraft:stone"))
assert(not pcall(filler.put, "item", "missing", "minecraft:stone"))
assert(not pcall(filler.put, "item", filler, "minecraft:stone"))
assert(not pcall(filler.put, "item", peripheral.getName(target), "minecraft:missing"))
