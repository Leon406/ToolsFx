plugins {
    kotlin("jvm") version libs.versions.kotlinVer.get()
    alias(libs.plugins.detekt)
    alias(libs.plugins.javafx)
    alias(libs.plugins.spotless)
}

val jvmTarget = "1.8"

subprojects {
    apply(from = "${rootProject.projectDir}/config/codeQuality.gradle")
    apply(plugin = "org.openjfx.javafxplugin")
    apply(plugin = "org.jetbrains.kotlin.jvm")

    dependencies {
        detektPlugins(rootProject.libs.detekt.formatting)
    }
    tasks.withType<JavaCompile> {
        options.encoding = "UTF-8"
        targetCompatibility = jvmTarget
        sourceCompatibility = jvmTarget
    }
    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions.jvmTarget = jvmTarget
    }
}

val hook = File("${rootProject.projectDir}/.git/hooks/pre-commit")
hook.parentFile.mkdirs()
hook.writeBytes(
    """
        #!/bin/bash
        echo "run code format"
        ./gradlew spotlessCheck
        echo "run code smell check"
        ./gradlew detekt
    """.trimIndent().toByteArray()
)
