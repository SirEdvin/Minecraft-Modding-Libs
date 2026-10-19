local p = assert(peripheral.wrap("left"), "Native Tweakium turtle upgrade missing")
assert(peripheral.getType("left") == "r3_native")
assert(p.size() == 16)
assert(p.list()[2].name == "minecraft:diamond" and p.list()[2].count == 12)
assert(p.getEnergy() == 100 and p.getEnergyCapacity() >= 100)
assert(p.getEnergyUnit() == "turtleFuel")
assert(p.tanks()[1].name == "minecraft:water")
assert(p.pushItems("front", "minecraft:diamond", 6, 2) == 4)
assert(p.pullItems("front", 2, 2, 3) == 2)
assert(p.list()[2].count == 8 and p.list()[3].count == 2)
local refused = {
    function() return p.pushItems("front", 2, 2, 0) end,
    function() return p.pullItems("front", 2, 2, 17) end,
    function() return p.pushItems("front", {or_ = {}}, 2) end,
    function() return p.pushItems("front", {unknown_instruction = true}, 2) end,
    function() return p.pushItem("front", "Invalid Identifier!", 2) end,
    function() return p.pushItems("missing", 2, 2) end,
    function() return p.pushItems("front", 2, 1e100) end,
    function() return p.pushItem("front", "minecraft:diamond", 1e100) end,
    function() return p.pushEnergy("front", 1e100) end,
    function() return p.pullEnergy("front", math.huge) end,
}
for index, action in ipairs(refused) do assert(not pcall(action), "Malformed transfer was accepted: " .. index) end
assert(p.pushItems("front", 2, -5) == 0)
assert(p.pushItems("front", 2, 0) == 0)
assert(p.list()[2].count == 8 and p.list()[3].count == 2)
assert(p.pushFluid("front", 1000, "minecraft:water") == 1000)
assert(p.pullFluid("front", 1000, "minecraft:water") == 1000)
for _, limit in ipairs({-1, 0, 0/0, math.huge}) do
    local ok, moved = pcall(p.pushFluid, "front", limit, "minecraft:water")
    assert(not ok or moved == 0, "Malformed fluid limit mutated native buckets")
end
assert(not pcall(p.pushFluid, "front", 1000, "minecraft:missing_fluid"))
assert(not pcall(p.pushFluid, "front", 1000, "Invalid Identifier!"))
assert(p.pushEnergy("front", 80) == 25)
assert(p.pullEnergy("front", 9) == 9)
assert(p.pushEnergy("front", -5) == 0 and p.pullEnergy("front", 0) == 0)
assert(p.getEnergy() == 84)
assert(not pcall(p.pushEnergy, "missing", 5))
local result, reason = p.spend(false)
assert(result == nil and reason == "permission denied" and p.getEnergy() == 84)
result, reason = p.spendInsufficient()
assert(result == nil and type(reason) == "string" and p.getEnergy() == 84)
assert(p.spend(true) == 1 and p.getEnergy() == 77)
result, reason = p.spend(true)
assert(result == nil and type(reason) == "string" and p.getEnergy() == 77)
assert(p.getCooldown("r3Paid") > 0)
