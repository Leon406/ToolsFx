group = "me.leon.toolsfx"
version = "1.0.1"

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
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit")
}
