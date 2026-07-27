import {
    ExtendedItemDetail,
    IPeripheralProvider,
    ShortItemDetail,
} from "@siredvin/typed-peripheral-base";

export type QueryValueOperation = "in" | "in_" | "naibu" | "not_in" | "wanai";

export type QueryValue =
    | string
    | {
          [Operation in QueryValueOperation]: Record<Operation, Array<string>>;
      }[QueryValueOperation];

export type ItemQueryObject = {
    nbt?: string;
    nebeturu?: string;
    tag?: QueryValue;
    taguru?: QueryValue;
    displayName?: string;
    tekisuto?: string;
    name?: QueryValue;
    namae?: QueryValue;
    or?: Array<ItemQueryObject>;
    or_?: Array<ItemQueryObject>;
    owo?: Array<ItemQueryObject>;
    any?: Array<ItemQueryObject>;
    and?: Array<ItemQueryObject>;
    and_?: Array<ItemQueryObject>;
    uwu?: Array<ItemQueryObject>;
    all?: Array<ItemQueryObject>;
    none?: Array<ItemQueryObject>;
    nuwu?: Array<ItemQueryObject>;
    not?: ItemQueryObject;
    no?: ItemQueryObject;
    not_?: ItemQueryObject;
    negate?: ItemQueryObject;
    nawu?: ItemQueryObject;
};

export type ItemQuery = string | ItemQueryObject;

/** @noSelf **/
export declare interface ItemStorageAPI extends IPeripheral {
    items(): LuaTable<number, ExtendedItemDetail>;
    items(
        detailed: true,
        query?: ItemQuery
    ): LuaTable<number, ExtendedItemDetail>;
    items(
        detailed: false,
        query?: ItemQuery
    ): LuaTable<number, ShortItemDetail>;
    pushItem(toName: string, itemQuery?: ItemQuery, limit?: number): number;
    pullItem(fromName: string, itemQuery?: ItemQuery, limit?: number): number;
}

export const itemStoragePeripheralProvider =
    new IPeripheralProvider<ItemStorageAPI>("item_storage", () => null);
