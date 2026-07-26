/** @noSelf **/
export declare interface SuckAPI extends IPeripheral {
    suck(
        limit?: number,
        itemQuery?: string | LuaTable<string, any> | object
    ): Result;
}
