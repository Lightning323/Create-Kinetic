/*
 * Decompiled with CFR 0.152.
 */
package org.lightning323.createkinetic.content.blocks.sable_track;

public enum SableTrackRole {
    MOUNT(0.0, 0.0, 0.0, 0.0, 0, false),
    SUSPENSION(0.75, 0.75, 0.0, 1.05, 3, true),
    DRIVE(0.8, 0.55, 1.9, 1.25, 2, true);

    private final double radius;
    private final double suspensionTravel;
    private final double driveMultiplier;
    private final double sideGripMultiplier;
    private final int contactSamples;
    private final boolean appliesPhysics;

    private SableTrackRole(double radius, double suspensionTravel, double driveMultiplier, double sideGripMultiplier, int contactSamples, boolean appliesPhysics) {
        this.radius = radius;
        this.suspensionTravel = suspensionTravel;
        this.driveMultiplier = driveMultiplier;
        this.sideGripMultiplier = sideGripMultiplier;
        this.contactSamples = contactSamples;
        this.appliesPhysics = appliesPhysics;
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

    public boolean appliesPhysics() {
        return this.appliesPhysics;
    }
}

