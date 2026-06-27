plugins {
    id("saffron.jvm.library")
    alias(libs.plugins.ktlint)
}

dependencies {
    api(project(":core:domain"))
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)
    implementation(libs.kotlinx.coroutines.core)
}
