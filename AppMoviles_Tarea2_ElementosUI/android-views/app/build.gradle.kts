plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "mx.escom.catalogoui.views"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "mx.escom.catalogoui.views"
        minSdk = 24
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        release {
            optimization {
                enable = false
            }
            // Se firma con la llave de depuración para que el APK de release se pueda
            // instalar directo, sin configurar un keystore propio.
            signingConfig = signingConfigs.getByName("debug")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.viewpager2)
    implementation(libs.androidx.swiperefreshlayout)
    implementation(libs.material)
    implementation(libs.coil)
    implementation(libs.coil.network.okhttp)
}
