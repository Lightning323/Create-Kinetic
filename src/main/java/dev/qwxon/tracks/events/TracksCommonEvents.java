/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.ryanhcode.sable.sublevel.system.SubLevelPhysicsSystem
 *  net.minecraft.server.level.ServerLevel
 */
package dev.qwxon.tracks.events;

import dev.qwxon.tracks.content.blocks.sable_track.SableTrackBlockEntity;
import dev.ryanhcode.sable.sublevel.system.SubLevelPhysicsSystem;
import net.minecraft.server.level.ServerLevel;

public class TracksCommonEvents {
    public static void physicsTick(SubLevelPhysicsSystem physicsSystem, double timeStep) {
        ServerLevel level = physicsSystem.getLevel();
        SableTrackBlockEntity.applyAllBatchedForces(level, timeStep);
    }
}

