import { IPeripheralProvider } from "@siredvin/typed-peripheral-base";
import { InventoryViewAPI } from "./inventory_view";
/** @noSelf **/
export declare interface InventoryAPI extends InventoryViewAPI {
    pushItems(toName: string, fromSlot: number, limit?: number, toSlot?: number): number;
    pullItems(fromName: string, fromSlot: number, limit?: number, toSlot?: number): number;
}
export declare const inventoryPeripheralProvider: IPeripheralProvider<InventoryAPI>;
