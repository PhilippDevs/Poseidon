plugins {
    id("dev.kikugie.stonecutter")
}

// The version that is currently active for IDE development.
// Must match vcsVersion in settings.gradle.kts.
stonecutter active "26.1"

stonecutter parameters {
    // --- Global string replacements ---
    // These run automatically on every file when building for older versions,
    // so you can write 26.1-style code in git and Stonecutter handles the rest.
    //
    // When building for 1.21.10 and below, Minecraft uses ResourceLocation
    // instead of Identifier. Use //? if < 1.21.11 blocks for this in your code
    // since "Identifier" is too generic for a safe global string replace.
    //
    // Safe global renames for 1.21.x vs 26.1:
    replacements {
        string(current.parsed < "26.1") {
            // 26.1 renamed ClientCommandManager → ClientCommands
            replace("ClientCommands", "ClientCommandManager")
            // 26.1 renamed ClickType → ContainerInput
            replace("ContainerInput", "ClickType")
        }
    }
}
