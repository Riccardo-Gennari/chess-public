plugins {
    `kotlin-dsl`
    alias(libs.plugins.semanticVersioning)
}

group = "it.ric.buildlogic"

dependencies {
    implementation(libs.android.gradlePlugin)
    implementation(libs.kotlin.gradlePlugin)
    implementation(libs.detekt.gradlePlugin)
    implementation(libs.ktlint.gradlePlugin)
    implementation("com.github.jmongard:git-semver-plugin:${libs.versions.semanticVersioning.get()}")
}

gradlePlugin {
    plugins {
        register("it.ric.convention") {
            id = "it.ric.convention"
            implementationClass = "it.ric.convention.ConventionPlugin"
        }
    }
}
