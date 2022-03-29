import org.asciidoctor.gradle.jvm.slides.RevealJSOptions

plugins {
    id("org.asciidoctor.jvm.gems") version "3.3.0"
    id("org.asciidoctor.jvm.revealjs") version "3.3.0"
    id("com.github.salomonbrys.gradle.sass") version "1.2.0"
}

repositories {
    mavenCentral()
    withGroovyBuilder {
        "ruby" {
            "gems"()
        }
    }
}

asciidoctorj {
    fatalWarnings(missingIncludes())
    modules {
        diagram.use()
    }
}

revealjs {
    version = "3.1.0"
    templateGitHub {
        setOrganisation("hakimel")
        setRepository("reveal.js")
        setTag("3.9.1")
    }
}

tasks {
    sassCompile {
        source = layout.projectDirectory.dir("src/style").asFileTree
        outputDir = layout.buildDirectory.dir("style").get().asFile
    }
    asciidoctorRevealJs {
        dependsOn(sassCompile)
        baseDirFollowsSourceDir()
        sourceDirProperty.set(layout.projectDirectory.dir("src/docs/asciidoc"))
        resources {
            from("${sourceDir}/images") {
                include("**")
                into("images")
            }
        }
        revealjsOptions {
            setCustomThemeLocation(sassCompile.map { it.outputDir.resolve("gradle.css") })
            setControls(true)
            setSlideNumber("c/t")
            setProgressBar(true)
            setPushToHistory(true)
            setOverviewMode(true)
            setKeyboardShortcuts(true)
            setTouchMode(true)
            setTransition(RevealJSOptions.Transition.SLIDE)
            setTransitionSpeed(RevealJSOptions.TransitionSpeed.DEFAULT)
            setBackgroundTransition(RevealJSOptions.Transition.FADE)
        }
        attributes(
            mapOf(
                "source-highlighter" to "coderay",
                "coderay-css" to "style",
                "imagesdir" to "./images",
                "icons" to "font",
                "idprefix" to "slide-",
                "docinfo" to "shared",
                // Configurations not available via the `revealjsOptions` block - See https://revealjs.com/config/
                "revealjs_disableLayout" to "true", // Disables the default reveal.js slide layout (scaling and centering) so that you can use custom CSS layout
                "revealjs_controlsLayout" to "edges", // Determines where controls appear, "edges" or "bottom-right"
                "revealjs_autoPlayMedia" to null   // Auto-playing embedded media (video/audio/iframe) - true/false or null: Media will only autoplay if data-autoplay is present

            )
        )
    }
    register("asciidoctor") {
        dependsOn(asciidoctorRevealJs)
    }
}
