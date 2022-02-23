group = "me.leon.toolsfx"
version = "1.0.1"

plugins {
    `java-library`
}

javafx {
    //latest version https://mvnrepository.com/artifact/org.openjfx/javafx-controls
    version = rootProject.extra["jfx_version"] as String
    modules = listOf(
        "javafx.controls",
        "javafx.swing",
        "javafx.web",
//            if you use javafx.fxml,then uncomment it
//            'javafx.fxml'
    )
}

dependencies {
    implementation(project(":plugin-lib"))
    implementation(project(":app"))
    //compress dependencies
    implementation("org.apache.commons:commons-compress:1.21")
    implementation("com.github.luben:zstd-jni:1.5.2-1")
    implementation("org.objectweb.asm:com.springsource.org.objectweb.asm:3.2.0")
    implementation("org.tukaani:xz:1.9")
    implementation("org.brotli:dec:0.1.2")
    runtimeOnly("org.jetbrains.kotlin:kotlin-reflect:${rootProject.extra["kotlin_version"]}")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:${rootProject.extra["kotlin_version"]}")
}
