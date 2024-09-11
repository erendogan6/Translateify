buildscript {
    repositories {
        google()
    }
    dependencies {
        classpath(libs.androidx.navigation.safe.args.gradle.plugin)
    }
}
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.jetbrains.kotlin.android) apply false
    id("com.google.dagger.hilt.android") version libs.versions.hiltAndroidGradlePlugin apply false
    id("org.jetbrains.kotlinx.kover") version "0.9.0-RC"
}
