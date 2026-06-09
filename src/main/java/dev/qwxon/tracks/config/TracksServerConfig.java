/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.neoforged.neoforge.common.ModConfigSpec
 *  net.neoforged.neoforge.common.ModConfigSpec$BooleanValue
 *  net.neoforged.neoforge.common.ModConfigSpec$Builder
 */
package dev.qwxon.tracks.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public class TracksServerConfig {
    public static final ModConfigSpec SPEC;
    public static final ModConfigSpec.BooleanValue ENABLE_RENDER_TUNING_CHEATS;

    public static boolean renderTuningCheatsEnabled() {
        return (Boolean)ENABLE_RENDER_TUNING_CHEATS.get();
    }

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
        builder.push("server");
        ENABLE_RENDER_TUNING_CHEATS = builder.comment("Allows operators to open the in-game tracks render tuning menu with J. Disabled by default.").define("enableRenderTuningCheats", false);
        builder.pop();
        SPEC = builder.build();
    }
}

