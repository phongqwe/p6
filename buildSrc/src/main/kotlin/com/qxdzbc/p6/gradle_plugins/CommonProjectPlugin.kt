package com.qxdzbc.p6.gradle_plugins

import com.qxdzbc.p6.gradle_plugins.Utils.dep
import com.qxdzbc.p6.gradle_plugins.Utils.bundle
import com.qxdzbc.p6.gradle_plugins.Utils.getVersionCatalog
import com.qxdzbc.p6.gradle_plugins.Utils.implementation
import com.qxdzbc.p6.gradle_plugins.Utils.testImplementation
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.kotlin

/**
 * Consist of the standard test library, assertion library, and result library
 */
class CommonProjectPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        val libs = target.getVersionCatalog()
        target.pluginManager.apply(TestPlugin::class.java)
        target.dependencies {
            implementation(libs.dep("michaelbull.kotlinResult"))
        }
    }
}