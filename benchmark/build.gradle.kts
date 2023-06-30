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
    testImplementation("tokyo.northside:mdict4j:0.5.3")
}
