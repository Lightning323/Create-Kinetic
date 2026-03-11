package com.lightning323.createkinetic.blocks.sailPulley;

import com.lightning323.createkinetic.registries.KineticItems;
import com.lightning323.createkinetic.registries.KineticPartialModels;
import com.lightning323.createkinetic.registries.KineticSpriteShifts;
import com.lightning323.createkinetic.sprite.KineticSpriteShiftEntry;
import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.content.contraptions.pulley.AbstractPulleyRenderer;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SpriteShiftEntry;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction.Axis;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;

public class SailRenderer extends AbstractSailPulleyRenderer<SailBlockEntity> {

    public SailRenderer(BlockEntityRendererProvider.Context context) {
        super(context, KineticPartialModels.ROPE_HALF, KineticPartialModels.ROPE_HALF_MAGNET);
    }

    @Override
    protected Axis getShaftAxis(SailBlockEntity be) {
        return be.getBlockState()
                .getValue(SailPulleyBlock.HORIZONTAL_AXIS);
    }

    @Override
    protected PartialModel getCoil() {
        return KineticPartialModels.SAIL_COIL;
    }

    @Override
    protected SuperByteBuffer renderRope(SailBlockEntity be) {
        BlockState state = be.getBlockState();// Get the axis the pulley is placed on
        Axis axis = state.getValue(SailPulleyBlock.HORIZONTAL_AXIS);

        SuperByteBuffer buffer = CachedBuffers.block(KineticItems.PULLEY_SAIL_CLOTH.getDefaultState());
        if (axis == Axis.Z) {
            buffer.rotateCentered((float) (Math.PI / 2), Axis.Y);
        }
        return buffer;
    }

    @Override
    protected SuperByteBuffer renderMagnet(SailBlockEntity be) {
        BlockState state = be.getBlockState();// Get the axis the pulley is placed on
        Axis axis = state.getValue(SailPulleyBlock.HORIZONTAL_AXIS);

        SuperByteBuffer buffer = CachedBuffers.block(KineticItems.PULLEY_SAIL_MAGNET.getDefaultState());
        if (axis == Axis.Z) {
            buffer.rotateCentered((float) (Math.PI / 2), Axis.Y);
        }
        return buffer;
    }

    @Override
    protected float getOffset(SailBlockEntity be, float partialTicks) {
        return getBlockEntityOffset(partialTicks, be);
    }

    @Override
    protected boolean isRunning(SailBlockEntity be) {
        return isPulleyRunning(be);
    }

    public static boolean isPulleyRunning(SailBlockEntity be) {
        return be.running || be.mirrorParent != null || be.isVirtual();
    }

    @Override
    protected KineticSpriteShiftEntry getCoilShift() {
        return KineticSpriteShifts.SAIL_COIL;
    }

    public static float getBlockEntityOffset(float partialTicks, SailBlockEntity blockEntity) {
        float offset = blockEntity.getInterpolatedOffset(partialTicks);

        AbstractContraptionEntity attachedContraption = blockEntity.getAttachedContraption();
        if (attachedContraption != null) {
            SailContraption c = (SailContraption) attachedContraption.getContraption();
            double entityPos = Mth.lerp(partialTicks, attachedContraption.yOld, attachedContraption.getY());
            offset = (float) -(entityPos - c.anchor.getY() - c.getInitialOffset());
        }

        return offset;
    }

}
