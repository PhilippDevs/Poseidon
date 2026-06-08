import java.util.Properties
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

// Stonecutter subprojects share this root build script, so Gradle loads the root
// gradle.properties — not versions/<ver>/gradle.properties. Load them explicitly.
val versionProps = Properties().also { p ->
    rootProject.file("versions/$ver/gradle.properties")
        .takeIf { it.exists() }
        ?.inputStream()?.use { p.load(it) }
}
fun prop(key: String): String =
    versionProps.getProperty(key)
        ?: providers.gradleProperty(key).orNull
        ?: error("Property '$key' not found in versions/$ver/gradle.properties or root gradle.properties")

version = "${prop("mod_version")}+$ver"
group = prop("maven_group")

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
        runDir("run/$ver")
    }
    runConfigs.named("server") {
        isIdeConfigGenerated = false
        runDir("run/$ver")
    }
}

repositories {
    // Add mod repositories here when needed
}

dependencies {
    minecraft("com.mojang:minecraft:${prop("minecraft_version")}")
    // No mappings block needed — 26.1+ is already unobfuscated

    implementation("net.fabricmc:fabric-loader:${prop("loader_version")}")
    implementation("net.fabricmc.fabric-api:fabric-api:${prop("fabric_version")}")
    implementation("net.fabricmc:fabric-language-kotlin:${prop("fabric_kotlin_version")}")
}

tasks.processResources {
    val r = mapOf(
        "version"             to project.version,
        "mod_mc_dep"          to prop("mod_mc_dep"),
        "loader_version"      to prop("loader_version"),
        "fabric_kotlin_version" to prop("fabric_kotlin_version")
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
