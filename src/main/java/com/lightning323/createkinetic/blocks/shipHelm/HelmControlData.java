package com.lightning323.createkinetic.blocks.shipHelm;

import net.minecraft.core.Direction;

import java.util.Objects;

public class HelmControlData {
    public float forwardImpulse;
    public float leftImpulse;
    public float upImpulse;
    public boolean sprintOn;

    public HelmControlData set(HelmControlData data) {
        this.forwardImpulse = data.forwardImpulse;
        this.leftImpulse = data.leftImpulse;
        this.upImpulse = data.upImpulse;
        this.sprintOn = data.sprintOn;
        return this;
    }

    // Data Class standard overrides
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HelmControlData that = (HelmControlData) o;
        return Float.compare(that.forwardImpulse, forwardImpulse) == 0 &&
                Float.compare(that.leftImpulse, leftImpulse) == 0 &&
                Float.compare(that.upImpulse, upImpulse) == 0 &&
                sprintOn == that.sprintOn;
    }

    @Override
    public int hashCode() {
        return Objects.hash(forwardImpulse, leftImpulse, upImpulse, sprintOn);
    }

    @Override
    public String toString() {
        return "ControlData(" +
                ", forwardImpulse=" + forwardImpulse +
                ", leftImpulse=" + leftImpulse +
                ", upImpulse=" + upImpulse +
                ", sprintOn=" + sprintOn +
                ')';
    }
}