/** @noSelf **/
export declare interface CaptureAPI extends IPeripheral {
    capture(mode: "entity" | "block", direction?: "up" | "down"): Result;
    release(): Result;
    getCaptured(): LuaTable<string, any>;
}
