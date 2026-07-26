package it.ric.convention

import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.api.provider.Provider
import org.gradle.kotlin.dsl.getByType
import org.jetbrains.kotlin.gradle.dsl.HasConfigurableKotlinCompilerOptions
import org.jetbrains.kotlin.gradle.dsl.KotlinBaseExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion

val Project.libs: VersionCatalog
    get() = rootProject.extensions.getByType<VersionCatalogsExtension>().named("libs")

fun KotlinBaseExtension.setupKotlin(
    kotlinVersionString: String,
    javaVersion: Int,
) {
    jvmToolchain(javaVersion)
    if (this is HasConfigurableKotlinCompilerOptions<*>) {
        val kotlinMajorVersion = kotlinVersionString.split('.').take(2).joinToString(".")
        val kotlinVersion = KotlinVersion.fromVersion(kotlinMajorVersion)
        compilerOptions {
            apiVersion.set(kotlinVersion)
            languageVersion.set(kotlinVersion)
            progressiveMode.set(true)
        }
    }
}

fun Provider<String>.getAsInt() = get().toInt()
