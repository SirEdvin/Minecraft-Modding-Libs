# Testiarium

Testiarium is a Minecraft 1.20.1 GameTest helper for Forge and Fabric.

Register the classes that contain your tests during your testmod initialization:

```kotlin
Testiarium.register(MyGameTests::class.java)
```

Then register the collected tests through the current loader:

```kotlin
// Forge
ForgeTestiarium.registerTests()

// Fabric
FabricTestiarium.registerTests()
```

Use `-Dtestiarium.tags=common,client` to enable groups declared with `@TestGroup`.
Use `-Dtestiarium.gametest-report=build/test-results/gametest.xml` to write JUnit XML.

CC:Tweaked is optional. Add its matching loader artifact only to your `testMod` classpath, and keep all code using its public APIs in that testmod source set.
