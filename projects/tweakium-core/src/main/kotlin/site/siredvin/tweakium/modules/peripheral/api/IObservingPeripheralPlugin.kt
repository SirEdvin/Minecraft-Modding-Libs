package site.siredvin.tweakium.modules.peripheral.api

interface IObservingPeripheralPlugin : IPeripheralPlugin {
    fun onFirstAttach()
    fun onLastDetach()
}
