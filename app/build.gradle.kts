plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    id("com.google.gms.google-services")
    id("de.undercouch.download") version "5.+"
}

android {
    namespace = "com.kilabid.workoutapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.kilabid.workoutapp"
        minSdk = 26
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
    buildFeatures{
        viewBinding = true
        buildConfig = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.vision.common)
    implementation(libs.firebase.auth)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    //MediaPipe Pose Landmarker
    implementation (libs.tasks.vision)

    //CameraX
    implementation (libs.androidx.camera.core)
    implementation (libs.androidx.camera.camera2)
    implementation (libs.androidx.camera.lifecycle)
    implementation (libs.androidx.camera.view)

    //Fragment ViewModels
    implementation ("androidx.fragment:fragment-ktx:1.7.1")

    //firebase Authentication
    implementation(platform("com.google.firebase:firebase-bom:33.1.1"))
    implementation("com.google.firebase:firebase-analytics")
    implementation ("com.firebaseui:firebase-ui-auth:8.0.2")

    //DataStore
    implementation("androidx.datastore:datastore-preferences:1.1.1")
}