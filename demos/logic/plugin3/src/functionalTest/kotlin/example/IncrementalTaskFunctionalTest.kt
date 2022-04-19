package example

import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class IncrementalTaskFunctionalTest {

    @get:Rule
    val tempFolder = TemporaryFolder()

    private val projectDir: File
        get() = tempFolder.root

    private val buildFile: File
        get() = projectDir.resolve("build.gradle")

    private val settingsFile: File
        get() = projectDir.resolve("settings.gradle")

    @Test
    fun `incremental task`() {
        settingsFile.writeText("")
        buildFile.writeText(
            """
            plugins {
                id('file-processing')
            }
            
            fileProcessing {
                processing = "some"
            }
            """
        )

        projectDir.resolve("input").run {
            mkdirs()
            resolve("file1.txt").writeText("1")
            resolve("file2.txt").writeText("2")
        }

        val runner = GradleRunner.create()
            .forwardOutput()
            .withPluginClasspath()
            .withProjectDir(projectDir)
            .withArguments("processFiles")

        runner.build().let { result ->
            assertEquals(TaskOutcome.SUCCESS, result.task(":processFiles")?.outcome)
            assertTrue(result.output.contains("/file1.txt"))
            assertTrue(result.output.contains("/file2.txt"))
        }

        runner.build().let { result ->
            assertEquals(TaskOutcome.UP_TO_DATE, result.task(":processFiles")?.outcome)
        }

        projectDir.resolve("input").run {
            resolve("file3.txt").writeText("3")
        }

        runner.build().let { result ->
            assertEquals(TaskOutcome.SUCCESS, result.task(":processFiles")?.outcome)
            assertFalse(result.output.contains("/file1.txt"))
            assertFalse(result.output.contains("/file2.txt"))
            assertTrue(result.output.contains("/file3.txt"))
        }
    }
}
