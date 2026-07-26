import { ExtendedItemDetail, IPeripheralProvider, ShortItemDetail } from "@siredvin/typed-peripheral-base";
import { ItemQuery } from "./item_storage";
/** @noSelf **/
export declare interface InventoryViewAPI extends IPeripheral {
    size(): number;
    list(): LuaTable<number, ShortItemDetail>;
    list(detailed: true, query?: ItemQuery): LuaTable<number, ExtendedItemDetail>;
    list(detailed: false, query?: ItemQuery): LuaTable<number, ShortItemDetail>;
    getItemDetail(slot: number): ExtendedItemDetail | null;
    getItemLimit(slot: number): number;
}
export declare const inventoryViewPeripheralProvider: IPeripheralProvider<InventoryViewAPI>;
