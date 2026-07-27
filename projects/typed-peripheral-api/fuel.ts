let GLOBAL_FUEL_CONSUMPTION_RATE = 1;

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
export class DummyFuelApi implements FuelApi {
    getFuelLevel(): number {
        return 100_000;
    }
    getFuelMaxLevel(): number {
        return 1_000_000;
    }
    getFuelConsumptionRate(): number {
        return GLOBAL_FUEL_CONSUMPTION_RATE;
    }
    setFuelConsumptionRate(rate: number): TResult<boolean> {
        GLOBAL_FUEL_CONSUMPTION_RATE = rate;
        return $multi(true, null);
    }
}
