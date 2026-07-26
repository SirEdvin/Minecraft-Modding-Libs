export declare type ScanApiMods = "item" | "block" | "entity" | "player" | "xp";
export declare type ScannedItem = {
    tags: Array<string>;
    maxCount: number;
    displayName: string;
    y: number;
    x: number;
    z: number;
    count: number;
    rawName: string;
    name: string;
    itemGroups: Array<string>;
};
export declare type ScannedBlockBase = {
    y: number;
    x: number;
    z: number;
    displayName: string;
    name: string;
};
export declare type ScannedBlock = ScannedBlockBase & {
    tags: LuaTable<string, boolean>;
};
export declare type ScannedEntity = {
    y: number;
    x: number;
    z: number;
    displayName: string;
    name: string;
    tags: Array<string>;
    health: number;
    uuid: string;
    type: string;
    category: string;
};
export declare type ScannedPlayer = ScannedEntity & {
    foodLevel: number;
    saturationLevel: number;
    xRot: number;
    yRot: number;
    experienceLevel: number;
    isCreative: boolean;
};
/** @noSelf **/
export declare interface ScanApi extends IPeripheral {
    scan(mode: "item", radius?: number): TResult<Array<ScannedItem>>;
    scan(mode: "block", radius?: number, filter?: LuaTable<string, any>): TResult<Array<ScannedBlock>>;
    scan(mode: "entity", radius?: number): TResult<Array<ScannedEntity>>;
    scan(mode: "player", radius?: number): TResult<Array<ScannedPlayer>>;
    scan(mode: ScanApiMods, radius?: number): TResult<Array<any>>;
}
