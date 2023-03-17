group = "me.leon.toolsfx"
version = "1.2.0"

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
    // compress dependencies
    implementation("org.apache.commons:commons-compress:1.22")
    implementation("com.github.luben:zstd-jni:1.5.4-2")
    implementation("org.objectweb.asm:com.springsource.org.objectweb.asm:3.2.0")
    implementation("org.tukaani:xz:1.9")
    implementation("org.brotli:dec:0.1.2")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit")
}
