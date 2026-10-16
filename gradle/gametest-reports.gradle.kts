import javax.xml.parsers.DocumentBuilderFactory

// Some Minecraft startup failures exit zero. Require fresh, non-empty test evidence.
subprojects {
    val reports = when {
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
            logger.lifecycle("Verified GameTest report: {} tests, {} skipped ({})", cases, skipped, file)
        }
    }
}
