/*
 * Decompiled with CFR 0.152.
 */
package org.lightning323.createkinetic.mixin_interface;

public interface WheelMountOffsetAccess {

    public double kinetic$getLerpedLateralOffset(float var1);

    public double kinetic$getLerpedLongitudinalOffset(float var1);

    public double kinetic$getLerpedHeightOffset(float var1);

    public double kinetic$getLerpedYaw(float var1);

    public boolean kinetic$isVisualSuspensionHidden();

    public void kinetic$toggleVisualSuspensionHidden();

    public int kinetic$getClientSteeringSignalLeft();

    public int kinetic$getClientSteeringSignalRight();
}

