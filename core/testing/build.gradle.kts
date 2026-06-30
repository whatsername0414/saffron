plugins {
    id("saffron.android.library")
    alias(libs.plugins.ktlint)
}

android {
    namespace = "com.saffron.cook.core.testing"
}

dependencies {
    implementation(project(":core:domain"))
    implementation(project(":core:database"))
    api(libs.kotlinx.coroutines.test)
    api(libs.junit)
}
