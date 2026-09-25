package it.ric.convention

import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import org.gradle.api.Project
import org.gradle.api.artifacts.MinimalExternalModuleDependency
import org.gradle.api.provider.Provider
import org.gradle.kotlin.dsl.configure
import org.gradle.plugin.use.PluginDependency
import org.jlleitschuh.gradle.ktlint.KtlintExtension
import org.jlleitschuh.gradle.ktlint.reporter.ReporterType

fun Project.setupQualityChecks(
    detektPlugin: Provider<PluginDependency>,
    ktlintPlugin: Provider<PluginDependency>,
    detektRules: Provider<MinimalExternalModuleDependency>,
    ktlintRules: Provider<MinimalExternalModuleDependency>,
    ktlintVersion: Provider<String>,
) {
    // 1. Apply Plugins
    plugins.apply(detektPlugin.get().pluginId)
    plugins.apply(ktlintPlugin.get().pluginId)

    // 2. Add Ruleset Dependencies
    dependencies.add("detektPlugins", detektRules)
    dependencies.add("ktlintRuleset", ktlintRules)

    // 3. Register common verification task
    tasks.register("checkQuality") {
        group = "verification"
        description = "Runs ktlint and detekt checks on the project"
        dependsOn("ktlintCheck", "detekt")
    }

    // 4. Configure Detekt
    extensions.configure<DetektExtension> {
        parallel = true
        config.setFrom(files("${rootProject.projectDir}/config/detekt/detekt.yml"))
        buildUponDefaultConfig = true
        allRules = false
        autoCorrect = true
    }

    // 5. Configure Ktlint
    extensions.configure<KtlintExtension> {
        android.set(true)
        ignoreFailures.set(false)
        version.set(ktlintVersion)
        reporters {
            reporter(ReporterType.HTML)
            reporter(ReporterType.CHECKSTYLE)
        }
    }
}
