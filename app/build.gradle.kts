plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
}

import org.jetbrains.kotlin.gradle.dsl.JvmTarget

        kotlin {
            compilerOptions {
                jvmTarget.set(JvmTarget.JVM_17)
            }
        }

android {
    namespace = "com.example.bebetterapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.bebetterapp"
        minSdk = 29
        targetSdk = 35
        versionCode = 1
        versionName = "0.1"
    }

    buildTypes {
        release { isMinifyEnabled = false }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures { compose = true }
}

dependencies {
    // AndroidX Core фиксируем явно (пока AGP 8.8 / compileSdk 35)
    implementation(libs.androidx.core)
    implementation(libs.androidx.core.ktx)

    // Compose
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")
    debugImplementation("androidx.compose.ui:ui-tooling")
    implementation("androidx.compose.material3:material3")

    // Lifecycle
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)

    // Room (KSP)
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)

    // Work / Coroutines
    implementation(libs.coroutines.android)
    implementation(libs.work.runtime.ktx)

    // ВРЕМЕННО ВЫКЛЮЧИ Vico, чтобы вернуть сборку:
    // implementation(libs.vico.compose.m3)
}