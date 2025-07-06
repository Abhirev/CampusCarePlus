plugins {
    
    alias(libs.plugins.android.application)

    id("com.google.gms.google-services")
}

android {
    namespace = "com.abhi.ltcecampuscare"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.abhi.ltcecampuscare"
        minSdk = 23
        targetSdk = 35
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
            val geminiApiKey: String = project.rootProject.file("local.properties")
                .readLines()
                .first { it.startsWith("GEMINI_API_KEY") }
                .split("=")[1]
                .trim()

            buildConfigField("String", "GEMINI_API_KEY", "\"$geminiApiKey\"")

        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    implementation(libs.activity)

    // Firebase BOM for consistent Firebase versions
    implementation(platform("com.google.firebase:firebase-bom:33.16.0")) // Ensure this is always current

    // Firebase products (versions managed by the BOM)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.firestore) // Use the libs. alias if available and correct in versions.toml
    implementation("com.google.firebase:firebase-storage") // Use direct string if no libs. alias or if it's new




    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    // Testing dependencies
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}