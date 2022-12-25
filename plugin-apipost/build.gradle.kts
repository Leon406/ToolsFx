group = "me.leon.toolsfx"
version = "1.6.1"

plugins {
    `java-library`
}

javafx {
    // latest version https://mvnrepository.com/artifact/org.openjfx/javafx-controls
    version = rootProject.extra["jfx_version"] as String
    modules = listOf(
        "javafx.controls",
        "javafx.swing",
        "javafx.web",
        // if you use javafx.fxml,then uncomment it
        // "javafx.fxml"
    )
}

dependencies {
    implementation(project(":plugin-lib"))
    implementation(project(":app"))

    testImplementation("org.xerial:sqlite-jdbc:3.39.3.0")
    testImplementation("net.java.dev.jna:jna:5.12.1")
    testImplementation("net.java.dev.jna:jna-platform:5.12.1")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit")
}
