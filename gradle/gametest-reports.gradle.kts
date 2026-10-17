import javax.xml.parsers.DocumentBuilderFactory

// Some Minecraft startup failures exit zero. Require fresh, non-empty test evidence.
subprojects {
    val reports = when {
        path.startsWith(":broccolium:fabric:") || path == ":broccolium:forge:26.1.2" -> mapOf("runStorageGameTest" to "storage-gametest.xml")
        path.startsWith(":testiarium:fabric:") -> mapOf(
            "runServer" to "gametest.xml",
            "runCctGameTest" to "cct-gametest.xml",
            "runClientGameTest" to "client-gametest.xml",
        )
        path.startsWith(":testiarium:forge:") -> mapOf(
            "runGameTestServer" to if (providers.gradleProperty("testiarium.cct").isPresent) "cct-gametest.xml" else "gametest.xml",
            "runClientGameTest" to "client-gametest.xml",
        )
        path.startsWith(":tweakium:fabric:") -> mapOf("runPeripheralGameTest" to "peripheral-gametest.xml")
        path.startsWith(":tweakium:forge:") -> mapOf("runGameTestServer" to "peripheral-gametest.xml")
        else -> emptyMap()
    }
    tasks.matching { it.name in reports }.configureEach {
        val report = layout.buildDirectory.file("test-results/${reports.getValue(name)}")
        doFirst {
            val file = report.get().asFile
            check(!file.exists() || file.delete()) { "Cannot remove stale GameTest report: $file" }
        }
        doLast {
            val file = report.get().asFile
            check(file.isFile) { "GameTest run did not produce a report: $file" }
            val factory = DocumentBuilderFactory.newInstance().apply {
                setFeature("http://apache.org/xml/features/disallow-doctype-decl", true)
            }
            val document = factory.newDocumentBuilder().parse(file)
            val cases = document.getElementsByTagName("testcase").length
            val failures = document.getElementsByTagName("failure").length
            val errors = document.getElementsByTagName("error").length
            val skipped = document.getElementsByTagName("skipped").length
            check(cases > skipped && failures == 0 && errors == 0) {
                "GameTest report is not passing: $cases tests, $failures failures, $errors errors, $skipped skipped ($file)"
            }
            check(document.documentElement.getAttribute("tests").toInt() == cases) {
                "GameTest report count does not match its test cases: $file"
            }
            for ((attribute, actual) in mapOf("failures" to failures, "errors" to errors, "skipped" to skipped)) {
                val declared = document.documentElement.getAttribute(attribute).ifEmpty { "0" }.toInt()
                check(declared == actual) { "GameTest declared $attribute does not match its elements: $file" }
            }
            if (reports.getValue(name) == "storage-gametest.xml") {
                check(skipped == 0) { "Storage conservation regressions must not be skipped: $file" }
                val expected = mutableSetOf<String>()
                fun expect(testClass: String, vararg methods: String) {
                    methods.mapTo(expected) { "$testClass.$it" }
                }
                if (project.path.startsWith(":broccolium:fabric:")) {
                    expect("ReverseItemBridgeGameTests", "occupiedInsertCommit", "occupiedExtractCommit",
                        "occupiedInsertAbort", "occupiedExtractAbort", "nestedInsertCommitOuterAbort",
                        "nestedExtractCommitOuterAbort", "differentDataCannotMergeIntoOccupiedSlot")
                    expect("ItemBridgeSlotGameTests", "getSlotZeroPreservesCountAndData",
                        "explicitSourceZeroDoesNotSpillIntoNextSlot", "explicitSourceZeroDoesNotSkipRejectedVariant",
                        "explicitDestinationZeroDoesNotSpillIntoNextSlot", "explicitDestinationOnNonSlottedSinkUsesDocumentedFallback")
                    expect("ItemBridgeContractGameTests", "emptySlotInsertionCommitAndAbort", "fullExtractionCommitAndAbort",
                        "variantReplacementCommitAndNestedAbort", "detachedGetterSupportsCommitAndRollback",
                        "rollbackBypassesInsertionPolicy", "replacementCopiesCallerStack", "insertionHonorsPerSlotLimit",
                        "unsupportedReverseAdapterRefusesBeforeWrites", "oversizedStacksRefusedWithoutTruncation",
                        "nativeSlotCommitAbortAndNestedRollback", "nestedAbortPreservesOuterCommit")
                    expect("EnergyIntegrationGameTests", "absentEnergyProvidersReturnNullWithoutChangingItems")
                }
                if (project.name == "26.1.2") {
                    expect("NativeBehaviorGameTests", "entityInventoriesExposeFirstAndLastNativeSlots",
                        "horseCargoExcludesEquipmentAndKeepsEveryCargoSlot", "nativeChunkClearDoesNotDestroyObserver",
                        "nativeSaveRetainsRootPayloadAndRegistryType", "observerUsesNativeBlockOrientationAndRemovalOrder",
                        "removedProvidersDoNotResolveReplacementOrDiscardedCargo", "removedWorldChestIsNotExposed",
                        "worldChestLookupConservesItems")
                    expect("RegistryCodecGameTests", "registeredTooltipHooksAreLazyAndDoNotLoadClientOnServer",
                        "registryEntryCodecResolvesRealSupplierAndRejectsUnknown")
                    if (project.path.contains(":fabric:")) {
                        expect("FabricProviderGameTests", "worldBucketReplacementConservesNativeUnits",
                            "worldPositiveFluidEnergyProvidersAndNestedRollback")
                    } else {
                        expect("ForgeProviderGameTests", "nativeBucketProviderReplacesWorldOriginAndRejectsUnsupported",
                            "worldPositiveProvidersConserveAndRejectRemoved", "completePlayerEquipmentRetainsNativeRulesAndNestedRollback")
                    }
                }
                val nodes = document.getElementsByTagName("testcase")
                val names = (0 until nodes.length).map { (nodes.item(it) as org.w3c.dom.Element).getAttribute("name") }
                check(names.filter { it != "minecraft:always_pass" }.toSet() == expected && names.distinct().size == names.size) {
                    "Storage discovery must match its exact once-only fixture inventory: expected $expected, got $names ($file)"
                }
            }
            if (project.path.startsWith(":testiarium:") && reports.getValue(name) in setOf("gametest.xml", "cct-gametest.xml")) {
                // Negative fixtures must fail for the intended reason, not silently pass or
                // fail later in an unrelated assertion while still being counted as skipped.
                val expectedFailures = linkedMapOf(
                    "StandaloneGameTests.requiredFailure" to "required failure fixture",
                    "StandaloneGameTests.optionalFailure" to "optional failure fixture",
                    "StandaloneGameTests.failFastFailureCannotBecomeSuccess" to "fail-fast sentinel",
                )
                val targetCct = project.name == "26.1.2" && reports.getValue(name) == "cct-gametest.xml"
                if (targetCct) expectedFailures["CctHarnessGameTests.luaFailureCannotBecomeSuccess"] = "cct lua sentinel"
                val testCases = document.getElementsByTagName("testcase")
                val elements = (0 until testCases.length).map { testCases.item(it) as org.w3c.dom.Element }
                for ((testName, message) in expectedFailures) {
                    val matches = (0 until testCases.length).map { testCases.item(it) as org.w3c.dom.Element }
                        .filter { it.getAttribute("name") == testName }
                    check(matches.size == 1) { "Expected exactly one negative fixture $testName ($file)" }
                    val skips = matches.single().getElementsByTagName("skipped")
                    check(skips.length == 1 && (skips.item(0) as org.w3c.dom.Element).getAttribute("message") == message) {
                        "Negative fixture $testName did not preserve its expected failure '$message' ($file)"
                    }
                }
                check(skipped == expectedFailures.size) { "Unexpected optional failure(s) in Testiarium report: $file" }
                val expectedNames = mutableSetOf(
                    "passes", "failFastSequence", "failFastAssertionIsImmediatelyTerminal",
                    "failFastExceptionIsImmediatelyTerminal", "failFastErrorIsImmediatelyTerminal",
                    "containerMatchesIncludingImplicitEmptySlots", "containerRejectsMismatchedActualSlots",
                    "containerRejectsNonemptyExpectedSlotsBeyondItsSize", "reporterCountsEscapesAndFinishesOnce",
                ).mapTo(mutableSetOf()) { "StandaloneGameTests.$it" }
                expectedNames.addAll(expectedFailures.keys)
                if (project.name == "26.1.2") expectedNames.add("StandaloneGameTests.nativeEnvironmentSetupAndTeardown")
                if (targetCct) expectedNames.addAll(listOf(
                    "CctHarnessGameTests.realLuaAndQueuedActionExecuteOnce",
                    "CctHarnessGameTests.luaFailureIsObservedByRequiredOuterTest",
                    "CctHarnessGameTests.queuedActionAfterLuaStartupExecutesOnce",
                ))
                val names = elements.map { it.getAttribute("name") }
                val nativeNames = names.filter { it != "minecraft:always_pass" }
                check(nativeNames.toSet() == expectedNames && names.distinct().size == names.size) {
                    "Testiarium discovery must match its exact once-only fixture inventory: expected $expectedNames, got $names ($file)"
                }
            }
            if (project.path.startsWith(":testiarium:") && reports.getValue(name) == "client-gametest.xml") {
                val testCases = document.getElementsByTagName("testcase")
                check(cases == 1 && skipped == 0 && (testCases.item(0) as org.w3c.dom.Element).getAttribute("name") == "StandaloneGameTests.clientFixture") {
                    "Expected exactly one passing Testiarium client fixture: $file"
                }
            }
            logger.lifecycle("Verified GameTest report: {} tests, {} skipped ({})", cases, skipped, file)
        }
    }
}
