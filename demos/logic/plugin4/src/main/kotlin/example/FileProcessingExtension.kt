package example

import org.gradle.api.provider.Property

interface FileProcessingExtension {

    abstract val processing: Property<String>

}
