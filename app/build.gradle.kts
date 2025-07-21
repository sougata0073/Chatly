import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)

    // Safe args
    id("androidx.navigation.safeargs.kotlin")

    // For parcelization
    id("kotlin-parcelize")

    // Serialization plugin
    kotlin("plugin.serialization") version "2.2.0"
}

android {
    namespace = "com.sougata.chatly"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.sougata.chatly"
        minSdk = 30
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        val properties = Properties()
        properties.load(project.rootProject.file("local.properties").inputStream())
        buildConfigField("String", "SUPABASE_URL", properties.getProperty("SUPABASE_URL"))
        buildConfigField("String", "SUPABASE_KEY", properties.getProperty("SUPABASE_KEY"))
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
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        dataBinding = true
        buildConfig = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // Supabase BOM
    implementation(platform("io.github.jan-tennert.supabase:bom:3.2.0"))
    // Supabase
    // Database
    implementation("io.github.jan-tennert.supabase:postgrest-kt")
    // Authentication
    implementation("io.github.jan-tennert.supabase:auth-kt")
    // Storage
    implementation("io.github.jan-tennert.supabase:storage-kt")
    // Realtime
    implementation("io.github.jan-tennert.supabase:realtime-kt")

    // Ktor
    val ktorVersion = "3.2.1"
    // This android ktor engine does not support websockets(Realtime work) so use okhttp engine
    // implementation("io.ktor:ktor-client-android:$ktorVersion")
    implementation("io.ktor:ktor-client-okhttp:$ktorVersion") // Use okhttp engine

    // Credential Manager
    implementation("androidx.credentials:credentials:1.5.0")
    implementation("androidx.credentials:credentials-play-services-auth:1.5.0")
    implementation("com.google.android.libraries.identity.googleid:googleid:1.1.1")

    // Onboarding page dot indicator
    implementation("com.tbuonomo:dotsindicator:5.1.0")

    // Navigation
    implementation("androidx.navigation:navigation-fragment-ktx:2.9.0")
    implementation("androidx.navigation:navigation-ui-ktx:2.9.0")

    // Splashscreen
    implementation("androidx.core:core-splashscreen:1.0.1")

    // Glide
    implementation("com.github.bumptech.glide:glide:4.16.0")

    // Image compressor
    implementation("id.zelory:compressor:3.0.1")
}