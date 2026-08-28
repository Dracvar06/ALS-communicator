import java.util.Properties
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
}

// Reads keystore.properties if it exists, so a release build is signed with the
// real key. The file holds passwords and is never committed, so a fresh clone
// simply builds an unsigned release rather than failing. See docs/PUBLISHING.md.
val keystoreProperties = Properties().apply {
    val file = rootProject.file("keystore.properties")
    if (file.exists()) file.inputStream().use { load(it) }
}

android {
    namespace = "cat.merce.comunicador"
    compileSdk = 37

    signingConfigs {
        if (keystoreProperties.containsKey("storeFile")) {
            create("release") {
                storeFile = file(keystoreProperties.getProperty("storeFile"))
                storePassword = keystoreProperties.getProperty("storePassword")
                keyAlias = keystoreProperties.getProperty("keyAlias")
                keyPassword = keystoreProperties.getProperty("keyPassword")
            }
        }
    }

    defaultConfig {
        applicationId = "cat.merce.comunicador"
        // 26 = Android 8.0. Set by the project brief.
        minSdk = 26
        targetSdk = 37
        versionCode = 5
        versionName = "0.5"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            signingConfig = signingConfigs.findByName("release")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        compose = true
        // So the version can be shown on screen. Without a cable it is the
        // only way to tell which build is on the tablet.
        buildConfig = true
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
