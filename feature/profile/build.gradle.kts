plugins {
    id("saffron.android.library")
    id("saffron.android.compose")
    id("saffron.android.test")
    alias(libs.plugins.ktlint)
}

android {
    namespace = "com.saffron.cook.feature.profile"
}

dependencies {
    implementation(project(":core:auth"))
    implementation(project(":core:database"))
    implementation(project(":core:design-system"))

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.coil.compose)
    implementation(libs.credentials)
    implementation(libs.credentials.play.services)
    implementation(libs.googleid)
    implementation(libs.koin.android)
    implementation(libs.koin.androidx.compose)
    debugImplementation(libs.androidx.compose.ui.tooling)

    testImplementation(project(":core:testing"))
}
