plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.example.runandfun"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.runandfun"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.play.services.maps)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // Google Play Services dla map
    implementation(libs.play.services.maps.v1802)
    implementation(libs.play.services.location.v2101)

    // ViewModel
    implementation(libs.androidx.lifecycle.viewmodel.ktx)

    // LiveData
    implementation(libs.androidx.lifecycle.livedata.ktx)

    // Google Play Services - localisation
    implementation(libs.play.services.location)

    // JUnit do testów jednostkowych
    testImplementation(libs.junit)

    // Mockito do mockowania obiektów
    testImplementation(libs.mockito.core)

    // Zaleźność do testów Coroutines
    testImplementation(libs.kotlinx.coroutines.test)

    // Dodatkowe narzędzia testowe dla Androida
    androidTestImplementation(libs.androidx.junit.v113)
    androidTestImplementation(libs.androidx.espresso.core.v340)

    // Zaleźność do obsługi InstantTaskExecutorRule
    testImplementation(libs.androidx.core.testing)
}