plugins {
    id("saffron.jvm.library")
    id("saffron.jvm.test")
    alias(libs.plugins.ktlint)
}

dependencies {
    api(project(":core:domain"))
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)
    implementation(libs.kotlinx.coroutines.core)
}
