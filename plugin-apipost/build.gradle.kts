group = "me.leon.toolsfx"
version = "1.9.2"

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

    testImplementation(libs.jna.platform)
    testImplementation(libs.kotlinx.coroutines)
    testImplementation(libs.kotlin.test)
    testImplementation(libs.selenium)
    testImplementation("com.squareup.okhttp3:okhttp-sse:4.12.0")
    testImplementation("io.github.sashirestela:simple-openai:3.18.0")
}
