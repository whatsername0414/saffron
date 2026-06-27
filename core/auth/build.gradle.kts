plugins {
    id("saffron.android.library")
    alias(libs.plugins.ktlint)
}

android {
    namespace = "com.saffron.cook.core.auth"
}

dependencies {
    api(platform(libs.firebase.bom))
    api(libs.firebase.auth)
    implementation(libs.koin.android)
    implementation(libs.kotlinx.coroutines.core)
}
