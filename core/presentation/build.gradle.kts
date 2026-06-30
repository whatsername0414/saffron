plugins {
    id("saffron.android.library")
    id("saffron.android.compose")
    alias(libs.plugins.ktlint)
}

android {
    namespace = "com.saffron.cook.core.presentation"
}

dependencies {
    api(project(":core:domain"))
    api(project(":core:design-system"))

    api(platform(libs.androidx.compose.bom))
    api(libs.androidx.compose.material3)
    api(libs.androidx.compose.ui)
    api(libs.androidx.compose.ui.graphics)
    api(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.coil.compose)
    debugImplementation(libs.androidx.compose.ui.tooling)
}
