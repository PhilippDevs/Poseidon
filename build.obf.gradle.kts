import org.jetbrains.kotlin.gradle.dsl.JvmTarget

// ─── 1.21.9 – 1.21.11 build script (obfuscated, Java 21) ────────────────────
// Used for Minecraft 1.21.9, 1.21.10, and 1.21.11. These versions are still
// obfuscated, so we use "net.fabricmc.fabric-loom-remap" and "modImplementation".

plugins {
    id("net.fabricmc.fabric-loom-remap")
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

    // Use official Mojang mappings (Yarn won't be available after 1.21.11)
    mappings(loom.officialMojangMappings())

    modImplementation("net.fabricmc:fabric-loader:${providers.gradleProperty("loader_version").get()}")
    modImplementation("net.fabricmc.fabric-api:fabric-api:${providers.gradleProperty("fabric_version").get()}")
    modImplementation("net.fabricmc:fabric-language-kotlin:${providers.gradleProperty("fabric_kotlin_version").get()}")
}

tasks.processResources {
    val r = mapOf(
        "version"               to project.version,
        "mod_mc_dep"            to providers.gradleProperty("mod_mc_dep").get(),
        "loader_version"        to providers.gradleProperty("loader_version").get(),
        "fabric_kotlin_version" to providers.gradleProperty("fabric_kotlin_version").get()
    )
    inputs.properties(r)
    filesMatching("fabric.mod.json") { expand(r) }
}

tasks.withType<JavaCompile>().configureEach {
    options.release = 21
}

kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_21
    }
}

java {
    withSourcesJar()
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

publishing {
    publications {
        register<MavenPublication>("mavenJava") {
            from(components["java"])
        }
    }
}
