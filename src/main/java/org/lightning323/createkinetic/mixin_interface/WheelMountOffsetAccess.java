/*
 * Decompiled with CFR 0.152.
 */
package org.lightning323.createkinetic.mixin_interface;

public interface WheelMountOffsetAccess {


    public double kinetic$adjustLateralOffset(int var1);

    public double kinetic$adjustLongitudinalOffset(int var1);

    public double kinetic$adjustHeightOffset(int var1, boolean sideInteraction);

    public double kinetic$adjustTuning(String var1, int var2);

    public void kinetic$resetTuning();

    public double kinetic$getTuning(String var1);

    public double kinetic$getLerpedLateralOffset(float var1);

    public double kinetic$getLerpedLongitudinalOffset(float var1);

    public double kinetic$getLerpedHeightOffset(float var1);

    public double kinetic$getLerpedYaw(float var1);

    public boolean kinetic$isVisualSuspensionHidden();

    public void kinetic$toggleVisualSuspensionHidden();

    public int kinetic$getClientSteeringSignalLeft();

    public int kinetic$getClientSteeringSignalRight();
}

