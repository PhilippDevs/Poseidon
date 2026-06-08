import org.jetbrains.kotlin.gradle.dsl.JvmTarget

// ─── 26.1 build script (non-obfuscated, Java 25) ─────────────────────────────
// Used for Minecraft 26.1.x and above. Minecraft source is no longer obfuscated,
// so we use "net.fabricmc.fabric-loom" (no remapping) and plain "implementation".

plugins {
    id("net.fabricmc.fabric-loom")
    `maven-publish`
    kotlin("jvm") version "2.4.0"
}

val stonecutter = extensions.getByType(dev.kikugie.stonecutter.build.StonecutterBuildExtension::class)
val ver = stonecutter.current.version

version = "${providers.gradleProperty("mod_version").get()}+$ver"
group = providers.gradleProperty("maven_group").get()

loom {
    splitEnvironmentSourceSets()

    mods {
        register("poseidon") {
            sourceSet(sourceSets.main.get())
            sourceSet(sourceSets.getByName("client"))
        }
    }

    runConfigs.named("client") {
        isIdeConfigGenerated = true
    }
    runConfigs.named("server") {
        isIdeConfigGenerated = false
    }
}

repositories {
    // Add mod repositories here when needed
}

dependencies {
    minecraft("com.mojang:minecraft:${providers.gradleProperty("minecraft_version").get()}")
    // No mappings block needed — 26.1+ is already unobfuscated

    implementation("net.fabricmc:fabric-loader:${providers.gradleProperty("loader_version").get()}")
    implementation("net.fabricmc.fabric-api:fabric-api:${providers.gradleProperty("fabric_version").get()}")
    implementation("net.fabricmc:fabric-language-kotlin:${providers.gradleProperty("fabric_kotlin_version").get()}")
}

tasks.processResources {
    val r = mapOf(
        "version"             to project.version,
        "mod_mc_dep"          to providers.gradleProperty("mod_mc_dep").get(),
        "loader_version"      to providers.gradleProperty("loader_version").get(),
        "fabric_kotlin_version" to providers.gradleProperty("fabric_kotlin_version").get()
    )
    inputs.properties(r)
    filesMatching("fabric.mod.json") { expand(r) }
}

tasks.withType<JavaCompile>().configureEach {
    options.release = 25
}

kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_25
    }
}

java {
    withSourcesJar()
    sourceCompatibility = JavaVersion.VERSION_25
    targetCompatibility = JavaVersion.VERSION_25
}

publishing {
    publications {
        register<MavenPublication>("mavenJava") {
            from(components["java"])
        }
    }
}
