plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.atuy.viewer_bridge"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.atuy.viewer_bridge"
        minSdk = 34
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

    flavorDimensions += "viewer"
    productFlavors {
        create("google") {
            dimension = "viewer"
            applicationIdSuffix = ".google"
            buildConfigField("String", "VIEWER_PREFIX", "\"https://docs.google.com/viewer?url=\"")
        }
        create("office") {
            dimension = "viewer"
            applicationIdSuffix = ".office"
            buildConfigField("String", "VIEWER_PREFIX", "\"https://view.officeapps.live.com/op/view.aspx?src=\"")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
}
