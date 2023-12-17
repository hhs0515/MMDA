import java.util.*

plugins {
    id("com.android.application")
}

val properties = Properties().apply {
    load(File(project.rootProject.projectDir, "local.properties").inputStream())
}

// Accessing properties
val apiKey: String = '"' + properties.getProperty("MY_OPENAI_KEY") + '"'

android {
    namespace = "com.csed404.mmda"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.csed404.mmda"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "api_key", apiKey)
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildFeatures {
        viewBinding = true
        buildConfig = true
    }
}

dependencies {

    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.6.2")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.2")
    implementation("androidx.navigation:navigation-fragment:2.7.6")
    implementation("androidx.navigation:navigation-ui:2.7.6")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    implementation ("androidx.legacy:legacy-support-v4:1.0.0")

    // For Google Calendar
    implementation ("com.google.android.gms:play-services-auth:20.7.0")
    implementation("com.google.api-client:google-api-client-android:1.22.0") {
        exclude("org.apache.httpcomponents")
    }
    implementation("com.google.apis:google-api-services-calendar:v3-rev235-1.22.0") {
        exclude("org.apache.httpcomponents")
    }

    // HTTP request (for GPT)
    implementation ("com.squareup.okhttp3:okhttp:4.9.0")

    // checkDebugDuplicateClasses Error prevention
    implementation ("com.google.guava:listenablefuture:9999.0-empty-to-avoid-conflict-with-guava")
}