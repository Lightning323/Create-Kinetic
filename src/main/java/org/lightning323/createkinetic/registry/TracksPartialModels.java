/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.engine_room.flywheel.lib.model.baked.PartialModel
 *  net.minecraft.resources.ResourceLocation
 */
package org.lightning323.createkinetic.registry;

import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.minecraft.resources.ResourceLocation;
import org.lightning323.createkinetic.CreateKinetic;

public class TracksPartialModels {
    public static final PartialModel TRACKWORK_WHEELS = TracksPartialModels.block("wheels");
    public static final PartialModel TRACKWORK_COGS = TracksPartialModels.block("cogs");
    public static final PartialModel TRACKWORK_TRACK_LINK = TracksPartialModels.block("track_link");
    public static final PartialModel TRACKWORK_TRACK_LINK_DOWN = TracksPartialModels.block("track_link_down");
    public static final PartialModel TRACKWORK_WRAPPED_LINK = TracksPartialModels.block("wrapped_link");

    private static PartialModel block(String path) {
        return PartialModel.of((ResourceLocation)ResourceLocation.tryBuild((String) CreateKinetic.MOD_ID, (String)("block/" + path)));
    }

    public static void init() {
    }
}

