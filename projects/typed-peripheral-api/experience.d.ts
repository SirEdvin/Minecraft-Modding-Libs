export declare type RelativeBlockPosition = {
    x: number;
    y: number;
    z: number;
};
export declare interface ExperienceAPIConfiguration {
    xpToFuelRate: number;
}
/** @noSelf **/
export declare interface ExperienceApi extends IPeripheral {
    collectXP(): TResult<number>;
    suckOwnerXP(limit: number): TResult<number>;
    burnXP(limit: number): number;
    sendXPToOwner(limit: number): TResult<number>;
    sendXP(position: RelativeBlockPosition, limit: number): TResult<number>;
    getStoredXP(): number;
    getOwnerXP(): TResult<number>;
}
