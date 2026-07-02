import com.android.build.api.dsl.ApplicationExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

/**
 * Convention plugin for the `:app` module: AGP application + Compose, SDK levels,
 * Java 11, BuildConfig, and the debug/release build types (AGP 9 `optimization` DSL).
 *
 * debug: `.debug` applicationIdSuffix/versionNameSuffix so it can install alongside release;
 * requires a matching Firebase Android app (`com.saffron.cook.debug`) in `google-services.json`.
 * release: R8 shrink+obfuscate enabled via `optimization { enable = true }`, picking up keep
 * rules from every module's `src/main/keepRules/rules.keep`.
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
                    debug {
                        applicationIdSuffix = ".debug"
                        versionNameSuffix = "-debug"
                        isDebuggable = true
                    }
                    release {
                        isDebuggable = false
                        optimization {
                            enable = true
                        }
                    }
                }
            }
        }
    }
}
