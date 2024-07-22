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
            
            repositories {
                mavenCentral()
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
            val buildFolder = projectDir.resolve("build")
            val file1 = buildFolder.resolve("output/file1.txt")
            val file2 = buildFolder.resolve("output/file2.txt")
            assertTrue(file1.isFile)
            assertEquals("1", file1.readText())
            assertTrue(file2.isFile)
            assertEquals("2", file2.readText())
        }

        runner.build().let { result ->
            assertEquals(TaskOutcome.UP_TO_DATE, result.task(":processFiles")?.outcome)
        }

        projectDir.resolve("input").run {
            resolve("file3.txt").writeText("3")
        }

        runner.build().let { result ->
            assertEquals(TaskOutcome.SUCCESS, result.task(":processFiles")?.outcome)
            val buildFolder = projectDir.resolve("build")
            val file1 = buildFolder.resolve("output/file1.txt")
            val file2 = buildFolder.resolve("output/file2.txt")
            val file3 = buildFolder.resolve("output/file3.txt")
            assertTrue(file1.isFile)
            assertEquals("1", file1.readText())
            assertTrue(file2.isFile)
            assertEquals("2", file2.readText())
            assertTrue(file3.isFile)
            assertEquals("3", file3.readText())
        }
    }
}
