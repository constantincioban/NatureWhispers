import org.jetbrains.kotlin.fir.declarations.builder.buildScript

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    kotlin("kapt")
    id("com.google.dagger.hilt.android")

}

android {
    namespace = "com.example.naturewhispers"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.naturewhispers"
        minSdk = 27
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }

        buildConfigField("String", "STORAGE_URL", "${properties["STORAGE_URL"]}")

    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            isDebuggable = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            multiDexEnabled = true
            signingConfig = signingConfigs.getByName("debug")
        }
        debug {
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
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }

}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.play.services.base)
    implementation(libs.play.services.auth)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // Coil
    implementation("io.coil-kt:coil-compose:2.6.0")

    // DataStore
    implementation("androidx.datastore:datastore-preferences:1.0.0")

    //MpAndroidChart
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")

    // Waveform
    implementation("com.github.lincollincol:compose-audiowaveform:1.1.1")

    // Calendar view
    implementation("io.github.boguszpawlowski.composecalendar:composecalendar:1.2.0")
    implementation("io.github.boguszpawlowski.composecalendar:kotlinx-datetime:1.2.0")

    // Icons
    implementation("androidx.compose.material:material-icons-extended-android:1.6.5")

    // Exoplayer
    implementation("androidx.media3:media3-datasource-okhttp:1.1.0")
    implementation("androidx.media3:media3-exoplayer:1.1.0")
    implementation("androidx.media3:media3-exoplayer-dash:1.1.0")
    implementation("androidx.media3:media3-ui:1.1.0")
    implementation("androidx.media3:media3-common:1.1.0")
    implementation("androidx.media3:media3-session:1.1.0")
    implementation("androidx.legacy:legacy-support-v4:1.0.0")



    implementation("androidx.credentials:credentials:1.3.0-alpha02")
    implementation("androidx.credentials:credentials-play-services-auth:1.2.2")
    implementation("com.google.android.libraries.identity.googleid:googleid:1.1.0")

// Hilt
    implementation("com.google.dagger:hilt-android:2.48")
    kapt("com.google.dagger:hilt-android-compiler:2.48")
    implementation("androidx.hilt:hilt-navigation-compose:1.0.0")

// LiveData
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.6.1")

// Room
    implementation("androidx.room:room-runtime:2.6.1")
    kapt("androidx.room:room-compiler:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")

// Navigation
    implementation("androidx.navigation:navigation-fragment-ktx: 2.5.3")
    implementation("androidx.navigation:navigation-ui-ktx: 2.5.3")
    implementation("androidx.navigation:navigation-compose: 2.5.3")
    implementation("com.google.accompanist:accompanist-navigation-animation:0.31.6-rc")

// Gson
    implementation("com.google.code.gson:gson:2.8.8")

// Glide
    implementation("com.github.bumptech.glide:glide:4.16.0")
}

kapt {
    correctErrorTypes = true
}