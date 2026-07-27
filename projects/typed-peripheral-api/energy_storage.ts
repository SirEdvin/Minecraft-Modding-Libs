import { IPeripheralProvider } from "@siredvin/typed-peripheral-base";

/** @noSelf **/
export declare interface EnergyStorageAPI extends IPeripheral {
    getEnergy(): number;
    getEnergyCapacity(): number;
    getEnergyUnit(): string;
}

export const energyStoragePeripheralProvider =
    new IPeripheralProvider<EnergyStorageAPI>("energy_storage", () => null);
