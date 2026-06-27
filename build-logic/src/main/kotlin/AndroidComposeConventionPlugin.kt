import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * Enables Jetpack Compose on whichever Android extension is present (application or library):
 * applies the Compose compiler plugin and flips `buildFeatures.compose = true`.
 *
 * Deliberately adds NO Compose library dependencies — each module keeps its own exact
 * Compose dependency declarations so the resolved dependency graph stays byte-for-byte identical.
 * Apply AFTER `com.android.application` / `com.android.library`.
 */
class AndroidComposeConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("org.jetbrains.kotlin.plugin.compose")

            extensions.findByType(ApplicationExtension::class.java)?.apply {
                buildFeatures.compose = true
            }
            extensions.findByType(LibraryExtension::class.java)?.apply {
                buildFeatures.compose = true
            }
        }
    }
}
