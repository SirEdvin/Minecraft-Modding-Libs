package site.siredvin.testiarium.api

/** Annotation facade for the registry-based native GameTest API (26.1+). */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class GameTest(
    val template: String = "",
    val batch: String = "defaultBatch",
    val timeoutTicks: Int = Timeouts.DEFAULT,
    val setupTicks: Int = 0,
    val required: Boolean = true,
    val rotationSteps: Int = 0,
    val manualOnly: Boolean = false,
    val attempts: Int = 1,
    val requiredSuccesses: Int = 1,
    val skyAccess: Boolean = false,
    val padding: Int = 0,
)
