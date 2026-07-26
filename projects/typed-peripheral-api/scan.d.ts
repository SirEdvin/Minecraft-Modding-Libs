import { ExtendedItemDetail } from "@siredvin/typed-peripheral-base";
import { QueryValue } from "./item_storage";
export declare type ScanApiMods = "item" | "block" | "entity" | "player" | "xp";
export declare type ScannedPosition = {
    y: number;
    x: number;
    z: number;
};
export declare type ScannedItem = ExtendedItemDetail & ScannedPosition;
export declare type ScannedBlockBase = ScannedPosition & {
    displayName: string;
    name: string;
};
export declare type ScannedBlock = ScannedBlockBase & {
    tags: LuaTable<string, boolean>;
    state: LuaTable<string, string | number | boolean>;
};
export declare type ScannedEntityBase = ScannedPosition & {
    displayName: string;
    name: string;
    tags: Array<string>;
    uuid: string;
    type: string;
    category: string;
};
export declare type ScannedEntity = ScannedEntityBase & {
    health: number;
};
export declare type ScannedPlayer = ScannedEntity & {
    foodLevel: number;
    saturationLevel: number;
    xRot: number;
    yRot: number;
    experienceLevel: number;
    isCreative: boolean;
};
export declare type ScannedXP = ScannedEntityBase & {
    xpValue: number;
};
export declare type BlockQueryObject = {
    tag?: QueryValue;
    taguru?: QueryValue;
    displayName?: string;
    tekisuto?: string;
    name?: QueryValue;
    namae?: QueryValue;
    or?: Array<BlockQueryObject>;
    or_?: Array<BlockQueryObject>;
    owo?: Array<BlockQueryObject>;
    any?: Array<BlockQueryObject>;
    and?: Array<BlockQueryObject>;
    and_?: Array<BlockQueryObject>;
    uwu?: Array<BlockQueryObject>;
    all?: Array<BlockQueryObject>;
    none?: Array<BlockQueryObject>;
    nuwu?: Array<BlockQueryObject>;
    not?: BlockQueryObject;
    no?: BlockQueryObject;
    not_?: BlockQueryObject;
    negate?: BlockQueryObject;
    nawu?: BlockQueryObject;
};
export declare type BlockQuery = string | BlockQueryObject;
/** @noSelf **/
export declare interface ScanApi extends IPeripheral {
    scan(mode: "item", radius?: number): TResult<Array<ScannedItem>>;
    scan(mode: "block", radius?: number, filter?: BlockQuery): TResult<Array<ScannedBlock>>;
    scan(mode: "entity", radius?: number): TResult<Array<ScannedEntity>>;
    scan(mode: "player", radius?: number): TResult<Array<ScannedPlayer>>;
    scan(mode: "xp", radius?: number): TResult<Array<ScannedXP>>;
    scan(mode: ScanApiMods, radius?: number): TResult<Array<any>>;
}
