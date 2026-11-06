import java.util.Properties
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

fun getReleaseSigningConfig(): File {
    return File(".sign/dsu_sideloader.prop")
}

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.kapt")
    id("com.google.dagger.hilt.android")
    id("com.mikepenz.aboutlibraries.plugin")
    id("org.jetbrains.kotlin.plugin.serialization")
    id("org.jetbrains.kotlin.plugin.compose")
    id("org.jmailen.kotlinter")
}

android {
    val versionCode: Int by rootProject.extra
    val versionName: String by rootProject.extra
    val packageName: String by rootProject.extra

    namespace = packageName
    compileSdkVersion("android-37.0")

    defaultConfig {
        this.applicationId = packageName
        this.versionCode = versionCode
        this.versionName = versionName

        minSdk = 29
        targetSdk = 33
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    signingConfigs {
        val releaseSigningConfig = getReleaseSigningConfig()
        if (releaseSigningConfig.exists()) {
            create("release") {
                /**
                 * .sign/dsu_sideloader.prop
                 *
                 * keystore=some/path/keystore.jks
                 * keystore_pw=keystore_password
                 * alias=alias
                 * alias_pw=alias_password
                 *
                 */
                val props = Properties()
                props.load(releaseSigningConfig.inputStream())

                storeFile = File(props.getProperty("keystore"))
                storePassword = props.getProperty("keystore_pw")
                keyAlias = props.getProperty("alias")
                keyPassword = props.getProperty("alias_pw")
            }
        }
    }

    buildTypes {
        getByName("release") {
            if (getReleaseSigningConfig().exists()) {
                signingConfig = signingConfigs.getByName("release")
            }
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        create("miniDebug") {
            signingConfig = signingConfigs.getByName("debug")
            isDebuggable = true
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
    lint {
        baseline = file("lint-baseline.xml")
    }
    buildFeatures {
        aidl = true
        buildConfig = true
        compose = true
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

aboutLibraries {
    // Remove the "generated" timestamp to allow for reproducible builds
    excludeFields = arrayOf("generated")
}

kotlin.sourceSets.all {
    languageSettings.optIn("kotlin.RequiresOptIn")
}

dependencies {
    testImplementation("junit:junit:4.13.2")
    implementation("androidx.appcompat:appcompat:1.7.1")
    implementation("androidx.datastore:datastore-preferences:1.2.0")
    implementation("androidx.activity:activity-compose:1.13.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.11.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.11.0")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.11.0")
    implementation("androidx.navigation:navigation-compose:2.9.0")
    implementation(platform("androidx.compose:compose-bom:2026.09.00"))
    implementation("androidx.compose.material3:material3:1.5.0-alpha28")
    implementation("androidx.compose.material:material")
    implementation("androidx.compose.runtime:runtime-livedata")
    implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.core:core-ktx:1.15.0")
    implementation("androidx.fragment:fragment-ktx:1.8.6")
    implementation("androidx.preference:preference-ktx:1.2.1")
    implementation("com.google.dagger:hilt-android:2.60.1")
    implementation("androidx.hilt:hilt-navigation-compose:1.3.0")
    kapt("com.google.dagger:hilt-compiler:2.60.1")
    implementation("com.google.android.material:material:1.12.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.11.0")
    implementation("com.github.topjohnwu.libsu:core:5.0.5")
    implementation("com.github.topjohnwu.libsu:service:5.0.5")
    implementation("org.tukaani:xz:1.9")
    implementation("org.apache.commons:commons-compress:1.23.0")
    implementation("com.mikepenz:aboutlibraries-core:10.6.2")
    implementation("dev.rikka.shizuku:api:13.1.1")
    implementation("dev.rikka.shizuku:provider:13.1.1")
    implementation("org.lsposed.hiddenapibypass:hiddenapibypass:4.3")
    implementation("top.yukonga.miuix.kmp:miuix-ui-android:0.9.4")
    implementation("top.yukonga.miuix.kmp:miuix-preference-android:0.9.4")
    implementation("com.materialkolor:material-kolor-android:5.0.1")
    compileOnly(project(":hidden-api-stub"))
}

kotlin {
    jvmToolchain(21)
    compilerOptions { jvmTarget.set(JvmTarget.JVM_21) }
}

kapt { correctErrorTypes = true }
