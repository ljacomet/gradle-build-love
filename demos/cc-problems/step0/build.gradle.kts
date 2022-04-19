tasks.register("someTask") {
    val destination = System.getProperty("someDestination") 
    inputs.dir("source")
    outputs.dir(destination)
    doLast {
        project.copy { 
            from("source")
            into(destination)
        }
    }
}

