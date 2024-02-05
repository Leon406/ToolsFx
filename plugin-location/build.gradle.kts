group = "me.leon.toolsfx"
version = "1.3.1"

plugins {
    `java-library`
}

javafx {
    // latest version https://mvnrepository.com/artifact/org.openjfx/javafx-controls
    version = libs.versions.jfxVer.get()
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
    testImplementation(libs.kotlin.test)
}
