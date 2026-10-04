plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.soko.browser"
    compileSdk = 36 // Android 16

    defaultConfig {
        applicationId = "com.soko.browser"
        minSdk = 24
        targetSdk = 36
        versionCode = 20270101
        versionName = "2027.1.0-global-id"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        create("release") {
            storeFile = file(project.findProperty("keystoreFile") ?: "")
            storePassword = project.findProperty("keystorePassword")?.toString()
            keyAlias = project.findProperty("keyAlias")?.toString()
            keyPassword = project.findProperty("keyPassword")?.toString()
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            signingConfig = signingConfigs["release"]
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    kotlinOptions {
        jvmTarget = "21"
    }
}

dependencies {
    // Chromium core
    implementation("org.chromium.android:content:131.0.6778.137")
    implementation("org.chromium.android:chrome:131.0.6778.137")
    
    // MV3 extension support
    implementation("org.chromium.android:extensions:131.0.6778.137")
    
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.8.0")
}
