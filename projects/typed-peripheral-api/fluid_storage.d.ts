import { FluidDetail, IPeripheralProvider } from "@siredvin/typed-peripheral-base";
/** @noSelf **/
export declare interface FluidStorageAPI extends IPeripheral {
    tanks(): LuaTable<number, FluidDetail>;
    pushFluid(toName: string, limit?: number, fluid?: string): number;
    pullFluid(fromName: string, limit?: number, fluid?: string): number;
}
export declare const fluidStoragePeripheralProvider: IPeripheralProvider<FluidStorageAPI>;
