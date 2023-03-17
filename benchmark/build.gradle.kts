group = "me.leon.benchmark"
version = "1.0.0"

plugins {
    `java-library`
    id("kotlin-kapt")
}

dependencies {
    implementation("androidx.collection:collection-ktx:1.2.0")
    implementation("org.openjdk.jol:jol-core:0.17")
    implementation("org.openjdk.jmh:jmh-core:1.36")
    implementation("cn.hutool:hutool-all:5.8.15")
    kapt("org.openjdk.jmh:jmh-generator-annprocess:1.36")
    implementation(project(":app"))
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit")
}
