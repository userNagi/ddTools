plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
}

android {
    namespace = "com.nagi.ddtools"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.nagi.ddtools"
        minSdk = 29
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.10.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")


    // Room 核心库
    implementation("androidx.room:room-runtime:2.6.0")

    // 如果你使用的是 Kotlin，添加以下依赖
    implementation("androidx.room:room-ktx:2.6.0")

    // 注解处理器用于生成 DAO 接口和类
    kapt("androidx.room:room-compiler:2.6.0")

}