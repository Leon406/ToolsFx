import javassist.ClassPool
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.time.LocalDate


group = "me.leon.tools"
version = "1.16.0"

plugins {
    application
    kotlin("plugin.serialization") version "1.8.10"
}

buildscript {
    dependencies {
        classpath("org.javassist:javassist:3.29.2-GA")
    }
}
repositories {
    mavenCentral()
}

javafx {
    // latest version https://mvnrepository.com/artifact/org.openjfx/javafx-controls
    version = rootProject.extra["jfx_version"] as String
    modules = listOf(
        "javafx.controls",
        "javafx.swing",
        "javafx.web",
        // if you use javafx.fxml,then uncomment it
        // 'javafx.fxml'
    )
}
application {
    mainClass.set("me.leon.MainKt")
}

tasks.register("jarLatest") {
    dependsOn(tasks.withType<KotlinCompile>())
    doLast {
        val file = File(buildDir, "classes/kotlin/main")
        val pool = ClassPool.getDefault()
        pool.insertClassPath(file.absolutePath)

        val ctClass = pool.get("me.leon.ConstantsKt")
        if (ctClass.isFrozen) {
            ctClass.defrost()
        }

        val currentDate = LocalDate.now().toString()
        val dateField = ctClass.getDeclaredMethod("getBuild")
        val versionField = ctClass.getDeclaredMethod("getAppVersion")
        versionField.setBody("{ return \"$version\";}")
        dateField.setBody("{ return \"$currentDate \";}")

        ctClass.writeFile(file.absolutePath)
    }
}

tasks.withType<Jar>().forEach {
    it.dependsOn(tasks["jarLatest"])
}

dependencies {
    implementation(project(":plugin-lib"))
    api("org.bouncycastle:bcprov-jdk18on:${rootProject.extra["bouncycastle_version"]}")
    implementation("com.google.zxing:javase:${rootProject.extra["zxing_version"]}")
    api("com.google.code.gson:gson:2.10.1")
    implementation("org.openjdk.nashorn:nashorn-core:15.4")

    testImplementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.0")
//    testImplementation("org.springframework.security:spring-security-web:5.6.2")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit")
    testImplementation("org.mockito:mockito-core:5.2.0")
    testImplementation("org:jaudiotagger:2.0.3")
    testImplementation("org.javassist:javassist:3.29.2-GA")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
}
