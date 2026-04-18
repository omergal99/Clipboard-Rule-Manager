plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
}

// ─────────────────────────────────────────────────────────────────────────
// VERSIONING: Update these values for new releases
// ─────────────────────────────────────────────────────────────────────────
object AppVersions {
    const val compileSdk = 34
    const val minSdk = 28
    const val targetSdk = 34
    const val versionCode = 1
    const val versionName = "1.0.0"
    const val applicationId = "com.clipboard.rulemanager"
    const val namespace = "com.clipboard.rulemanager"
}

android {
    namespace = AppVersions.namespace
    compileSdk = AppVersions.compileSdk

    defaultConfig {
        applicationId = AppVersions.applicationId
        minSdk = AppVersions.minSdk
        targetSdk = AppVersions.targetSdk
        versionCode = AppVersions.versionCode
        versionName = AppVersions.versionName

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        create("release") {
            // Signing configuration for release builds
            // Supports environment variables for CI/CD (GitHub Actions):
            //   SIGNING_KEY_ALIAS
            //   SIGNING_KEY_PASSWORD
            //   SIGNING_STORE_PASSWORD
            //   SIGNING_KEYSTORE_PATH (optional, defaults to ~/.android/release.keystore)
            // For local builds, use ~/.android/release.keystore with standard keystore password
            
            storeFile = File(
                System.getenv("SIGNING_KEYSTORE_PATH") 
                    ?: System.getProperty("user.home") + "/.android/release.keystore"
            )
            storePassword = System.getenv("SIGNING_STORE_PASSWORD") ?: ""
            keyAlias = System.getenv("SIGNING_KEY_ALIAS") ?: ""
            keyPassword = System.getenv("SIGNING_KEY_PASSWORD") ?: ""
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            signingConfig = signingConfigs.getByName("release")
        }
        debug {
            isMinifyEnabled = false
            isDebuggable = true
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
        freeCompilerArgs += "-Xopt-in=kotlin.RequiresOptIn"
        freeCompilerArgs += "-Xopt-in=androidx.compose.foundation.ExperimentalFoundationApi"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.0"
    }
}

dependencies {
    // Core Android
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.2")

    // Jetpack Compose
    implementation("androidx.compose.ui:ui:1.5.4")
    implementation("androidx.compose.material3:material3:1.1.2")
    implementation("androidx.compose.ui:ui-tooling-preview:1.5.4")
    implementation("androidx.compose.runtime:runtime-livedata:1.5.4")
    implementation("androidx.activity:activity-compose:1.8.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.2")

    // DataStore
    implementation("androidx.datastore:datastore-preferences:1.0.0")

    // Room Database
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    kapt("androidx.room:room-compiler:2.6.1")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

    // Serialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")

    // Testing
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:1.9.0")
    testImplementation("org.mockito.kotlin:mockito-kotlin:5.1.0")
    testImplementation("org.mockito:mockito-core:5.3.1")
    
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation("androidx.room:room-testing:2.6.1")
}
