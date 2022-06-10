group = "me.leon.toolsfx"
version = "1.4.3"

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

    testImplementation("org.xerial:sqlite-jdbc:3.36.0.3")
    testImplementation("net.java.dev.jna:jna:5.11.0")
    testImplementation("net.java.dev.jna:jna-platform:5.11.0")
    runtimeOnly("org.jetbrains.kotlin:kotlin-reflect")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit")
}
