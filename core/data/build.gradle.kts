plugins {
    alias(libs.plugins.kotlin.jvm)
}

dependencies {
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)
    implementation(libs.kotlinx.coroutines.core)
}
