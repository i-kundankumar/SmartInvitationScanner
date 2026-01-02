plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.orbitex.sis"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.orbitex.sis"
        minSdk = 24
        targetSdk = 36
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.activity)
    implementation(libs.firebase.auth)
    implementation(libs.play.services.mlkit.text.recognition.common)
    implementation(libs.firebase.firestore)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    implementation(libs.androidx.appcompat)
    implementation("com.google.android.material:material:1.13.0")
    implementation("androidx.constraintlayout:constraintlayout:2.2.1")
    // RecyclerView (for history list)
    implementation("androidx.recyclerview:recyclerview:1.4.0")
    //Camera
    implementation("androidx.camera:camera-core:1.5.1")
    implementation("androidx.camera:camera-camera2:1.5.1")
    implementation("androidx.camera:camera-lifecycle:1.5.1")
    implementation("androidx.camera:camera-view:1.5.1")
    // ML Kit Barcode Scanning
    implementation("com.google.mlkit:barcode-scanning:17.3.0")
    // ZXing for QR generation (optional)
    implementation("com.journeyapps:zxing-android-embedded:4.3.0")
    // Utility
    implementation("androidx.appcompat:appcompat:1.7.1")
    implementation("androidx.constraintlayout:constraintlayout:2.2.1")

    implementation("com.github.bumptech.glide:glide:4.15.1")
    annotationProcessor("com.github.bumptech.glide:compiler:4.15.1")
    implementation("de.hdodenhof:circleimageview:3.1.0")
    // (Optional) Lifecycle & ViewModel for clean architecture
    implementation("androidx.lifecycle:lifecycle-viewmodel:2.10.0")
    implementation("androidx.lifecycle:lifecycle-livedata:2.10.0")
    // Cloudinary add on for storage
    implementation("com.cloudinary:cloudinary-android:3.1.2")
}