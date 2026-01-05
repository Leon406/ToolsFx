group = "me.leon.benchmark"
version = "1.0.0"

plugins {
    `java-library`
    id("kotlin-kapt")
}

dependencies {
    implementation("androidx.collection:collection-ktx:1.5.0")
    implementation("org.openjdk.jol:jol-core:0.17")
    implementation("org.openjdk.jmh:jmh-core:1.37")
    implementation("cn.hutool:hutool-all:5.8.43")
    kapt("org.openjdk.jmh:jmh-generator-annprocess:1.37")
    implementation(project(":app"))

    testImplementation(libs.kotlin.test)
    testImplementation(libs.jsoup)
    testImplementation("org.java-websocket:Java-WebSocket:1.6.0")
    // mp3格式支持
    // testImplementation("com.googlecode.soundlibs:mp3spi:1.9.5.4")

    //  refer  https://github.com/Tianscar/javasound-spcollect
    // https://mvnrepository.com/artifact/com.tianscar.javasound/javasound-mp3  com.googlecode.soundlib 维护库
    testImplementation("com.tianscar.javasound:javasound-mp3:1.9.8")
    //    https://mvnrepository.com/artifact/com.googlecode.soundlibs/vorbisspi ogg格式支持
    testImplementation("com.googlecode.soundlibs:vorbisspi:1.0.3.3")
    // flac格式
    testImplementation("com.tianscar.javasound:javasound-flac:1.4.1")
    // ape格式
    testImplementation("com.tianscar.javasound:javasound-ape:1.7.7")
    // aac  某些mp3会误识别为 aac
    // testImplementation("com.tianscar.javasound:javasound-aac:0.9.8")
}
