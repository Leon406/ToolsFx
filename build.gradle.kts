
plugins {
    kotlin("jvm") version "1.5.30"
    application
    id("com.diffplug.spotless") version "5.15.0"
    id("io.gitlab.arturbosch.detekt") version "1.18.1"
    id("org.openjfx.javafxplugin") version "0.0.10"
}
group = "me.leon.toolsfx"
version = "1.7.3.beta"
val tornadofx_version: String by rootProject

repositories {
    mavenCentral()
    maven { url = uri("https://maven.aliyun.com/repository/public") }
    maven { url = uri("https://maven.aliyun.com/repository/gradle-plugin") }
    maven { url = uri("https://maven.aliyun.com/repository/google") }
}

application {
    mainClass.set("me.leon.MainKt")
}

dependencies {
    implementation("no.tornado:tornadofx:$tornadofx_version")
    implementation("org.bouncycastle:bcprov-jdk15on:1.69")
    runtimeOnly("org.jetbrains.kotlin:kotlin-reflect:1.5.30")
    implementation("org.glassfish:javax.json:1.1.4")
    implementation("com.google.zxing:javase:3.4.1")

    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:1.5.30")
}

javafx {
    version = "18-ea+2"
    modules = listOf(
        "javafx.controls",
        "javafx.swing",
//            if you use javafx.fxml,then uncomment it
//            'javafx.fxml'
    )
}



apply(from = "${rootProject.projectDir}/config/codeQuality.gradle")
val hook = File("${rootProject.projectDir}/.git/hooks/pre-commit")
hook.writeBytes(
    """#!/bin/bash
echo "run code format"
./gradlew spotlessJCh spotlessKCh
echo "run code smell check"
./gradlew detekt
""".toByteArray()
)
