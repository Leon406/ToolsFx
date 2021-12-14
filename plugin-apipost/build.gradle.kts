group = "me.leon.toolsfx"
version = "1.2.0"

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
//    implementation("no.tornado:tornadofx:$tornadofx_version")
    implementation(project(":plugin-lib"))
    implementation(project(":app"))

    testImplementation ("org.xerial:sqlite-jdbc:3.36.0.3")
    testImplementation ("net.java.dev.jna:jna:5.9.0")
    testImplementation ("net.java.dev.jna:jna-platform:5.9.0")
    runtimeOnly("org.jetbrains.kotlin:kotlin-reflect:${rootProject.extra["kotlin_version"]}")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:${rootProject.extra["kotlin_version"]}")
}