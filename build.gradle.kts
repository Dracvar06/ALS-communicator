// Top-level build file. Plugins are declared here without being applied, so the
// :app module can apply them without repeating version numbers.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
}
