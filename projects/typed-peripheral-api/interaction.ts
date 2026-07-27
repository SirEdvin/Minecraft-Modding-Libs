/** @noSelf **/
export declare interface InteractionApi extends IPeripheral {
    use(mode: InteractionMode, direction?: Direction): Result;
    swing(mode: InteractionMode, direction?: Direction): Result;
}

/** @noSelf **/
export class DummyInteractionApi implements InteractionApi {
    use(mode: InteractionMode, direction?: Direction): Result {
        return $multi(true, null);
    }
    swing(mode: InteractionMode, direction?: Direction): Result {
        return $multi(true, null);
    }
}
