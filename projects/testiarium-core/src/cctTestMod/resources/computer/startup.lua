-- SPDX-FileCopyrightText: 2022 The CC: Tweaked Developers
-- SPDX-FileCopyrightText: 2026 SirEdvin
-- SPDX-License-Identifier: MPL-2.0
-- Adapted from CC:Tweaked 1.113.0 projects/common/src/testMod/resources/data/cctest/computer/startup.lua.

local label = os.getComputerLabel()
if label == nil then return test.fail("Label a computer to use it.") end

local fn, err = loadfile("tests/" .. label .. ".lua", nil, _ENV)
if not fn then return test.fail(err) end

local ok, result = pcall(fn)
if not ok then return test.fail(result) end
test.ok()
