/** @noSelf **/
export declare interface WarpAPI extends IPeripheral {
    savePoint(name: string): Result;
    deletePoint(name: string): Result;
    warpToPoint(name: string): Result;
    points(): string[];
    estimateWarpCost(name: string): number;
    distanceToPoint(name: string): number;
}
