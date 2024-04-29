group = "me.leon.toolsfx"
version = "1.2.1"

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
    // compress dependencies
    implementation(libs.commons.compress)
    implementation(libs.zstd)
    implementation(libs.asm)
    implementation("org.tukaani:xz:1.9")
    implementation("org.brotli:dec:0.1.2")
    testImplementation(libs.kotlin.test)
}
