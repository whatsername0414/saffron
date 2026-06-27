import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

/**
 * Convention plugin for pure-JVM Kotlin library modules (no Android): `kotlin.jvm` + Java 11.
 * Java and Kotlin JVM targets are kept aligned to avoid inconsistent-target validation failures.
 * Reusable for both `:core:data` (now) and `:core:domain` (Phase 2).
 */
class JvmLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("org.jetbrains.kotlin.jvm")

            extensions.configure<JavaPluginExtension> {
                sourceCompatibility = JavaVersion.VERSION_11
                targetCompatibility = JavaVersion.VERSION_11
            }

            tasks.withType(KotlinCompile::class.java).configureEach {
                compilerOptions {
                    jvmTarget.set(JvmTarget.JVM_11)
                }
            }
        }
    }
}
