plugins {
    id("com.android.application") version "9.4.1" apply false
    id("com.android.library") version "9.4.1" apply false
    id("org.jetbrains.kotlin.android") version "2.4.20" apply false
    id("org.jetbrains.kotlin.jvm") version "2.4.20" apply false
    id("org.jetbrains.kotlin.kapt") version "2.4.20" apply false
    id("org.jetbrains.kotlin.plugin.compose") version "2.4.20" apply false
    id("org.jetbrains.kotlin.plugin.serialization") version "2.4.20" apply false
    id("com.google.dagger.hilt.android") version "2.60.1" apply false
    id("com.mikepenz.aboutlibraries.plugin") version "10.6.2" apply false
    id("org.jmailen.kotlinter") version "5.7.0" apply false
}

val versionCode by extra { 24 }
val versionName by extra { "2.04-miuix.11" }
val packageName by extra { "vegabobo.dsusideloader" }

tasks.register<Delete>("clean") {
    delete(layout.buildDirectory)
}
