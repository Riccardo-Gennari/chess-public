import it.ric.convention.setupKover
import it.ric.convention.setupQualityChecks
import org.jlleitschuh.gradle.ktlint.KtlintExtension

plugins {
    id("it.ric.convention")
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.composeCompiler) apply false
    alias(libs.plugins.googleServices) apply false

    alias(libs.plugins.detekt) apply true
    alias(libs.plugins.kover) apply true
    alias(libs.plugins.ktlint) apply true
    alias(libs.plugins.semanticVersioning) apply true
}

dependencies {
    "detektPlugins"(libs.detekt.compose.rules)
    "ktlintRuleset"(libs.ktlint.compose.rules)
    subprojects.forEach { kover(it) }
}

configure<KtlintExtension> {
    version.set(libs.versions.ktlint.core)
}

allprojects {
    setupQualityChecks(
        detektPlugin = rootProject.libs.plugins.detekt,
        ktlintPlugin = rootProject.libs.plugins.ktlint,
        detektRules = rootProject.libs.detekt.compose.rules,
        ktlintRules = rootProject.libs.ktlint.compose.rules,
        ktlintVersion = rootProject.libs.versions.ktlint.core,
    )
    setupKover(rootProject.libs.plugins.kover)
    kover {
        reports {
            filters {
                excludes {
                    annotatedBy("androidx.compose.runtime.Composable")
                    annotatedBy("androidx.compose.ui.tooling.preview.Preview")
                    classes(
                        "*ComposableSingletons*",
                        "*LiveLiterals*",
                        "*$*DefaultImpls",
                        "*_*",
                    )
                    packages("dagger.hilt", "hilt_aggregated_deps", "*.composable")
                }
            }
        }
    }
}
