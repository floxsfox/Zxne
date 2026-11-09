plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

// Release signing is read from the environment (set by the CI workflow from
// repository secrets). Nothing is hardcoded; without it only debug is signed.
val releaseKeystorePath: String? = System.getenv("FILEX_KEYSTORE_PATH")
val hasReleaseSigning: Boolean =
    !releaseKeystorePath.isNullOrBlank() &&
        file(releaseKeystorePath).exists() &&
        !System.getenv("FILEX_KEYSTORE_PASSWORD").isNullOrBlank() &&
        !System.getenv("FILEX_KEY_ALIAS").isNullOrBlank() &&
        !System.getenv("FILEX_KEY_PASSWORD").isNullOrBlank()

android {
    namespace = "com.filex.studio"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.filex.studio"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "0.1.0"
    }

    signingConfigs {
        if (hasReleaseSigning) {
            create("release") {
                storeFile = file(releaseKeystorePath!!)
                storePassword = System.getenv("FILEX_KEYSTORE_PASSWORD")
                keyAlias = System.getenv("FILEX_KEY_ALIAS")
                keyPassword = System.getenv("FILEX_KEY_PASSWORD")
            }
        }
    }

    buildTypes {
        debug {
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-debug"
        }
        release {
            // Minification is intentionally off until it is tested (task C13/C16).
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            if (hasReleaseSigning) {
                signingConfig = signingConfigs.getByName("release")
            }
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.androidx.documentfile)
    implementation(libs.shizuku.api)
    implementation(libs.shizuku.provider)
}
