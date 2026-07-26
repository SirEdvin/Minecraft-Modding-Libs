/** @noSelf **/
export declare interface OperationApi extends IPeripheral {
    getOperations(): Array<string>;
    getCooldown(operationName: string): number;
}
