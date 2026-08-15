// Top-level build file. Plugins are declared here without being applied, so the
// :app module can apply them without repeating version numbers.
//
// There is no Kotlin plugin here: since AGP 9.0 the Android plugin brings its
// own Kotlin support, and applying org.jetbrains.kotlin.android alongside it is
// an error.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.compose) apply false
}
