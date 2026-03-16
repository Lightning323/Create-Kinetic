package com.lightning323.createkinetic.blocks.sail;

import com.lightning323.createkinetic.blocks.sail.sailPulley.AbstractSailPulleyRenderer;
import com.lightning323.createkinetic.blocks.sail.sailPulley.SailBlockBase;
import com.lightning323.createkinetic.blocks.sail.sailPulley.SailPulleyBlock;
import com.lightning323.createkinetic.registries.KineticBlocks;
import com.lightning323.createkinetic.registries.KineticPartialModels;
import com.lightning323.createkinetic.registries.KineticSpriteShifts;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SpriteShiftEntry;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.state.BlockState;

public class SailRenderer extends AbstractSailPulleyRenderer<RetractableSailBlockEntity> {

    public SailRenderer(BlockEntityRendererProvider.Context context) {
        super(context, KineticPartialModels.ROPE_HALF, KineticPartialModels.ROPE_HALF_WEIGHT);
    }

    @Override
    protected Axis getShaftAxis(RetractableSailBlockEntity be) {
        return be.getBlockState()
                .getValue(SailPulleyBlock.HORIZONTAL_AXIS);
    }

    @Override
    protected PartialModel getCoil() {
        return KineticPartialModels.SAIL_COIL;
    }

    @Override
    protected SuperByteBuffer renderMagnet(RetractableSailBlockEntity be) {
        return SailRenderer.renderSailAppendage(CachedBuffers.block(KineticBlocks.SAIL_WEIGHT.getDefaultState()), be.getBlockState());
    }

    @Override
    protected SuperByteBuffer renderRope(RetractableSailBlockEntity be) {
        return SailRenderer.renderSailAppendage(CachedBuffers.block(KineticBlocks.SAIL_CLOTH.getDefaultState()), be.getBlockState());
    }

    public static SuperByteBuffer renderSailAppendage(SuperByteBuffer buffer, BlockState state) {
        return buffer;
    }


    @Override
    protected float getOffset(RetractableSailBlockEntity be, float partialTicks) {
        return getBlockEntityOffset(partialTicks, be);
    }

    @Override
    protected boolean isRunning(RetractableSailBlockEntity be) {
        return isPulleyRunning(be);
    }

    public static boolean isPulleyRunning(RetractableSailBlockEntity be) {
        return be.running || be.mirrorParent != null || be.isVirtual();
    }

    @Override
    protected SpriteShiftEntry getCoilShift() {
        return KineticSpriteShifts.SAIL_COIL;
    }

    public static float getBlockEntityOffset(float partialTicks, RetractableSailBlockEntity blockEntity) {
        float offset = blockEntity.getInterpolatedOffset(partialTicks);
        return offset;
    }

}
