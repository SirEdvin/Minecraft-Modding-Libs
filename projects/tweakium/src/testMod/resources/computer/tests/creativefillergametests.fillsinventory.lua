local filler = assert(peripheral.find("creative_filler"), "Creative Filler is missing")
local target = assert(peripheral.find("minecraft:barrel"), "Target barrel is missing")
assert(not next(target.list()), "Target barrel must be empty before filling")
assert(peripheral.getType(peripheral.getName(filler)) == "creative_filler")
assert(type(filler.getConfiguration()) == "table", "ConfigurationAPI must return a table")
local methods = {}
for _, name in ipairs(peripheral.getMethods(peripheral.getName(filler))) do
    assert(not methods[name], "Duplicate peripheral method: " .. name)
    methods[name] = true
end
assert(methods.put and methods.getConfiguration, "CreativeFillerAPI method missing")

assert(select("#", filler.put("item", peripheral.getName(target), "minecraft:stone", 1)) == 0,
    "CreativeFillerAPI.put must return void, not a transfer count")

local inventory = target.list()
local stone = assert(inventory[1], "Target barrel was not filled")
assert(stone.name == "minecraft:stone", "Expected stone, got " .. stone.name)
assert(stone.count == 1, "Expected one stone, got " .. stone.count)
for slot in pairs(inventory) do assert(slot == 1, "Expected only slot 1 to be filled") end
assert(target.size() == 27, "InventoryAPI.size must return the native barrel size")
local detail = assert(target.getItemDetail(1), "InventoryAPI detail missing")
assert(detail.name == stone.name and detail.count == stone.count)
assert(target.getItemDetail(2) == nil, "Empty inventory slot must return nil")

assert(not pcall(filler.put, "invalid", peripheral.getName(target), "minecraft:stone"))
assert(not pcall(filler.put, "item", "missing", "minecraft:stone"))
assert(not pcall(filler.put, "item", filler, "minecraft:stone"))
assert(not pcall(filler.put, "item", peripheral.getName(target), "minecraft:missing"))
assert(target.list()[1].count == 1, "Refused calls must not mutate the inventory")

-- The public limit is optional; the native default is one stack, not one item.
assert(select("#", filler.put("item", peripheral.getName(target), "minecraft:dirt")) == 0)
local dirt = assert(target.list()[2], "Default-limit put did not create the second stack")
assert(dirt.name == "minecraft:dirt" and dirt.count == 64)
-- Native inventory methods use one-based slots and return numeric moved counts.
assert(target.pushItems(peripheral.getName(target), 1, 1, 3) == 1)
assert(target.list()[1] == nil and target.list()[3].count == 1)
assert(target.pullItems(peripheral.getName(target), 3, 1, 1) == 1)
assert(target.list()[3] == nil and target.list()[1].count == 1)
test.log("R7_CONTRACTS_OK: filler methods/configuration/void/default/refusals; inventory size/detail/one-based transfers/counts")
