plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.example.gameapp"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.gameapp"
        minSdk = 24
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
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }

    buildFeatures {
        viewBinding = true // HABILITAR BINDING
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

    // ... otras dependencias

// Retrofit (Librería para peticiones HTTP)
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
// Convertidor de JSON (usaremos Gson, pero Moshi o kotlinx.serialization son alternativas válidas)
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
// OkHttp (Para interceptores o logs)
    implementation("com.squareup.okhttp3:okhttp:4.12.0")

// Coroutines y Componentes de Arquitectura
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.7.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

// Para RecyclerView para mostrar la lista de juegos
    implementation("androidx.recyclerview:recyclerview:1.3.2")
// Para Coil o Glide si quieres cargar imágenes de los juegos
    implementation("io.coil-kt:coil:2.5.0")
}