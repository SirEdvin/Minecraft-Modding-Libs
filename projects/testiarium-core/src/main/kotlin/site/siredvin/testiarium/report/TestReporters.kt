// SPDX-FileCopyrightText: 2022 The CC: Tweaked Developers
// SPDX-FileCopyrightText: 2026 SirEdvin
// SPDX-License-Identifier: MPL-2.0
// Adapted from CC:Tweaked projects/common/src/testMod/kotlin/dan200/computercraft/gametest/core/TestReporters.kt

package site.siredvin.testiarium.report

import net.minecraft.gametest.framework.GameTestInfo
import net.minecraft.gametest.framework.TestReporter
import org.w3c.dom.Element
import java.io.File
import java.io.PrintWriter
import java.io.StringWriter
import java.nio.file.Files
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult

class MultiTestReporter(private vararg val reporters: TestReporter) : TestReporter {
    override fun onTestFailed(test: GameTestInfo) = reporters.forEach { it.onTestFailed(test) }

    override fun onTestSuccess(test: GameTestInfo) = reporters.forEach { it.onTestSuccess(test) }

    override fun finish() = reporters.forEach(TestReporter::finish)
}

class JunitTestReporter(private val destination: File) : TestReporter {
    private val document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument()
    private val suite = document.createElement("testsuite").also { document.appendChild(it) }
    private var successful = 0
    private var failed = 0
    private var skipped = 0
    private var duration = 0L

    override fun onTestFailed(test: GameTestInfo) {
        duration += test.runTime
        val error = requireNotNull(test.error)
        val result = document.createElement(if (test.isRequired) "failure" else "skipped")
        result.setAttribute("message", error.message.orEmpty())
        if (test.isRequired) {
            failed++
            result.setAttribute("type", error.javaClass.name)
            result.textContent = StringWriter().also { error.printStackTrace(PrintWriter(it)) }.toString()
        } else {
            skipped++
        }
        testCase(test).appendChild(result)
    }

    override fun onTestSuccess(test: GameTestInfo) {
        successful++
        duration += test.runTime
        testCase(test)
    }

    override fun finish() {
        val total = successful + failed + skipped
        suite.setAttribute("tests", total.toString())
        suite.setAttribute("failures", failed.toString())
        suite.setAttribute("skipped", skipped.toString())
        suite.setAttribute("time", (duration.toDouble() / 1000).toString())
        destination.parentFile?.toPath()?.let(Files::createDirectories)
        TransformerFactory.newInstance().newTransformer().transform(DOMSource(document), StreamResult(destination))
        println("JUnit report: $total tests, $successful passed, $failed failed, $skipped skipped: ${destination.absolutePath}")
    }

    private fun testCase(test: GameTestInfo): Element = document.createElement("testcase").also {
        it.setAttribute("name", test.testName)
        it.setAttribute("classname", test.structureName)
        it.setAttribute("time", (test.runTime.toDouble() / 1000).toString())
        suite.appendChild(it)
    }
}
