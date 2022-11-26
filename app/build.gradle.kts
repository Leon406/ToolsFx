group = "me.leon.tools"
version = "1.15.0"

plugins {
    application
    kotlin("plugin.serialization") version "1.7.21"
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
}
