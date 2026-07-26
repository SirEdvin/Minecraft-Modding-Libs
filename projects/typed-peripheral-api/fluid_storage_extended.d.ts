import { IPeripheralProvider, PreciseFluidDetail } from "@siredvin/typed-peripheral-base";
import { FluidStorageAPI } from "./fluid_storage";
/** @noSelf **/
export declare interface ExtendedFluidStorageAPI extends FluidStorageAPI {
    tanks(): LuaTable<number, PreciseFluidDetail>;
    capacities(): LuaTable<number, number>;
}
export declare const extendedFluidStoragePeripheralProvider: IPeripheralProvider<ExtendedFluidStorageAPI>;
