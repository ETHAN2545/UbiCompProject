import java.util.Properties
import java.io.FileInputStream

plugins {
    alias(libs.plugins.android.application)

    id("com.google.gms.google-services")
}

val localProperties = Properties()

val localPropertiesFile = rootProject.file("local.properties")

if (localPropertiesFile.exists()) {
    FileInputStream(localPropertiesFile).use { input ->
        localProperties.load(input)
    }
}

android {
    namespace = "com.example.dublin8community"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "com.example.dublin8community"
        minSdk = 24
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        manifestPlaceholders["MAPS_API_KEY"] =
            localProperties.getProperty("MAPS_API_KEY", "")
    }

    buildTypes {
        release {
            optimization {
                enable = false
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(libs.activity.ktx)
    implementation(libs.appcompat)
    implementation(libs.constraintlayout)
    implementation(libs.material)

    implementation(platform("com.google.firebase:firebase-bom:35.1.0"))
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-database")
    implementation("com.google.android.gms:play-services-maps:20.1.0")

    testImplementation(libs.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(libs.ext.junit)
}