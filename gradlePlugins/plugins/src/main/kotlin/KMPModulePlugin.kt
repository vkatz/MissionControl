@file:Suppress("unused")

import com.android.build.gradle.LibraryExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.*
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinDependencyHandler

class KMPModulePlugin : Plugin<Project> {

    override fun apply(target: Project): Unit = with(target) {
        apply(plugin = "kotlin-multiplatform")
        apply(plugin = "com.android.library")
        extensions.create<KMPModuleExtension>("KMPModuleExtension")
    }

    @Suppress("UNUSED_VARIABLE")
    @OptIn(ExperimentalKotlinGradlePluginApi::class)
    internal fun applyConfig(target: Project): Unit = with(target) {

        val extension: KMPModuleExtension = project.extensions.getByType<KMPModuleExtension>()

        extensions.configure<KotlinMultiplatformExtension> {
            val kmmExtension = this

            targetHierarchy.default()

            androidTarget {
                compilations.all {
                    kotlinOptions {
                        jvmTarget = "1.8"
                    }
                }
            }

            jvm("desktop")

            sourceSets {
                val commonMain by getting {
                    dependencies {
                        extension.commonMainDependencies?.invoke(this, kmmExtension)
                    }
                }
                val commonTest by getting {
                    dependencies {
                        extension.commonTestDependencies?.invoke(this, kmmExtension)
                    }
                }
                val androidMain by getting {
                    dependencies {
                        extension.androidMainDependencies?.invoke(this, kmmExtension)
                    }
                }

                val desktopMain by getting {
                    dependencies {
                        extension.desktopMainDependencies?.invoke(this, kmmExtension)
                    }
                }
            }
        }

        extensions.configure<LibraryExtension> {
            namespace = extension.androidName
            // todo ref to libs (toml)
            compileSdk = 33
            defaultConfig {
                minSdk = 24
            }
        }
    }
}

fun Project.kmmModule(configure: KMPModuleExtension.() -> Unit) {
    extensions.getByType<KMPModuleExtension>().configure()
    plugins.getPlugin(KMPModulePlugin::class).applyConfig(this)
}

abstract class KMPModuleExtension {
    abstract var androidName: String?
    var commonMainDependencies: (KotlinDependencyHandler.(kmm: KotlinMultiplatformExtension) -> Unit)? = null
    var commonTestDependencies: (KotlinDependencyHandler.(kmm: KotlinMultiplatformExtension) -> Unit)? = null
    var androidMainDependencies: (KotlinDependencyHandler.(kmm: KotlinMultiplatformExtension) -> Unit)? = null
    var desktopMainDependencies: (KotlinDependencyHandler.(kmm: KotlinMultiplatformExtension) -> Unit)? = null

    fun commonMainDependencies(action: KotlinDependencyHandler.(kmm: KotlinMultiplatformExtension) -> Unit) = run { commonMainDependencies = action }
    fun commonTestDependencies(action: KotlinDependencyHandler.(kmm: KotlinMultiplatformExtension) -> Unit) = run { commonTestDependencies = action }
    fun androidMainDependencies(action: KotlinDependencyHandler.(kmm: KotlinMultiplatformExtension) -> Unit) = run { androidMainDependencies = action }
    fun desktopMainDependencies(action: KotlinDependencyHandler.(kmm: KotlinMultiplatformExtension) -> Unit) = run { desktopMainDependencies = action }
}
