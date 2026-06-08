pluginManagement {
	repositories {
		maven {
			name = "Fabric"
			url = uri("https://maven.fabricmc.net/")
		}
		maven("https://maven.kikugie.dev/snapshots")
		mavenCentral()
		gradlePluginPortal()
	}

	plugins {
		// net.fabricmc.fabric-loom       → used for 26.1+ (non-obfuscated)
		// net.fabricmc.fabric-loom-remap → used for 1.21.x (obfuscated, needs remapping)
		id("net.fabricmc.fabric-loom") version providers.gradleProperty("loom_version").get()
		id("net.fabricmc.fabric-loom-remap") version providers.gradleProperty("loom_version").get()
	}
}

plugins {
	id("dev.kikugie.stonecutter") version "0.9"
}

stonecutter.create(rootProject) {
	// 1.21.9-1.21.11 are obfuscated → use the obf build script
	versions("1.21.9", "1.21.10", "1.21.11").buildscript = "build.obf.gradle.kts"
	// 26.1 is non-obfuscated (Mojang-native) → use the regular build script
	version("26.1").buildscript = "build.gradle.kts"
	// The source code in git is written targeting 26.1
	vcsVersion = "26.1"
}

rootProject.name = "poseidon"
