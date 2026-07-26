import { ExtendedItemDetail, IPeripheralProvider, ShortItemDetail } from "@siredvin/typed-peripheral-base";
import { ConfigurationAPI } from "./configuration";
import { InventoryAPI } from "./inventory";
export declare interface ExtendedInventoryConfiguration {
    inventoryTransferLimit: number;
}
/** @noSelf **/
export declare interface ExtendedInventoryAPI extends InventoryAPI, ConfigurationAPI<ExtendedInventoryConfiguration> {
    list(): LuaTable<number, ShortItemDetail>;
    list(detailed: true, query?: LuaTable<string, any>): LuaTable<number, ExtendedItemDetail>;
    list(detailed: true, query?: object): LuaTable<number, ExtendedItemDetail>;
    list(detailed: false, query?: LuaTable<string, any>): LuaTable<number, ShortItemDetail>;
    pushItems(toName: string, filter: LuaTable<string, any> | {
        [key: string]: any;
    }, limit?: number, toSlot?: number): number;
    pushItems(toName: string, filter: string, limit?: number, toSlot?: number): number;
    pullItems(fromName: string, filter: LuaTable<string, any>, limit?: number, toSlot?: number): number;
    pullItems(fromName: string, filter: {
        [key: string]: any;
    }, limit?: number, toSlot?: number): number;
    pushItems(toName: string, fromSlot: number, limit?: number, toSlot?: number): number;
    pullItems(fromName: string, fromSlot: number, limit?: number, toSlot?: number): number;
    pullItems(fromName: string, filter: string, limit?: number, toSlot?: number): number;
}
export declare const extendedInventoryPeripheralProvider: IPeripheralProvider<ExtendedInventoryAPI>;
