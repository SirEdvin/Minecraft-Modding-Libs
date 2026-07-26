export declare interface FuelAPIConfiguration {
    maxFuelConsumptionRate: number;
    isFuelConsumptionDisable: number;
}
/** @noSelf **/
export declare interface FuelApi extends IPeripheral {
    getFuelLevel(): number;
    getFuelMaxLevel(): number;
    getFuelConsumptionRate(): number;
    setFuelConsumptionRate(rate: number): void;
}
/** @noSelf **/
export declare class DummyFuelApi implements FuelApi {
    getFuelLevel(): number;
    getFuelMaxLevel(): number;
    getFuelConsumptionRate(): number;
    setFuelConsumptionRate(rate: number): void;
}
