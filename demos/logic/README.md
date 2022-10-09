plugin -> no incremental behaviour; fails with missing output
 - remedy by adding logging output to task action

plugin2 -> task is marked incremental/cacheable but not tested

plugin3 -> IBFT#'incremental build' fails because inputdir is not created on execution
 - Task input is @Incremental
 - Show adding `-is` to GradleRunner arguments in IBFT
 - Show adding withDebug(true) to debug the test in IBFT
 - Remedy by adding `setOf(FileType.DIRECTORY, FileType.MISSING).contains(change.fileType)` to FPT

plugin4 -> Same as plugin3 but uses worker.
 - Remedy by adding `setOf(FileType.DIRECTORY, FileType.MISSING).contains(change.fileType)` to FPT
 - Show Worker setup in TaskAction
 - Show classLoaderIsolation(clws -> ... ) with a classpath/configuration

