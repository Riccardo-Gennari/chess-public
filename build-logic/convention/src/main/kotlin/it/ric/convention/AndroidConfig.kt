package it.ric.convention

import org.gradle.api.JavaVersion

object AndroidConfig {
    const val COMPILE_SDK = 37
    const val MIN_SDK = 32
    const val TARGET_SDK = 37

    val jvmTarget = JavaVersion.VERSION_21
}
