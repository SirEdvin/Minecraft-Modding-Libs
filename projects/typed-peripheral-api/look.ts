/** @noSelf **/
export declare interface LookApi extends IPeripheral {
    look(
        mode: "entity" | "block" | "both",
        direction?: "up" | "down"
    ): TResult<LuaTable<string, any>>;
}
