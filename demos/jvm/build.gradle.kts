
plugins {
    `java-library`
    groovy
    alias(libs.plugins.spotbugs)
    `java-test-fixtures`
    `maven-publish`
}

group = "org.example"
version = "0.1"

repositories {
    mavenCentral()
}

dependencies {
    api(libs.math)
    implementation(libs.guava)
    runtimeOnly(micronaut.logback)

    testFixturesApi(libs.groovy.core)

    testImplementation(libs.spock)
    testImplementation(libs.groovy.core)
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
        vendor.set(JvmVendorSpec.ADOPTIUM)
    }
}

testing {
    suites {
        val integrationTest by registering(JvmTestSuite::class) {
            dependencies {
                implementation(project)
                implementation(project.dependencies.testFixtures(project))
            }
            useJUnitJupiter()
            targets {
                all {
                    testTask.configure {
                        shouldRunAfter(tasks.test)
                    }
                }
            }
        }

        val test by getting(JvmTestSuite::class) {
            useJUnitJupiter()
            targets {
                all {
                    testTask.configure {
                        javaLauncher.set(javaToolchains.launcherFor{
                            languageVersion.set(JavaLanguageVersion.of(17))
                        })
                    }
                }
            }
        }
    }
}

val javaComponent = components["java"] as AdhocComponentWithVariants
javaComponent.withVariantsFromConfiguration(configurations.testFixturesApiElements.get()) {
    skip()
}
javaComponent.withVariantsFromConfiguration(configurations.testFixturesRuntimeElements.get()) {
    skip()
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
            pom {
                description.set("Funky library for Devoxx Fr turning 10!")
            }
        }
    }
    repositories {
        maven {
            url = uri("repository")
        }
    }
}