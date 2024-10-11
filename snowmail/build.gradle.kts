plugins {
    kotlin("jvm") version "2.0.10"
    kotlin("plugin.serialization") version "2.0.10"
    alias(libs.plugins.jetbrains.compose)
    alias(libs.plugins.compose.compiler)
    id("application")
}

group = "ca.uwaterloo"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()    // Maven Central repository
    google()          // Google's Maven repository for AndroidX artifacts
    maven { url = uri("https://maven.pkg.jetbrains.space/public/p/compose/dev") }  // JetBrains Compose repository
}


dependencies {
    implementation(libs.datetime)
    implementation(compose.desktop.currentOs)
    implementation("io.ktor:ktor-client-core:2.3.12")
    implementation("io.ktor:ktor-client-cio:2.3.12")
    implementation("io.ktor:ktor-client-serialization:2.3.12")
    implementation("io.ktor:ktor-client-plugins:2.3.12")
    implementation(platform("io.github.jan-tennert.supabase:bom:3.0.0"))
    implementation("io.github.jan-tennert.supabase:postgrest-kt")
    implementation("io.github.jan-tennert.supabase:supabase-kt")
    implementation("io.github.jan-tennert.supabase:auth-kt:3.0.0")
    implementation("org.slf4j:slf4j-simple:2.0.16")

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}

application {
    mainClass.set("ca.uwaterloo.MainKt")
}

