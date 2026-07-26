import { IPeripheralProvider } from "@siredvin/typed-peripheral-base";
/** @noSelf **/
export declare interface EnergyStorageAPI extends IPeripheral {
    getEnergy(): number;
    getEnergyCapacity(): number;
    getEnergyUnit(): string;
}
export declare const energyStoragePeripheralProvider: IPeripheralProvider<EnergyStorageAPI>;
