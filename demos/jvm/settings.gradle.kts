
rootProject.name = "library"

dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }
    versionCatalogs {
        create("micronaut") {
            from("io.micronaut:micronaut-bom:3.3.1")
        }
    }
}
