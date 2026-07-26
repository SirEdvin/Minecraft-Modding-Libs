/** @noSelf **/
export declare interface InteractionApi extends IPeripheral {
    use(mode: InteractionMode, direction?: Direction): Result;
    swing(mode: InteractionMode, direction?: Direction): Result;
}
/** @noSelf **/
export declare class DummyInteractionApi implements InteractionApi {
    use(mode: InteractionMode, direction?: Direction): Result;
    swing(mode: InteractionMode, direction?: Direction): Result;
}
