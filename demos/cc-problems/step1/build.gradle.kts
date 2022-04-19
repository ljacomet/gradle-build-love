abstract class MyCopyTask : DefaultTask() { 

    @get:InputDirectory abstract val source: DirectoryProperty 

    @get:OutputDirectory abstract val destination: DirectoryProperty 

    @get:Inject abstract val fs: FileSystemOperations 

    @TaskAction
    fun action() {
        fs.copy { 
            from(source)
            into(destination)
        }
    }
}

tasks.register<MyCopyTask>("someTask") {
    val projectDir = layout.projectDirectory
    source.set(projectDir.dir("source"))
    destination.set(projectDir.dir(System.getProperty("someDestination")))
}

