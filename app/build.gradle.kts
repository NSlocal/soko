plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.soko.browser"
    compileSdk = 36 // Android 16

    defaultConfig {
        applicationId = "com.soko.browser"
        minSdk = 26
        targetSdk = 36
        versionCode = 156080784
        versionName = "156.0.8078.4"

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

    packaging {
        resources {
            excludes += listOf(
                "/META-INF/**",
                "org/chromium/**/*.version",
                "com/google/**"
            )
        }
    }
}

dependencies {
    // === CHROMIUM 156.0.8078.4 — FULL OPEN SOURCE ===
    implementation("org.chromium.android:content:156.0.8078.4")
    implementation("org.chromium.android:chrome:156.0.8078.4")
    implementation("org.chromium.android:extensions:156.0.8078.4")
    implementation("org.chromium.android:components:156.0.8078.4")
    
    // AndroidX
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("androidx.webkit:webkit:1.12.1")
    
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.9.0")
}
