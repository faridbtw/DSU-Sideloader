plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "vegabobo.dsusideloader"
    compileSdkVersion("android-37.0")

    defaultConfig {
        minSdk = 29
    }
    buildTypes {
        create("miniDebug"){}
    }
}

dependencies {
    implementation("dev.rikka.shizuku:api:13.1.1")
    implementation("dev.rikka.shizuku:provider:13.1.1")
}