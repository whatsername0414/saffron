// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.ktlint) apply false
    alias(libs.plugins.ksp) apply false
}

subprojects {
    plugins.withId("org.jlleitschuh.gradle.ktlint") {
        configure<org.jlleitschuh.gradle.ktlint.KtlintExtension> {
            version.set("1.4.1")
            verbose.set(true)
            outputToConsole.set(true)
            coloredOutput.set(true)
        }
        dependencies {
            "ktlintRuleset"("io.nlopez.compose.rules:ktlint:0.4.22")
        }
    }
}