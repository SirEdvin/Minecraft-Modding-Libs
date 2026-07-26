export declare interface FuelAPIConfiguration {
    maxFuelConsumptionRate: number;
    isFuelConsumptionDisable: boolean;
}
/** @noSelf **/
export declare interface FuelApi extends IPeripheral {
    getFuelLevel(): number;
    getFuelMaxLevel(): number;
    getFuelConsumptionRate(): number;
    setFuelConsumptionRate(rate: number): TResult<boolean>;
}
/** @noSelf **/
export declare class DummyFuelApi implements FuelApi {
    getFuelLevel(): number;
    getFuelMaxLevel(): number;
    getFuelConsumptionRate(): number;
    setFuelConsumptionRate(rate: number): TResult<boolean>;
}
