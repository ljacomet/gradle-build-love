package example

import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.FileType
import org.gradle.api.provider.Property
import org.gradle.api.tasks.*
import org.gradle.work.ChangeType
import org.gradle.work.Incremental
import org.gradle.work.InputChanges
import java.io.File


@CacheableTask
abstract class FileProcessingTask : DefaultTask() {

    @get:Input
    abstract val processing: Property<String>

    @get:InputFiles
    @get:PathSensitive(PathSensitivity.RELATIVE)
    @get:Incremental
    abstract val inputDirectory: DirectoryProperty

    @get:OutputDirectory
    abstract val outputDirectory: DirectoryProperty

    @TaskAction
    fun action(inputChanges: InputChanges) {

        inputChanges.getFileChanges(inputDirectory).forEach { change ->
            if (setOf(FileType.DIRECTORY, FileType.MISSING).contains(change.fileType)) return@forEach

            val targetFile = outputDirectory.file(change.normalizedPath).get().asFile
            if (change.changeType == ChangeType.REMOVED) {
                targetFile.delete()
            } else {
                processFile(processing.get(), change.file, targetFile)
            }

        }
    }

    private
    fun processFile(processing: String, inputFile: File, outputFile: File) {
        println("processing file '$inputFile'")
        outputFile.run {
            parentFile.mkdirs()
            writeText(inputFile.readText().reversed())
        }
    }
}