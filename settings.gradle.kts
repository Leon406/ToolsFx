pluginManagement {
    repositories {
        mavenLocal()
        maven { url = uri("https://maven.aliyun.com/repository/gradle-plugin") }
        maven { url = uri("https://mirrors.tencent.com/nexus/repository/maven-public") }
        maven { url = uri("https://mirrors.huaweicloud.com/repository/maven") }
        maven { url = uri("https://maven.aliyun.com/repository/public") }
        maven { url = uri("https://maven.aliyun.com/repository/google") }
        gradlePluginPortal()
    }
}

rootProject.name = "ToolsFx"
include(
"app",
"plugin-lib",
"plugin-sample",
"plugin-compress",
"plugin-apipost",
"plugin-location",
)
include("benchmark")
