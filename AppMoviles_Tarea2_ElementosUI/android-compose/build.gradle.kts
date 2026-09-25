// Archivo de compilación principal: los plugins se aplican en el módulo :app.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.compose) apply false
}
