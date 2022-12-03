plugins {
    kotlin("jvm") version "1.7.22"
    // https://github.com/diffplug/spotless/blob/main/plugin-gradle/CHANGES.md
    id("com.diffplug.spotless") version "6.12.0"
    // https://detekt.dev/changelog/
    id("io.gitlab.arturbosch.detekt") version "1.22.0"
    id("org.openjfx.javafxplugin") version "0.0.13"
}

apply(from = "${rootProject.projectDir}/config/Versions.gradle.kts")

subprojects {
    apply(from = "${rootProject.projectDir}/config/codeQuality.gradle")

    apply(plugin = "org.openjfx.javafxplugin")
    apply(plugin = "org.jetbrains.kotlin.jvm")
    dependencies {
        detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:1.22.0")
    }
    tasks.withType<JavaCompile> {
        options.encoding = "UTF-8"
    }
}

val hook = File("${rootProject.projectDir}/.git/hooks/pre-commit")

hook.writeBytes(
    """
        #!/bin/bash
        echo "run code format"
        ./gradlew spotlessCheck
        echo "run code smell check"
        ./gradlew detekt
    """.trimIndent().toByteArray()
)
