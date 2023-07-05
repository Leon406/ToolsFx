group = "me.leon.benchmark"
version = "1.0.0"

plugins {
    `java-library`
    id("kotlin-kapt")
}
val jvmTarget = "11"

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
    targetCompatibility = jvmTarget
    sourceCompatibility = jvmTarget
}
tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.jvmTarget = jvmTarget
}
dependencies {
    implementation("androidx.collection:collection-ktx:1.2.0")
    implementation("org.openjdk.jol:jol-core:0.17")
    implementation("org.openjdk.jmh:jmh-core:1.36")
    implementation("cn.hutool:hutool-all:5.8.20")
    kapt("org.openjdk.jmh:jmh-generator-annprocess:1.36")
    implementation(project(":app"))
    testImplementation(libs.kotlin.test)

    testImplementation(libs.jsoup)
    // https://mvnrepository.com/artifact/tokyo.northside/mdict4j
    testImplementation("tokyo.northside:mdict4j:0.5.3") {
        exclude("org.bouncycastle", "bcprov-jdk15on")
    }
    testImplementation("org.java-websocket:Java-WebSocket:1.5.3")
    // mp3格式支持
//    testImplementation("com.googlecode.soundlibs:mp3spi:1.9.5.4")
    // https://mvnrepository.com/artifact/com.tianscar.javasound/javasound-mp3  com.googlecode.soundlib 维护库
    testImplementation("com.tianscar.javasound:javasound-mp3:1.9.8")

    //    https://mvnrepository.com/artifact/com.googlecode.soundlibs/vorbisspi ogg格式支持
    testImplementation("com.googlecode.soundlibs:vorbisspi:1.0.3.3")
    // flac格式
    testImplementation("com.tianscar.javasound:javasound-flac:1.4.1")
    // ape格式
    testImplementation("com.tianscar.javasound:javasound-ape:1.7.7")
    // speex格式  opus
    testImplementation("com.tianscar.javasound:javasound-speex:0.9.8")
    // wav
    testImplementation("com.tianscar.javasound:javasound-wavpack:1.4.2")
    // aac
    testImplementation("com.tianscar.javasound:javasound-aac:0.9.8")
}
