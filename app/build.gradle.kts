import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
}

val dicePhysicsNatives by configurations.creating
val diceNativeDirectory = layout.buildDirectory.dir("generated/dicePhysics/jniLibs")
val extractDicePhysicsNatives by tasks.registering(Sync::class) {
    from(dicePhysicsNatives.elements.map { files -> files.map { zipTree(it.asFile) } }) {
        include("*.so")
        into("arm64-v8a")
    }
    into(diceNativeDirectory)
}

android {
    namespace = "com.zhifou.fortune"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.zhifou.fortune"
        minSdk = 26
        targetSdk = 36
        versionCode = 18
        versionName = "1.1.1"

        ndk {
            abiFilters += listOf("arm64-v8a")
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        compose = true
    }

    androidResources {
        noCompress += "onnx"
    }

    sourceSets.getByName("main").jniLibs.srcDir(diceNativeDirectory)
}

tasks.named("preBuild") { dependsOn(extractDicePhysicsNatives) }

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_17)
    }
}

dependencies {
    val composeBom = platform("androidx.compose:compose-bom:2026.06.01")
    implementation(composeBom)
    implementation("androidx.activity:activity-compose:1.13.0")
    implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material3:material3-adaptive-navigation-suite")
    implementation("androidx.compose.material3.adaptive:adaptive")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.core:core-ktx:1.17.0")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.7")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.7")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7")
    implementation("androidx.navigation:navigation-compose:2.8.5")
    implementation("com.google.android.material:material:1.14.0")
    implementation("io.github.kyant0:backdrop:1.0.6")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("cn.6tail:lunar:1.7.7")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.9.0")
    implementation("org.json:json:20240303")
    implementation(files("libs/sherpa-onnx-1.13.2-arm64.aar"))

    val gdxVersion = "1.14.2"
    implementation("com.badlogicgames.gdx:gdx:$gdxVersion")
    implementation("com.badlogicgames.gdx:gdx-bullet:$gdxVersion")
    val filamentVersion = "1.68.4"
    implementation("com.google.android.filament:filament-android:$filamentVersion") {
        exclude(group = "org.jetbrains.kotlin")
    }
    implementation("com.google.android.filament:gltfio-android:$filamentVersion") {
        exclude(group = "org.jetbrains.kotlin")
    }
    dicePhysicsNatives("com.badlogicgames.gdx:gdx-platform:$gdxVersion:natives-arm64-v8a")
    dicePhysicsNatives("com.badlogicgames.gdx:gdx-bullet-platform:$gdxVersion:natives-arm64-v8a")
    testRuntimeOnly("com.badlogicgames.gdx:gdx-platform:$gdxVersion:natives-desktop")
    testRuntimeOnly("com.badlogicgames.gdx:gdx-bullet-platform:$gdxVersion:natives-desktop")
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.json:json:20240303")
    debugImplementation("androidx.compose.ui:ui-tooling")
}
