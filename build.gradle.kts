
plugins {
    kotlin("jvm") version "1.7.0"
    id("com.diffplug.spotless") version "6.7.0"
    id("io.gitlab.arturbosch.detekt") version "1.20.0"
    id("org.openjfx.javafxplugin") version "0.0.13"
}

repositories {
    mavenCentral()
    maven { url = uri("https://maven.aliyun.com/repository/public") }
    maven { url = uri("https://maven.aliyun.com/repository/gradle-plugin") }
    maven { url = uri("https://maven.aliyun.com/repository/google") }
}

apply(from = "${rootProject.projectDir}/config/Versions.gradle.kts")

subprojects {
    apply(from = "${rootProject.projectDir}/config/codeQuality.gradle")

    apply(plugin = "org.openjfx.javafxplugin")
    apply(plugin = "org.jetbrains.kotlin.jvm")
    repositories {
        mavenCentral()
        maven { url = uri("https://maven.aliyun.com/repository/public") }
        maven { url = uri("https://maven.aliyun.com/repository/gradle-plugin") }
        maven { url = uri("https://maven.aliyun.com/repository/google") }
    }
    dependencies {
        detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:1.20.0")
    }
}

val hook = File("${rootProject.projectDir}/.git/hooks/pre-commit")
hook.writeBytes(
    """#!/bin/bash
echo "run code format"
./gradlew spotlessCheck
echo "run code smell check"
./gradlew detekt
""".toByteArray()
)
