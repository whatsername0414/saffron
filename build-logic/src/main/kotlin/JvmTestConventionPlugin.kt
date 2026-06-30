import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType

class JvmTestConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")
            fun lib(alias: String) = libs.findLibrary(alias).get()

            dependencies {
                add("testImplementation", lib("junit"))
                add("testImplementation", lib("kotlinx-coroutines-test"))
                add("testImplementation", lib("turbine"))
                add("testImplementation", lib("truth"))
            }
        }
    }
}
