import com.android.build.api.dsl.ApplicationExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

/**
 * Convention plugin for the `:app` module: AGP application + Compose, SDK levels,
 * Java 11, BuildConfig, and the AGP 9 release `optimization` block.
 *
 * Module-specific config (namespace, applicationId, versionCode/Name, testInstrumentationRunner)
 * stays in `app/build.gradle.kts`.
 */
class AndroidApplicationConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.android.application")
                apply("saffron.android.compose")
            }

            extensions.configure<ApplicationExtension> {
                compileSdk = 37

                defaultConfig {
                    minSdk = 24
                    targetSdk = 37
                }

                compileOptions {
                    sourceCompatibility = JavaVersion.VERSION_11
                    targetCompatibility = JavaVersion.VERSION_11
                }

                buildFeatures {
                    buildConfig = true
                }

                buildTypes {
                    release {
                        optimization {
                            enable = false
                        }
                    }
                }
            }
        }
    }
}
