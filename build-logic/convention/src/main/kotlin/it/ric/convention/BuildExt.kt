package it.ric.convention

import com.android.build.api.dsl.ApplicationExtension
import git.semver.plugin.gradle.GitSemverPluginExtension
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.testing.Test
import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.gradle.kotlin.dsl.getByType
import org.jetbrains.kotlin.gradle.dsl.HasConfigurableKotlinCompilerOptions
import org.jetbrains.kotlin.gradle.dsl.KotlinBaseExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion
import java.util.Properties

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

val GitSemverPluginExtension.versionCode: Int
    get() = (semVersion.major * 10000) + (semVersion.minor * 100) + (semVersion.patch * 10) + semVersion.commitCount

val Project.localProperties: Properties
    get() {
        val props = Properties()
        val localFile = rootProject.file("local.properties")
        if (localFile.exists()) {
            localFile.inputStream().use { props.load(it) }
        }
        return props
    }

fun Project.findLocalOrProjectProperty(key: String): String? {
    return localProperties.getProperty(key) ?: (project.findProperty(key) as String?)
}

fun Project.setupReleaseSigning(android: ApplicationExtension) {
    val storeFilePath =
        findLocalOrProjectProperty("RELEASE_STORE_FILE")
            ?: findLocalOrProjectProperty("releaseStoreFile")
    if (!storeFilePath.isNullOrEmpty()) {
        val keyFile =
            file(storeFilePath).takeIf { it.exists() }
                ?: rootProject.file(storeFilePath).takeIf { it.exists() }
        if (keyFile != null) {
            android.signingConfigs.create("release") {
                storeFile = keyFile
                storePassword =
                    findLocalOrProjectProperty("RELEASE_STORE_PASSWORD")
                        ?: findLocalOrProjectProperty("releaseStorePassword")
                keyAlias =
                    findLocalOrProjectProperty("RELEASE_KEY_ALIAS")
                        ?: findLocalOrProjectProperty("releaseKeyAlias")
                keyPassword =
                    findLocalOrProjectProperty("RELEASE_KEY_PASSWORD")
                        ?: findLocalOrProjectProperty("releaseKeyPassword")
            }
        }
    }
}

fun Project.setupTestLogging() {
    tasks.withType(Test::class.java).configureEach {
        useJUnitPlatform()
        testLogging {
            events(TestLogEvent.PASSED, TestLogEvent.SKIPPED, TestLogEvent.FAILED)
            exceptionFormat = TestExceptionFormat.SHORT
            showStandardStreams = false
        }
    }
}
