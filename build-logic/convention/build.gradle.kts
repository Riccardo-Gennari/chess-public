plugins {
    `kotlin-dsl`
    alias(libs.plugins.semanticVersioning)
}

group = "it.ric.buildlogic"

dependencies {
    implementation(libs.android.gradlePlugin)
    implementation(libs.kotlin.gradlePlugin)
    implementation(libs.ktlint.gradlePlugin)
}

gradlePlugin {
    plugins {
        register("it.ric.convention") {
            id = "it.ric.convention"
            implementationClass = "it.ric.convention.ConventionPlugin"
        }
    }
}
