plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "cat.merce.comunicador"
    compileSdk = 37

    defaultConfig {
        applicationId = "cat.merce.comunicador"
        // 26 = Android 8.0. Set by the project brief.
        minSdk = 26
        targetSdk = 37
        versionCode = 1
        versionName = "0.1"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        compose = true
    }
}

// Prints the scan walkthrough: ./gradlew walkthrough
// A reading aid. It runs the class from the test sources, so nothing it needs
// is ever packaged into the app.
tasks.register<JavaExec>("walkthrough") {
    group = "documentation"
    description = "Prints a step by step walkthrough of the scan state machine."
    val unitTests = tasks.named<Test>("testDebugUnitTest")
    dependsOn("compileDebugUnitTestKotlin")
    classpath = files({ unitTests.get().classpath })
    mainClass.set("cat.merce.comunicador.scan.ScanWalkthroughKt")
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity.compose)
    // lifecycleScope, for loading the model off the main thread.
    implementation(libs.androidx.lifecycle.runtime.ktx)

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    debugImplementation(libs.androidx.compose.ui.tooling)

    // Runs on the laptop's JVM, no emulator required. This is what the scan/
    // and input/ layers are tested with.
    testImplementation(libs.junit)
}
