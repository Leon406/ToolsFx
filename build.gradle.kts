plugins {
    kotlin("jvm") version "1.7.10"
    // https://github.com/diffplug/spotless/blob/main/plugin-gradle/CHANGES.md
    id("com.diffplug.spotless") version "6.8.0"
    id("io.gitlab.arturbosch.detekt") version "1.21.0"
    id("org.openjfx.javafxplugin") version "0.0.13"
}

apply(from = "${rootProject.projectDir}/config/Versions.gradle.kts")

subprojects {
    apply(from = "${rootProject.projectDir}/config/codeQuality.gradle")

    apply(plugin = "org.openjfx.javafxplugin")
    apply(plugin = "org.jetbrains.kotlin.jvm")
    repositories {
        mavenLocal()
//        mavenCentral()
        maven { url = uri("https://mirrors.tencent.com/nexus/repository/maven-public") }
        maven { url = uri("https://mirrors.huaweicloud.com/repository/maven/") }
        maven { url = uri("https://maven.aliyun.com/repository/public") }
        maven { url = uri("https://maven.aliyun.com/repository/gradle-plugin") }
        maven { url = uri("https://maven.aliyun.com/repository/google") }
    }
    dependencies {
        detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:1.21.0")
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
