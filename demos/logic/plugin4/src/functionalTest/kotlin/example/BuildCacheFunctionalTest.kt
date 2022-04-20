package example

import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File
import kotlin.test.assertEquals

class BuildCacheFunctionalTest {

    @get:Rule
    val tempFolder = TemporaryFolder()

    private val rootDir: File
        get() = tempFolder.root

    @Test
    fun `relocatable cached outputs`() {
        val gradleUserHome = rootDir.resolve("guh")
        val firstLocation = rootDir.resolve("first-location").apply {
            mkdirs()
            resolve("settings.gradle").writeText("")
            resolve("build.gradle").writeText(
                """
                plugins {
                    id('file-processing')
                }
                
                fileProcessing {
                    processing = "some"
                }
                """.trimIndent()
            )
            resolve("input").run {
                mkdirs()
                resolve("file1.txt").writeText("1")
                resolve("file2.txt").writeText("2")
            }
        }
        val secondLocation = rootDir.resolve("secondLocation")
        firstLocation.copyRecursively(secondLocation)

        GradleRunner.create()
            .forwardOutput()
            .withPluginClasspath()
            .withProjectDir(firstLocation)
            .withArguments(
                "--gradle-user-home=$gradleUserHome",
                "--build-cache",
                "processFiles"
            ).build().let { result ->
                assertEquals(TaskOutcome.SUCCESS, result.task(":processFiles")?.outcome)
            }

        GradleRunner.create()
            .forwardOutput()
            .withPluginClasspath()
            .withProjectDir(secondLocation)
            .withArguments(
                "--gradle-user-home=$gradleUserHome",
                "--build-cache",
                "processFiles"
            ).build().let { result ->
                assertEquals(TaskOutcome.FROM_CACHE, result.task(":processFiles")?.outcome)
            }
    }
}
