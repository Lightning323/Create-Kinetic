package com.lightning323.createkinetic.blocks.sail.sailPulley;

import com.lightning323.createkinetic.blocks.sail.SailRenderer;
import com.lightning323.createkinetic.registries.KineticBlocks;
import com.lightning323.createkinetic.registries.KineticPartialModels;
import com.lightning323.createkinetic.registries.KineticSpriteShifts;
import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SpriteShiftEntry;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction.Axis;
import net.minecraft.util.Mth;

public class SailPulleyRenderer extends AbstractSailPulleyRenderer<SailPulleyBlockEntity> {

    public SailPulleyRenderer(BlockEntityRendererProvider.Context context) {
        super(context, KineticPartialModels.ROPE_HALF, KineticPartialModels.ROPE_HALF_MAGNET);
    }

    @Override
    protected Axis getShaftAxis(SailPulleyBlockEntity be) {
        return be.getBlockState()
                .getValue(SailPulleyBlock.HORIZONTAL_AXIS);
    }

    @Override
    protected PartialModel getCoil() {
        return KineticPartialModels.SAIL_COIL;
    }

    @Override
    protected SuperByteBuffer renderMagnet(SailPulleyBlockEntity be) {
        return SailRenderer.renderSailAppendage(CachedBuffers.block(KineticBlocks.SAIL_MAGNET.getDefaultState()), be.getBlockState());
    }

    @Override
    protected SuperByteBuffer renderRope(SailPulleyBlockEntity be) {
        return SailRenderer.renderSailAppendage(CachedBuffers.block(KineticBlocks.SAIL_CLOTH.getDefaultState()), be.getBlockState());
    }

    @Override
    protected float getOffset(SailPulleyBlockEntity be, float partialTicks) {
        return getBlockEntityOffset(partialTicks, be);
    }

    @Override
    protected boolean isRunning(SailPulleyBlockEntity be) {
        return isPulleyRunning(be);
    }

    public static boolean isPulleyRunning(SailPulleyBlockEntity be) {
        return be.running || be.mirrorParent != null || be.isVirtual();
    }

    @Override
    protected SpriteShiftEntry getCoilShift() {
        return KineticSpriteShifts.SAIL_COIL;
    }

    public static float getBlockEntityOffset(float partialTicks, SailPulleyBlockEntity blockEntity) {
        float offset = blockEntity.getInterpolatedOffset(partialTicks);

        AbstractContraptionEntity attachedContraption = blockEntity.getAttachedContraption();
        if (attachedContraption != null) {
            SailPulleyContraption c = (SailPulleyContraption) attachedContraption.getContraption();
            double entityPos = Mth.lerp(partialTicks, attachedContraption.yOld, attachedContraption.getY());
            offset = (float) -(entityPos - c.anchor.getY() - c.getInitialOffset());
        }

        return offset;
    }

}
