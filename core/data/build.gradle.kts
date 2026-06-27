plugins {
    id("saffron.jvm.library")
    alias(libs.plugins.ktlint)
}

dependencies {
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)
    implementation(libs.kotlinx.coroutines.core)
}
