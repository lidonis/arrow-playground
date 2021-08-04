import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val arrow_kt_version = "0.13.2"

plugins {
    kotlin("jvm") version "1.5.21"
}

group = "fr.lidonis"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}


dependencies {
    implementation(platform("io.arrow-kt:arrow-stack:$arrow_kt_version"))
    implementation("io.arrow-kt:arrow-core")
}


tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}