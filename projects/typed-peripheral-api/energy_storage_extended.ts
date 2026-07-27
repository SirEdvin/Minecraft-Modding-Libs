import { IPeripheralProvider } from "@siredvin/typed-peripheral-base";
import { EnergyStorageAPI } from "./energy_storage";

/** @noSelf **/
export declare interface ExtendedEnergyStorageAPI extends EnergyStorageAPI {
    pullEnergy(from: string, limit?: number): number;
    pushEnergy(to: string, limit?: number): number;
}

export const extendedEnergyStoragePeripheralProvider =
    new IPeripheralProvider<ExtendedEnergyStorageAPI>(
        "energy_storage_extended",
        () => null
    );
