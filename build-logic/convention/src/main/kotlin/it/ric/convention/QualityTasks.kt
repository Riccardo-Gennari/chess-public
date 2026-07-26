package it.ric.convention

import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jlleitschuh.gradle.ktlint.KtlintExtension
import org.jlleitschuh.gradle.ktlint.reporter.ReporterType

fun Project.setupQualityChecks() {
    val libs = project.libs
    pluginManager.apply("org.jlleitschuh.gradle.ktlint")

    // Configure Ktlint
    pluginManager.withPlugin("org.jlleitschuh.gradle.ktlint") {
        extensions.configure<KtlintExtension> {
            android.set(true)
            ignoreFailures.set(false)
            reporters {
                reporter(ReporterType.HTML)
                reporter(ReporterType.CHECKSTYLE)
            }
            filter {
                exclude("**/generated/**")
            }
        }
        dependencies.add("ktlintRuleset", libs.findLibrary("ktlint-compose-rules").get())
    }
}
