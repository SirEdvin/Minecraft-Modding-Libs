import { ExtendedItemDetail, IPeripheralProvider, PreciseFluidDetail, ShortItemDetail } from "@siredvin/typed-peripheral-base";
import { InventoryAPI, inventoryPeripheralProvider } from "../inventory";
import { ExtendedInventoryAPI, extendedInventoryPeripheralProvider } from "../inventory_extended";
import { InventoryViewAPI, inventoryViewPeripheralProvider } from "../inventory_view";
import { ItemQuery, ItemStorageAPI, itemStoragePeripheralProvider } from "../item_storage";
import { ScanApi, ScannedBlock, ScannedEntity, ScannedItem, ScannedPlayer, ScannedXP } from "../scan";
import { CaptureAPI } from "../capture";
import { ConfigurationAPI } from "../configuration";
import { CreativeFillerAPI, creativeFillerPeripheralProvider } from "../creative_filler";
import { EnergyStorageAPI, energyStoragePeripheralProvider } from "../energy_storage";
import { ExtendedEnergyStorageAPI, extendedEnergyStoragePeripheralProvider } from "../energy_storage_extended";
import { ExperienceApi } from "../experience";
import { FluidStorageAPI, fluidStoragePeripheralProvider } from "../fluid_storage";
import { ExtendedFluidStorageAPI, extendedFluidStoragePeripheralProvider } from "../fluid_storage_extended";
import { FuelApi } from "../fuel";
import { InteractionApi } from "../interaction";
import { LookApi } from "../look";
import { OperationApi } from "../operations";
import { SuckAPI } from "../suck";
import { WarpAPI } from "../warp";

// Assignment alone accepts any. Compare inferred types in both directions,
// rejecting top-level any explicitly; equality also distinguishes nested any.
type IsAny<T> = 0 extends (1 & T) ? true : false;
type Equal<A, B> = IsAny<A> extends true ? false
    : IsAny<B> extends true ? false
    : (<T>() => T extends A ? 1 : 2) extends (<T>() => T extends B ? 1 : 2)
        ? (<T>() => T extends B ? 1 : 2) extends (<T>() => T extends A ? 1 : 2)
            ? true : false
        : false;
type Assert<T extends true> = T;
type ProviderAPI<P> = P extends IPeripheralProvider<infer API> ? API : never;

type EqualityContracts = [
    Assert<Equal<Equal<any, any>, false>>,
    Assert<Equal<Equal<any, number>, false>>,
    Assert<Equal<Equal<number, any>, false>>,
    Assert<Equal<Equal<any[], number[]>, false>>,
    Assert<Equal<Equal<unknown, number>, false>>,
    Assert<Equal<Equal<never, number>, false>>,
];

declare const inventory: InventoryAPI;
declare const extended: ExtendedInventoryAPI;
declare const items: ItemStorageAPI;
declare const scan: ScanApi;
declare const capture: CaptureAPI;
declare const configuration: ConfigurationAPI<{ limit: number }>;
declare const filler: CreativeFillerAPI;
declare const energy: ExtendedEnergyStorageAPI;
declare const experience: ExperienceApi;
declare const fluid: ExtendedFluidStorageAPI;
declare const fuel: FuelApi;
declare const interaction: InteractionApi;
declare const look: LookApi;
declare const operations: OperationApi;
declare const suck: SuckAPI;
declare const warp: WarpAPI;

const query: ItemQuery = { and: [{ name: { in: ["minecraft:diamond"] } }, { not: { tag: "test:blocked" } }] };
const moved = inventory.pullItems("source", 1, 4, 2);
const pushed = inventory.pushItems("target", 1);
const pulledByQuery = extended.pullItems("source", query, 4, 2);
const pulledBySlot = extended.pullItems("source", 1, 4, 2);
const pushedByQuery = extended.pushItems("target", query, 4, 2);
const pushedBySlot = extended.pushItems("target", 1);
const pulledItem = items.pullItem("source", query, 4);
const pushedItem = items.pushItem("target", query, 4);
// Call every overload: ReturnType alone would only inspect the last signature.
const defaultInventory = extended.list();
const detailedInventory = extended.list(true, query);
const shortInventory = extended.list(false, query);
const defaultItems = items.items();
const detailedItems = items.items(true, query);
const shortItems = items.items(false, query);
const itemDetail = inventory.getItemDetail(1);
const blocks = scan.scan("block", 4, { name: "minecraft:stone" });
const unfilteredBlocks = scan.scan("block");
const scannedItems = scan.scan("item", 4);
const entities = scan.scan("entity", 4);
const players = scan.scan("player", 4);
const xp = scan.scan("xp", 4);
const captured = capture.capture("entity", "up");
const configured = configuration.getConfiguration();
const filled = filler.put("energy", "target", "", 100);
const transferredEnergy = energy.pushEnergy("target", 100);
const pulledEnergy = energy.pullEnergy("source", 100);
const sentXP = experience.sendXP({ x: 0, y: 1, z: 0 }, 10);
const capacity = fluid.capacities();
const tanks = fluid.tanks();
const setRate = fuel.setFuelConsumptionRate(2);
const used = interaction.use("block");
const looked = look.look("both", "down");
const cooldown = operations.getCooldown("scan");
const sucked = suck.suck(4, query);
const points = warp.points();

type InferredResultContracts = [
    Assert<Equal<typeof moved, number>>,
    Assert<Equal<typeof pushed, number>>,
    Assert<Equal<typeof pulledByQuery, number>>,
    Assert<Equal<typeof pulledBySlot, number>>,
    Assert<Equal<typeof pushedByQuery, number>>,
    Assert<Equal<typeof pushedBySlot, number>>,
    Assert<Equal<typeof pulledItem, number>>,
    Assert<Equal<typeof pushedItem, number>>,
    Assert<Equal<typeof defaultInventory, LuaTable<number, ShortItemDetail>>>,
    Assert<Equal<typeof detailedInventory, LuaTable<number, ExtendedItemDetail>>>,
    Assert<Equal<typeof shortInventory, LuaTable<number, ShortItemDetail>>>,
    Assert<Equal<typeof defaultItems, LuaTable<number, ExtendedItemDetail>>>,
    Assert<Equal<typeof detailedItems, LuaTable<number, ExtendedItemDetail>>>,
    Assert<Equal<typeof shortItems, LuaTable<number, ShortItemDetail>>>,
    // Nullable coverage is limited: the inherited config has strictNullChecks off.
    // Enabling it currently fails in typed-peripheral-base's TS source (5 errors).
    // This rejects any/wrong item types, NOT removal of null (also for providers).
    Assert<Equal<typeof itemDetail, ExtendedItemDetail | null>>,
    Assert<Equal<typeof blocks, TResult<ScannedBlock[]>>>,
    Assert<Equal<typeof unfilteredBlocks, TResult<ScannedBlock[]>>>,
    Assert<Equal<typeof scannedItems, TResult<ScannedItem[]>>>,
    Assert<Equal<typeof entities, TResult<ScannedEntity[]>>>,
    Assert<Equal<typeof players, TResult<ScannedPlayer[]>>>,
    Assert<Equal<typeof xp, TResult<ScannedXP[]>>>,
    Assert<Equal<typeof captured, Result>>,
    Assert<Equal<typeof configured, { limit: number }>>,
    Assert<Equal<typeof filled, void>>,
    Assert<Equal<typeof transferredEnergy, number>>,
    Assert<Equal<typeof pulledEnergy, number>>,
    Assert<Equal<typeof sentXP, TResult<number>>>,
    Assert<Equal<typeof capacity, LuaTable<number, number>>>,
    Assert<Equal<typeof tanks, LuaTable<number, PreciseFluidDetail>>>,
    Assert<Equal<typeof setRate, TResult<boolean>>>,
    Assert<Equal<typeof used, Result>>,
    Assert<Equal<typeof looked, TResult<LuaTable<string, any>>>>,
    Assert<Equal<typeof cooldown, number>>,
    Assert<Equal<typeof sucked, Result>>,
    Assert<Equal<typeof points, string[]>>,
];

// Import the actual exports: recreating providers here would miss erased generics.
const wrappedFiller = creativeFillerPeripheralProvider.wrap("filler");
const wrappedEnergy = energyStoragePeripheralProvider.wrap("energy");
const wrappedExtendedEnergy = extendedEnergyStoragePeripheralProvider.wrap("energy");
const wrappedFluid = fluidStoragePeripheralProvider.wrap("fluid");
const wrappedExtendedFluid = extendedFluidStoragePeripheralProvider.wrap("fluid");
const wrappedInventory = inventoryPeripheralProvider.wrap("inventory");
const wrappedExtendedInventory = extendedInventoryPeripheralProvider.wrap("inventory");
const wrappedView = inventoryViewPeripheralProvider.wrap("inventory");
const wrappedItems = itemStoragePeripheralProvider.wrap("items");
const wrappedFillers = creativeFillerPeripheralProvider.wrap(["filler"]);
const wrappedEnergies = energyStoragePeripheralProvider.wrap(["energy"]);
const wrappedExtendedEnergies = extendedEnergyStoragePeripheralProvider.wrap(["energy"]);
const wrappedFluids = fluidStoragePeripheralProvider.wrap(["fluid"]);
const wrappedExtendedFluids = extendedFluidStoragePeripheralProvider.wrap(["fluid"]);
const wrappedInventories = inventoryPeripheralProvider.wrap(["inventory"]);
const wrappedExtendedInventories = extendedInventoryPeripheralProvider.wrap(["inventory"]);
const wrappedViews = inventoryViewPeripheralProvider.wrap(["inventory"]);
const wrappedItemStores = itemStoragePeripheralProvider.wrap(["items"]);

type ProviderContracts = [
    Assert<Equal<ProviderAPI<typeof creativeFillerPeripheralProvider>, CreativeFillerAPI>>,
    Assert<Equal<ProviderAPI<typeof energyStoragePeripheralProvider>, EnergyStorageAPI>>,
    Assert<Equal<ProviderAPI<typeof extendedEnergyStoragePeripheralProvider>, ExtendedEnergyStorageAPI>>,
    Assert<Equal<ProviderAPI<typeof fluidStoragePeripheralProvider>, FluidStorageAPI>>,
    Assert<Equal<ProviderAPI<typeof extendedFluidStoragePeripheralProvider>, ExtendedFluidStorageAPI>>,
    Assert<Equal<ProviderAPI<typeof inventoryPeripheralProvider>, InventoryAPI>>,
    Assert<Equal<ProviderAPI<typeof extendedInventoryPeripheralProvider>, ExtendedInventoryAPI>>,
    Assert<Equal<ProviderAPI<typeof inventoryViewPeripheralProvider>, InventoryViewAPI>>,
    Assert<Equal<ProviderAPI<typeof itemStoragePeripheralProvider>, ItemStorageAPI>>,
    Assert<Equal<typeof wrappedFiller, CreativeFillerAPI | null>>,
    Assert<Equal<typeof wrappedEnergy, EnergyStorageAPI | null>>,
    Assert<Equal<typeof wrappedExtendedEnergy, ExtendedEnergyStorageAPI | null>>,
    Assert<Equal<typeof wrappedFluid, FluidStorageAPI | null>>,
    Assert<Equal<typeof wrappedExtendedFluid, ExtendedFluidStorageAPI | null>>,
    Assert<Equal<typeof wrappedInventory, InventoryAPI | null>>,
    Assert<Equal<typeof wrappedExtendedInventory, ExtendedInventoryAPI | null>>,
    Assert<Equal<typeof wrappedView, InventoryViewAPI | null>>,
    Assert<Equal<typeof wrappedItems, ItemStorageAPI | null>>,
    Assert<Equal<typeof wrappedFillers, CreativeFillerAPI[]>>,
    Assert<Equal<typeof wrappedEnergies, EnergyStorageAPI[]>>,
    Assert<Equal<typeof wrappedExtendedEnergies, ExtendedEnergyStorageAPI[]>>,
    Assert<Equal<typeof wrappedFluids, FluidStorageAPI[]>>,
    Assert<Equal<typeof wrappedExtendedFluids, ExtendedFluidStorageAPI[]>>,
    Assert<Equal<typeof wrappedInventories, InventoryAPI[]>>,
    Assert<Equal<typeof wrappedExtendedInventories, ExtendedInventoryAPI[]>>,
    Assert<Equal<typeof wrappedViews, InventoryViewAPI[]>>,
    Assert<Equal<typeof wrappedItemStores, ItemStorageAPI[]>>,
];

// Each directive must correspond to a real diagnostic: accidental widening fails the suite.
// @ts-expect-error Slot arguments are numeric in the vanilla inventory contract.
inventory.pullItems("source", query);
// @ts-expect-error A query operation takes strings, not numeric IDs.
const invalidQuery: ItemQuery = { name: { in: [42] } };
// @ts-expect-error Direction is limited to up/down.
capture.capture("block", "sideways");
// @ts-expect-error Filling arbitrary modes is not part of the public API.
filler.put("magic", "target", "", 1);
// @ts-expect-error Coordinates must be complete.
experience.sendXP({ x: 0, y: 1 }, 10);
// @ts-expect-error Scanned players do not expose block states.
const invalidPlayers: TResult<ScannedBlock[]> = scan.scan("player");
// @ts-expect-error Transfer limits are numeric.
fluid.pushFluid("target", "100");
// @ts-expect-error A warp point name is required.
warp.warpToPoint();
// @ts-expect-error Only block scans accept a block filter, not item scans.
scan.scan("item", 4, { name: "minecraft:stone" });
// @ts-expect-error Only block scans accept a block filter, not entity scans.
scan.scan("entity", 4, { name: "minecraft:stone" });
// @ts-expect-error Only block scans accept a block filter, not player scans.
scan.scan("player", 4, { name: "minecraft:stone" });
// @ts-expect-error Only block scans accept a block filter, not xp scans.
scan.scan("xp", 4, { name: "minecraft:stone" });
