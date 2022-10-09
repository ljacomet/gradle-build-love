import example.FileProcessingExtension
import example.FileProcessingTask

plugins {
    base
}

val fileProcessing = extensions.create<FileProcessingExtension>("fileProcessing")

val guavaCoordinates = "com.google.guava:guava:31.1-jre"

val fooConfiguration = configurations.create("fileProcessingTool").defaultDependencies {
    this.add(project.dependencies.create(guavaCoordinates))
}

tasks.register<FileProcessingTask>("processFiles") {
    processing.set(fileProcessing.processing)
    toolClasspath.from(fooConfiguration)
    inputDirectory.set(layout.projectDirectory.dir("input"))
    outputDirectory.set(layout.buildDirectory.dir("output"))
}
