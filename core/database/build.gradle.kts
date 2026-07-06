plugins {
    id("saffron.android.library")
    id("saffron.android.test")
    alias(libs.plugins.ksp)
    alias(libs.plugins.ktlint)
}

android {
    namespace = "com.saffron.cook.core.database"
}

dependencies {
    api(project(":core:domain"))
    api(libs.androidx.room.runtime)
    api(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)
    implementation(libs.koin.android)
    implementation(libs.kotlinx.coroutines.core)
    testImplementation(project(":core:testing"))
}
