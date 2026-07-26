import { IPeripheralProvider } from "@siredvin/typed-peripheral-base";
import { ConfigurationAPI } from "./configuration";
export declare type CreativeFillerMode = "item" | "fluid" | "energy";
/** @noSelf **/
export declare interface CreativeFillerAPI extends ConfigurationAPI<LuaTable<string, any>> {
    put(mode: CreativeFillerMode, target: string, id: string, limit?: number): void;
}
export declare const creativeFillerPeripheralProvider: IPeripheralProvider<CreativeFillerAPI>;
