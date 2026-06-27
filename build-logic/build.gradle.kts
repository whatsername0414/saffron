import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `kotlin-dsl`
}

// build-logic plugins run inside Gradle and compile against the AGP 9 API, which targets JDK 17.
// Keep Java and Kotlin targets aligned to avoid "Inconsistent JVM-target compatibility".
java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

tasks.withType<KotlinCompile>().configureEach {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_17)
    }
}

dependencies {
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.kotlin.gradlePlugin)
    compileOnly(libs.compose.gradlePlugin)
}

gradlePlugin {
    plugins {
        register("androidApplication") {
            id = "saffron.android.application"
            implementationClass = "AndroidApplicationConventionPlugin"
        }
        register("androidLibrary") {
            id = "saffron.android.library"
            implementationClass = "AndroidLibraryConventionPlugin"
        }
        register("androidCompose") {
            id = "saffron.android.compose"
            implementationClass = "AndroidComposeConventionPlugin"
        }
        register("jvmLibrary") {
            id = "saffron.jvm.library"
            implementationClass = "JvmLibraryConventionPlugin"
        }
    }
}
