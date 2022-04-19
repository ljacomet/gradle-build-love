package example

import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.*
import java.io.File


@CacheableTask
abstract class FileProcessingTask : DefaultTask() {

    @get:Input
    abstract val processing: Property<String>

    @get:InputFiles
    @get:SkipWhenEmpty
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val inputDirectory: DirectoryProperty

    @get:OutputDirectory
    abstract val outputDirectory: DirectoryProperty

    @TaskAction
    fun action() {
        val outputDir = outputDirectory.get().asFile
        outputDir.deleteRecursively()
        outputDir.mkdirs()
        val inputDir = inputDirectory.get().asFile
        inputDirectory.get().asFileTree.files.forEach { inputFile ->
            processFile(
                processing = processing.get(),
                inputFile = inputFile,
                outputFile = outputDir.resolve(inputFile.relativeTo(inputDir).path)
            )
        }
    }

    private
    fun processFile(processing: String, inputFile: File, outputFile: File) {
        outputFile.run {
            parentFile.mkdirs()
            writeText(processing)
        }
    }
}