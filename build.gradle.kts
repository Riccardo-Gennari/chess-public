import it.ric.convention.setupQualityChecks

plugins {
    id("it.ric.convention")
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.composeCompiler) apply false
    alias(libs.plugins.googleServices) apply false
    alias(libs.plugins.semanticVersioning) apply true
}

allprojects {
    setupQualityChecks()
}
