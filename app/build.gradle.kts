import javassist.ClassPool
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.time.LocalDate


group = "me.leon.tools"
version = "1.15.1.beta"

plugins {
    application
    kotlin("plugin.serialization") version "1.7.21"
}

buildscript {
    dependencies {
        classpath("org.javassist:javassist:3.29.2-GA")
    }
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

fun modifyField(field: javassist.CtField, src: String) {
    val ctClass = field.declaringClass
    ctClass.removeField(field)
    ctClass.addField(javassist.CtField.make(src, ctClass))
}

tasks.register("jarLatest") {
    dependsOn(tasks.withType<KotlinCompile>())
    doLast {
        val file = File(buildDir, "classes/kotlin/main")
        val pool = ClassPool.getDefault()
        pool.insertClassPath(file.absolutePath)

        val ctClass = pool.get("me.leon.ConfigKt")
        if (ctClass.isFrozen) {
            ctClass.defrost()
        }

        var versionField = ctClass.getDeclaredField("VERSION")
        var dateField = ctClass.getDeclaredField("BUILD_DATE")
        println("old: version= ${versionField.constantValue} date= ${dateField.constantValue}")
        val currentDate = LocalDate.now().toString()
        var isModified = false

        if (currentDate != dateField.constantValue) {
            isModified = true
            modifyField(
                dateField,
                "public static final java.lang.String BUILD_DATE =\"$currentDate\";"
            )
            dateField = ctClass.getDeclaredField("BUILD_DATE")
        }
        if (version != versionField.constantValue) {
            isModified = true
            modifyField(versionField, "public static final java.lang.String VERSION =\"$version\";")
            versionField = ctClass.getDeclaredField("VERSION")
        }

        println("new: isModify = $isModified version= ${versionField.constantValue} date= ${dateField.constantValue}")
        if (isModified) {
            ctClass.writeFile(file.absolutePath)
        }
    }
}

tasks.withType<Jar>().forEach {
    it.dependsOn(tasks["jarLatest"])
}

dependencies {
    implementation(project(":plugin-lib"))
    implementation("org.bouncycastle:bcprov-jdk18on:${rootProject.extra["bouncycastle_version"]}")
    implementation("com.google.zxing:javase:${rootProject.extra["zxing_version"]}")
    api("com.google.code.gson:gson:2.10")
    implementation("org.openjdk.nashorn:nashorn-core:15.4")

    testImplementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.1")
//    testImplementation("org.springframework.security:spring-security-web:5.6.2")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit")
    testImplementation("org.mockito:mockito-core:4.8.0")
    testImplementation("org:jaudiotagger:2.0.3")
    testImplementation("org.javassist:javassist:3.29.2-GA")
}
