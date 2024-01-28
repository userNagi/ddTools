plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp")
}

android {
    signingConfigs {
        create("release") {
            storeFile = file("/Users/fangmingqi/privateWork/ddTools/ddtools")
            storePassword = "fangmingqi"
            keyAlias = "key0"
            keyPassword = "fangmingqi"
        }
    }
    namespace = "com.nagi.ddtools"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.nagi.ddtools"
        minSdk = 29
        targetSdk = 34
        versionCode = 1
        versionName = "1.1"
    }

    buildFeatures {
        viewBinding = true
    }

    buildTypes {
        debug {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }


        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
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
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    //noinspection GradleDependency
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.2")
    //noinspection GradleDependency
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.6.2")
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.6")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.6")

    // Room 核心库
    implementation("androidx.room:room-runtime:2.6.1")
    // 如果你使用的是 Kotlin，添加以下依赖
    implementation("androidx.room:room-ktx:2.6.1")
    // 注解处理器用于生成 DAO 接口和类
    ksp("androidx.room:room-compiler:2.6.1")
    //json解析库
    implementation("com.google.code.gson:gson:2.9.0")
    // OkHttp
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
}