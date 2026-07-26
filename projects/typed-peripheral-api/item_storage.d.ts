import { ExtendedItemDetail, IPeripheralProvider, ShortItemDetail } from "@siredvin/typed-peripheral-base";
export type ItemQuery = string | {
    nbt?: string;
    tag?: string;
    displayName?: string;
    name?: string;
};
/** @noSelf **/
export declare interface ItemStorageAPI extends IPeripheral {
    items(): LuaTable<number, ExtendedItemDetail>;
    items(detailed: true, query?: LuaTable<string, any> | object): LuaTable<number, ExtendedItemDetail>;
    items(detailed: false, query?: LuaTable<string, any> | object): LuaTable<number, ShortItemDetail>;
    pushItem(toName: string, itemQuery?: string, limit?: number, to_slot?: number): number;
    pullItem(fromName: string, itemQuery?: string, limit?: number, to_slot?: number): number;
}
export declare const itemStoragePeripheralProvider: IPeripheralProvider<ItemStorageAPI>;
