package example

import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import java.io.File


abstract class FileProcessingTask : DefaultTask() {

    @get:Input
    abstract val processing: Property<String>

    @get:InputFiles
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
            writeText(inputFile.readText().reversed())
        }
    }
}