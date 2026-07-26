import {
    ExtendedItemDetail,
    IPeripheralProvider,
    ShortItemDetail,
} from "@siredvin/typed-peripheral-base";
import { ConfigurationAPI } from "./configuration";
import { InventoryAPI } from "./inventory";
import { ItemQuery } from "./item_storage";

export declare interface ExtendedInventoryConfiguration {
    inventoryTransferLimit: number;
}

/** @noSelf **/
export declare interface ExtendedInventoryAPI
    extends InventoryAPI,
        ConfigurationAPI<ExtendedInventoryConfiguration> {
    list(): LuaTable<number, ShortItemDetail>;
    list(
        detailed: true,
        query?: ItemQuery
    ): LuaTable<number, ExtendedItemDetail>;
    list(detailed: false, query?: ItemQuery): LuaTable<number, ShortItemDetail>;
    pushItems(
        toName: string,
        filter: ItemQuery,
        limit?: number,
        toSlot?: number
    ): number;
    pushItems(
        toName: string,
        fromSlot: number,
        limit?: number,
        toSlot?: number
    ): number;
    pullItems(
        fromName: string,
        filter: ItemQuery,
        limit?: number,
        toSlot?: number
    ): number;
    pullItems(
        fromName: string,
        fromSlot: number,
        limit?: number,
        toSlot?: number
    ): number;
}

export const extendedInventoryPeripheralProvider =
    new IPeripheralProvider<ExtendedInventoryAPI>(
        "inventory_extended",
        () => null
    );
