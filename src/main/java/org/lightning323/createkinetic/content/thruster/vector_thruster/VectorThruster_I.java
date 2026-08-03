package org.lightning323.createkinetic.content.thruster.vector_thruster;

import net.minecraft.util.Mth;

public interface VectorThruster_I {

    public float getInterpolatedVectorX(float partialTick);

    public float getInterpolatedVectorY(float partialTick);

    public float getInterpolatedFlapProgress(float partialTick);
}
