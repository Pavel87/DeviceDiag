plugins {
    id("com.android.application")
    alias(libs.plugins.org.jetbrains.kotlin.android)
    alias(libs.plugins.dagger.hilt.android)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.ksp)
}

android {
    signingConfigs {
        create("config") {
        }
    }
    compileSdk = 35
    buildToolsVersion = "35.0.0"

    defaultConfig {
        applicationId = "com.pacmac.devicediag"
        minSdk = 24
        targetSdk = 34
        versionCode = 75
        versionName = "3.0.5"
        multiDexEnabled = true

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android.txt"),
                "proguard-rules.pro"
            )
            resValue("string", "banner_id_1", "ca-app-pub-9192035457575047/1516826451")
            resValue("string", "banner_id_2", "ca-app-pub-9192035457575047/9450817484")
            resValue("string", "banner_id_3", "ca-app-pub-9192035457575047/7805206084")
            // resValue("string", "banner_id_4", "ca-app-pub-9192035457575047/4397563675")
            resValue("string", "banner_id_5", "ca-app-pub-9192035457575047/4205991983")
            resValue("string", "banner_id_6", "ca-app-pub-9192035457575047/2892910315")
            resValue("string", "banner_id_7", "ca-app-pub-9192035457575047/1515415313")
            resValue("string", "banner_id_8", "ca-app-pub-9192035457575047/5770602510")
            resValue("string", "banner_id_9", "ca-app-pub-9192035457575047/2636925292")
            resValue("string", "banner_id_10", "ca-app-pub-9192035457575047/4933777967")
            // resValue("string", "banner_id_11", "ca-app-pub-9192035457575047/6892112497")
            // resValue("string", "banner_id_12", "ca-app-pub-9192035457575047/8461959436")
            // resValue("string", "banner_id_13", "ca-app-pub-9192035457575047/4786438304")
            resValue("string", "banner_id_14", "ca-app-pub-9192035457575047/8530242194")
            resValue("string", "banner_id_15", "ca-app-pub-9192035457575047/7630479099")
            resValue("string", "banner_id_16", "ca-app-pub-9192035457575047/1420880696")
            resValue("string", "rewarded1", "ca-app-pub-9192035457575047/6962209340")
        }
        getByName("debug") {
            // isMinifyEnabled = false
            // proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
            resValue("string", "banner_id_1", "ca-app-pub-3940256099942544/6300978111")
            resValue("string", "banner_id_2", "ca-app-pub-3940256099942544/6300978111")
            resValue("string", "banner_id_3", "ca-app-pub-3940256099942544/6300978111")
            // resValue("string", "banner_id_4", "ca-app-pub-3940256099942544/6300978111")
            resValue("string", "banner_id_5", "ca-app-pub-3940256099942544/6300978111")
            resValue("string", "banner_id_6", "ca-app-pub-3940256099942544/6300978111")
            resValue("string", "banner_id_7", "ca-app-pub-3940256099942544/6300978111")
            resValue("string", "banner_id_8", "ca-app-pub-3940256099942544/6300978111")
            resValue("string", "banner_id_9", "ca-app-pub-3940256099942544/6300978111")
            resValue("string", "banner_id_10", "ca-app-pub-3940256099942544/6300978111")
            // resValue("string", "banner_id_11", "ca-app-pub-3940256099942544/6300978111")
            // resValue("string", "banner_id_12", "ca-app-pub-3940256099942544/6300978111")
            // resValue("string", "banner_id_13", "ca-app-pub-3940256099942544/6300978111")
            resValue("string", "banner_id_14", "ca-app-pub-3940256099942544/6300978111")
            resValue("string", "banner_id_15", "ca-app-pub-3940256099942544/6300978111")
            resValue("string", "banner_id_16", "ca-app-pub-3940256099942544/6300978111")

            resValue("string", "rewarded1", "ca-app-pub-3940256099942544/5224354917")
        }
    }
    flavorDimensions += "default"

    productFlavors {
        create("free") {
            applicationId = "com.pacmac.devicediag.free"
        }
    }
    sourceSets {
        getByName("main") {
            java.srcDirs("src/main/java")
        }
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.2"
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    namespace = "com.pacmac.devinfo"
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(fileTree(mapOf("include" to listOf("*.jar"), "dir" to "libs")))

    implementation(platform(libs.compose.bom))
    implementation(libs.bundles.androidx.libs)
    implementation(libs.bundles.compose)

    implementation(libs.bundles.ktor)
    implementation(libs.bundles.extras)

    // HILT
    implementation(libs.bundles.hilt)
    ksp(libs.hilt.android.compiler)

    implementation(libs.bundles.debug.only)
    testImplementation(libs.bundles.test.libs)
    // For instrumentation tests
    androidTestImplementation(libs.bundles.android.tests)

    implementation(libs.bundles.mediation.adapters)
}