/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.item.ItemStack
 */
package org.lightning323.createkinetic.content.blocks.sable_track;

import org.lightning323.createkinetic.registry.KineticItems;
import net.minecraft.world.item.ItemStack;

public enum SableTrackPart {
    NONE(SableTrackRole.MOUNT, 0.0, 0.0, 0.0, 0.0, 0, 1.0f),
    SMALL_SUSPENSION(SableTrackRole.SUSPENSION, 0.55, 0.6, 1.05, 1.0, 2, 0.75f),
    SUSPENSION(SableTrackRole.SUSPENSION, 0.75, 0.75, 1.35, 1.05, 3, 1.0f),
    LARGE_SUSPENSION(SableTrackRole.SUSPENSION, 0.95, 0.95, 1.65, 1.15, 4, 1.25f),
    SMALL_DRIVE(SableTrackRole.DRIVE, 0.6, 0.5, 1.5, 1.15, 2, 0.8f),
    DRIVE(SableTrackRole.DRIVE, 0.8, 0.55, 1.9, 1.25, 2, 1.0f),
    LARGE_DRIVE(SableTrackRole.DRIVE, 1.0, 0.7, 2.35, 1.35, 3, 1.25f);

    private final SableTrackRole role;
    private final double radius;
    private final double suspensionTravel;
    private final double driveMultiplier;
    private final double sideGripMultiplier;
    private final int contactSamples;
    private final float visualScale;

    private SableTrackPart(SableTrackRole role, double radius, double suspensionTravel, double driveMultiplier, double sideGripMultiplier, int contactSamples, float visualScale) {
        this.role = role;
        this.radius = radius;
        this.suspensionTravel = suspensionTravel;
        this.driveMultiplier = driveMultiplier;
        this.sideGripMultiplier = sideGripMultiplier;
        this.contactSamples = contactSamples;
        this.visualScale = visualScale;
    }

    public static SableTrackPart fromStack(ItemStack stack) {
        if (stack.is(KineticItems.SMALL_SUSPENSION_TRACK.asItem())) {
            return SMALL_SUSPENSION;
        }
        if (stack.is(KineticItems.SMALL_TRACK_DRIVE_WHEEL.asItem())) {
            return SMALL_DRIVE;
        }
        return NONE;
    }

    public SableTrackRole role() {
        return this.role;
    }

    public boolean appliesPhysics() {
        return this.role.appliesPhysics();
    }

    public double radius() {
        return this.radius;
    }

    public double suspensionTravel() {
        return this.suspensionTravel;
    }

    public double driveMultiplier() {
        return this.driveMultiplier;
    }

    public double sideGripMultiplier() {
        return this.sideGripMultiplier;
    }

    public int contactSamples() {
        return this.contactSamples;
    }

    public float visualScale() {
        return this.visualScale;
    }
}

