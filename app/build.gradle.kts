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

    val releaseStoreFile = providers.gradleProperty("RELEASE_STORE_FILE")
        .orElse(providers.environmentVariable("RELEASE_STORE_FILE"))
        .orNull
    val releaseStorePassword = providers.gradleProperty("RELEASE_STORE_PASSWORD")
        .orElse(providers.environmentVariable("RELEASE_STORE_PASSWORD"))
        .orNull
    val releaseKeyAlias = providers.gradleProperty("RELEASE_KEY_ALIAS")
        .orElse(providers.environmentVariable("RELEASE_KEY_ALIAS"))
        .orNull
    val releaseKeyPassword = providers.gradleProperty("RELEASE_KEY_PASSWORD")
        .orElse(providers.environmentVariable("RELEASE_KEY_PASSWORD"))
        .orNull

    signingConfigs {
        if (releaseStoreFile != null && releaseStorePassword != null && releaseKeyAlias != null && releaseKeyPassword != null) {
            create("release") {
                storeFile = file(releaseStoreFile)
                storePassword = releaseStorePassword
                keyAlias = releaseKeyAlias
                keyPassword = releaseKeyPassword
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            signingConfig = signingConfigs.findByName("release") ?: signingConfigs.getByName("debug")
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
