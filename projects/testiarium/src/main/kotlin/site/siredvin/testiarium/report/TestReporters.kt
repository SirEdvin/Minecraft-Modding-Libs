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
    private val results = mutableListOf<TestResult>()
    private var successful = 0
    private var failed = 0
    private var skipped = 0
    private var duration = 0L
    private var finished = false

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
        results += TestResult(test, if (test.isRequired) Status.FAILED else Status.SKIPPED, error)
        testCase(test).appendChild(result)
    }

    override fun onTestSuccess(test: GameTestInfo) {
        successful++
        duration += test.runTime
        results += TestResult(test, Status.PASSED)
        testCase(test)
    }

    override fun finish() {
        if (finished) return
        finished = true
        val total = successful + failed + skipped
        suite.setAttribute("tests", total.toString())
        suite.setAttribute("failures", failed.toString())
        suite.setAttribute("skipped", skipped.toString())
        suite.setAttribute("time", (duration.toDouble() / 1000).toString())
        destination.parentFile?.toPath()?.let(Files::createDirectories)
        TransformerFactory.newInstance().newTransformer().transform(DOMSource(document), StreamResult(destination))
        val htmlDestination = destination.toPath().resolveSibling("${destination.nameWithoutExtension}.html").toFile()
        htmlDestination.writeText(html(total))
        println(
            "JUnit report: $total tests, $successful passed, $failed failed, $skipped skipped\n" +
                "  XML: ${destination.absolutePath}\n" +
                "  HTML: ${htmlDestination.absolutePath}",
        )
    }

    private fun testCase(test: GameTestInfo): Element = document.createElement("testcase").also {
        it.setAttribute("name", test.testName)
        it.setAttribute("classname", test.structureName)
        it.setAttribute("time", (test.runTime.toDouble() / 1000).toString())
        suite.appendChild(it)
    }

    private fun html(total: Int) = """
        <!doctype html>
        <html lang="en">
        <head>
            <meta charset="utf-8">
            <meta name="viewport" content="width=device-width, initial-scale=1">
            <title>GameTest report</title>
            <style>
                :root { color-scheme: dark; font-family: Georgia, serif; background: #121711; color: #edf1e5; }
                body { max-width: 960px; margin: 0 auto; padding: 4rem 1.5rem; }
                header { border-bottom: 4px solid #ccdc58; padding-bottom: 2rem; }
                h1 { font-size: clamp(2.5rem, 8vw, 5rem); letter-spacing: -.06em; margin: 0; }
                .summary { display: flex; gap: 1px; margin: 2rem 0; background: #40503a; }
                .summary div { flex: 1; padding: 1rem; background: #1d251b; }
                .summary strong { display: block; font: 2rem/1 ui-monospace, monospace; }
                .passed { color: #ccdc58; } .failed { color: #ff8e72; } .skipped { color: #b8c0ac; }
                table { width: 100%; border-collapse: collapse; background: #1d251b; }
                th, td { padding: 1rem; border-bottom: 1px solid #40503a; text-align: left; }
                th { color: #b8c0ac; font: .75rem/1 ui-monospace, monospace; letter-spacing: .12em; text-transform: uppercase; }
                td { font-size: 1.05rem; } .status { font: .75rem/1 ui-monospace, monospace; letter-spacing: .08em; }
                details { margin-top: .75rem; } pre { overflow: auto; padding: 1rem; background: #121711; color: #ffb4a1; }
                @media (max-width: 600px) { body { padding: 2rem 1rem; } .summary { display: grid; grid-template-columns: 1fr 1fr; } th, td { padding: .75rem; } }
            </style>
        </head>
        <body>
            <header><p>TESTIARIUM / GAMETEST</p><h1>Test report</h1><p>${duration / 1000.0} seconds total</p></header>
            <section class="summary">
                <div><strong>$total</strong>tests</div>
                <div class="passed"><strong>$successful</strong>passed</div>
                <div class="failed"><strong>$failed</strong>failed</div>
                <div class="skipped"><strong>$skipped</strong>skipped</div>
            </section>
            <table><thead><tr><th>Test</th><th>Duration</th><th>Status</th></tr></thead><tbody>
                ${results.joinToString("\n") { result -> result.html() }}
            </tbody></table>
        </body>
        </html>
    """.trimIndent()

    private fun TestResult.html() = """
        <tr>
            <td>${test.testName.escapeHtml()}${error?.let { "<details><summary>${it.message.orEmpty().escapeHtml()}</summary><pre>${it.stackTraceToString().escapeHtml()}</pre></details>" }.orEmpty()}</td>
            <td>${test.runTime / 1000.0}s</td>
            <td class="status ${status.name.lowercase()}">${status.name}</td>
        </tr>
    """.trimIndent()

    private fun String.escapeHtml() = replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;")

    private data class TestResult(val test: GameTestInfo, val status: Status, val error: Throwable? = null)

    private enum class Status { PASSED, FAILED, SKIPPED }
}
