import {
    ExtendedItemDetail,
    IPeripheralProvider,
    ShortItemDetail,
} from "@siredvin/typed-peripheral-base";

/** @noSelf **/
export declare interface InventoryViewAPI extends IPeripheral {
    size(): number;
    list(): LuaTable<number, ShortItemDetail>;
    getItemDetail(slot: number): ExtendedItemDetail;
    getItemLimit(slot: number): number;
}

export const inventoryViewPeripheralProvider =
    new IPeripheralProvider<InventoryViewAPI>("inventory_view", () => null);
