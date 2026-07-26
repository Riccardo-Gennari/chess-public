import it.ric.convention.BuildParams
import it.ric.convention.getAsInt
import it.ric.convention.setupKotlin
import java.util.Properties

val localProperties = Properties().apply {
    val localFile = rootProject.file("local.properties")
    if (localFile.exists()) {
        localFile.inputStream().use { load(it) }
    }
}

plugins {
    id("it.ric.convention")
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.googleServices)
    alias(libs.plugins.hilt)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.semanticVersioning)
}

kotlin {
    setupKotlin(
        kotlinVersionString = libs.versions.kotlin.get(),
        javaVersion = libs.versions.jvmTarget.getAsInt(),
    )
}

dependencies {
    implementation(libs.androidx.activity.compose)
    implementation(libs.bundles.compose)
    implementation(libs.bundles.firebase)
    implementation(libs.bundles.hilt)
    implementation(libs.compose.uiToolingPreview)
    implementation(libs.datastore.preferences)
    implementation(libs.kotlinx.serialization.core)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.playAuth)
    implementation(libs.playGames)
    implementation(platform(libs.firebase.bom))

    debugImplementation(libs.compose.uiTooling)

    ksp(libs.hilt.compiler)

    testImplementation(libs.bundles.unitTest)
}

android {
    namespace = BuildParams.PACKAGE_NAME
    compileSdk =
        libs.versions.android.compileSdk
            .getAsInt()

    defaultConfig {
        applicationId = BuildParams.PACKAGE_NAME
        manifestPlaceholders["appName"] = BuildParams.APP_NAME
        minSdk =
            libs.versions.android.minSdk
                .getAsInt()
        targetSdk =
            libs.versions.android.targetSdk
                .getAsInt()
        versionCode = semver.semVersion.commitCount
        versionName = semver.version

        buildConfigField("String", "APP_NAME", "\"${BuildParams.APP_NAME}\"")
        buildConfigField("String", "WEB_CLIENT_ID", "\"${localProperties.getProperty("WEB_CLIENT_ID") ?: ""}\"")
    }

    compileOptions {
        sourceCompatibility = JavaVersion.toVersion(libs.versions.jvmTarget.get())
        targetCompatibility = JavaVersion.toVersion(libs.versions.jvmTarget.get())
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
    }
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}
