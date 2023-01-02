group = "me.leon.toolsfx"
version = "1.1.0"

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
    api("no.tornado:tornadofx:${rootProject.extra["tornadofx_version"]}")
    api("org.jetbrains.kotlin:kotlin-reflect")
    api("org.glassfish:javax.json:${rootProject.extra["javax_json_version"]}")
}
