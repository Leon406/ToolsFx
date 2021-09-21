group = "me.leon.toolsfx"
version = "1.8.0"

plugins {
    application
}
val tornadofx_version: String by rootProject
javafx {
    //latest version https://mvnrepository.com/artifact/org.openjfx/javafx-controls
    version = "17"
    modules = listOf(
        "javafx.controls",
        "javafx.swing",
        "javafx.web",
//            if you use javafx.fxml,then uncomment it
//            'javafx.fxml'
    )
}
application {
    mainClass.set("me.leon.MainKt")
}
dependencies {
    implementation("no.tornado:tornadofx:$tornadofx_version")
    implementation("org.bouncycastle:bcprov-jdk15on:1.69")
    runtimeOnly("org.jetbrains.kotlin:kotlin-reflect:1.5.31")
    implementation("org.glassfish:javax.json:1.1.4")
    implementation("com.google.zxing:javase:3.4.1")
    implementation(project(":plugin-lib"))
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:1.5.31")
}