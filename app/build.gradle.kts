import javassist.ClassPool
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.time.LocalDate


group = "me.leon.tools"
version = "1.16.1.beta"

plugins {
    application
    kotlin("plugin.serialization") version libs.versions.kotlinVer.get()
}

buildscript {
    dependencies {
        classpath(libs.javassist)
    }
}
repositories {
    mavenCentral()
}

javafx {

    // latest version https://mvnrepository.com/artifact/org.openjfx/javafx-controls
    version = libs.versions.jfxVer.get()
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
    api(libs.bouncycastle)
    implementation(libs.zxing)
    api(libs.gson)
    implementation(libs.nashron)
    implementation(libs.kotlinx.coroutines)

    testImplementation(libs.kotlin.serialization)
    testImplementation(libs.kotlin.test)
    testImplementation(libs.mockito)
    testImplementation(libs.javassist)
//    testImplementation("org.springframework.security:spring-security-web:5.6.2")
    testImplementation("org:jaudiotagger:2.0.3")
    // https://www.atilika.org/
    testImplementation("com.atilika.kuromoji:kuromoji-ipadic:+")
}
