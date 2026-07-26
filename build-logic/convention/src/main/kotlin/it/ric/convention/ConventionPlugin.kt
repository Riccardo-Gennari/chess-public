package it.ric.convention

import org.gradle.api.Plugin
import org.gradle.api.Project

class ConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        // This plugin just puts the convention classes on the classpath
    }
}
