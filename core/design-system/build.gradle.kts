plugins {
    id("saffron.android.library")
    id("saffron.android.compose")
    alias(libs.plugins.ktlint)
}

android {
    namespace = "com.saffron.cook.core.designsystem"
}

dependencies {
    api(platform(libs.androidx.compose.bom))
    api(libs.androidx.compose.material3)
    api(libs.androidx.compose.ui)
    api(libs.androidx.compose.ui.graphics)
    api(libs.androidx.compose.ui.tooling.preview)
    debugImplementation(libs.androidx.compose.ui.tooling)
}
