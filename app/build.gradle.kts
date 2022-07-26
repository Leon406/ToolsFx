group = "me.leon.tools"
version = "1.14.0"

repositories {
    mavenLocal()
}

plugins {
    application
    kotlin("plugin.serialization") version "1.7.10"
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

dependencies {
    implementation("no.tornado:tornadofx:${rootProject.extra["tornadofx_version"]}")
    implementation("org.bouncycastle:bcprov-jdk18on:${rootProject.extra["bouncycastle_version"]}")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.glassfish:javax.json:${rootProject.extra["javax_json_version"]}")
    implementation("com.google.zxing:javase:${rootProject.extra["zxing_version"]}")
    api("com.google.code.gson:gson:2.9.0")
    implementation(project(":plugin-lib"))
    implementation("org.openjdk.nashorn:nashorn-core:15.4")

    testImplementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.3")
//    testImplementation("org.springframework.security:spring-security-web:5.6.2")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit")
    testImplementation("org.mockito:mockito-core:4.6.1")
}
