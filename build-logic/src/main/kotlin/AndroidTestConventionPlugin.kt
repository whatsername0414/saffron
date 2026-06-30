import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType

class AndroidTestConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")
            fun lib(alias: String) = libs.findLibrary(alias).get()

            extensions.configure<LibraryExtension> {
                testOptions {
                    unitTests.isIncludeAndroidResources = true
                    unitTests.isReturnDefaultValues = true
                }
            }

            dependencies {
                add("testImplementation", lib("junit"))
                add("testImplementation", lib("kotlinx-coroutines-test"))
                add("testImplementation", lib("turbine"))
                add("testImplementation", lib("truth"))
                add("testImplementation", lib("androidx-test-core"))
                add("testImplementation", lib("androidx-arch-core-testing"))
                add("testImplementation", lib("robolectric"))
            }
        }
    }
}
