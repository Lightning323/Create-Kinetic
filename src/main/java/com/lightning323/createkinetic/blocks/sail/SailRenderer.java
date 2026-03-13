//package com.lightning323.createkinetic.blocks.sail;
//
//import com.lightning323.createkinetic.blocks.sailPulley.AbstractSailPulleyRenderer;
//import com.lightning323.createkinetic.blocks.sailPulley.SailPulleyBlock;
//import com.lightning323.createkinetic.blocks.sailPulley.SailPulleyBlockEntity;
//import com.lightning323.createkinetic.blocks.sailPulley.SailPulleyContraption;
//import com.lightning323.createkinetic.registries.KineticItems;
//import com.lightning323.createkinetic.registries.KineticPartialModels;
//import com.lightning323.createkinetic.registries.KineticSpriteShifts;
//import com.mojang.blaze3d.vertex.PoseStack;
//import com.mojang.blaze3d.vertex.VertexConsumer;
//import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
//import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
//import com.simibubi.create.infrastructure.config.AllConfigs;
//import dev.engine_room.flywheel.api.visualization.VisualizationManager;
//import dev.engine_room.flywheel.lib.model.baked.PartialModel;
//import net.createmod.catnip.render.CachedBuffers;
//import net.createmod.catnip.render.SpriteShiftEntry;
//import net.createmod.catnip.render.SuperByteBuffer;
//import net.minecraft.client.renderer.LevelRenderer;
//import net.minecraft.client.renderer.MultiBufferSource;
//import net.minecraft.client.renderer.RenderType;
//import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
//import net.minecraft.core.BlockPos;
//import net.minecraft.core.Direction;
//import net.minecraft.core.Direction.Axis;
//import net.minecraft.util.Mth;
//import net.minecraft.world.level.Level;
//import net.minecraft.world.level.LevelAccessor;
//import net.minecraft.world.level.block.state.BlockState;
//
//public class SailRenderer extends KineticBlockEntityRenderer<T> {
//
//    private PartialModel halfRope;
//    private PartialModel halfMagnet;
//
//
//    @Override
//    public boolean shouldRenderOffScreen(T p_188185_1_) {
//        return true;
//    }
//
//    @Override
//    protected void renderSafe(T be, float partialTicks, PoseStack ms, MultiBufferSource buffer,
//                              int light, int overlay) {
//
//        if (VisualizationManager.supportsVisualization(be.getLevel()))
//            return;
//
//        super.renderSafe(be, partialTicks, ms, buffer, light, overlay);
//        float offset = getOffset(be, partialTicks);
//        boolean running = isRunning(be);
//
//        VertexConsumer vb = buffer.getBuffer(RenderType.solid());
//        scrollCoil(getRotatedCoil(be), getCoilShift(), offset, 1)
//                .light(light)
//                .renderInto(ms, vb);
//
//        Level world = be.getLevel();
//        BlockState blockState = be.getBlockState();
//        BlockPos pos = be.getBlockPos();
//
//        SuperByteBuffer halfMagnet = CachedBuffers.partial(this.halfMagnet, blockState);
//        SuperByteBuffer halfRope = CachedBuffers.partial(this.halfRope, blockState);
//        SuperByteBuffer magnet = renderMagnet(be);
//        SuperByteBuffer rope = renderRope(be);
//
//        if (running || offset == 0)
//            renderAt(world, offset > .25f ? magnet : halfMagnet, offset, pos, ms, vb);
//
//        float f = offset % 1;
//        if (offset > .75f && (f < .25f || f > .75f))
//            renderAt(world, halfRope, f > .75f ? f - 1 : f, pos, ms, vb);
//
//        if (!running)
//            return;
//
//        for (int i = 0; i < offset - 1.25f; i++)
//            renderAt(world, rope, offset - i - 1, pos, ms, vb);
//    }
//
//    public static void renderAt(LevelAccessor world, SuperByteBuffer partial, float offset, BlockPos pulleyPos,
//                                PoseStack ms, VertexConsumer buffer) {
//        BlockPos actualPos = pulleyPos.below((int) offset);
//        int light = LevelRenderer.getLightColor(world, world.getBlockState(actualPos), actualPos);
//        partial.translate(0, -offset, 0)
//                .light(light)
//                .renderInto(ms, buffer);
//    }
//
//
//
//    @Override
//    protected BlockState getRenderedBlockState(T be) {
//        return shaft(getShaftAxis(be));
//    }
//
//    protected SuperByteBuffer getRotatedCoil(T be) {
//        BlockState blockState = be.getBlockState();
//        return CachedBuffers.partialFacing(getCoil(), blockState,
//                Direction.get(Direction.AxisDirection.POSITIVE, getShaftAxis(be)));
//    }
//
//    public static SuperByteBuffer scrollCoil(SuperByteBuffer sbb, SpriteShiftEntry coilShift, float offset, float speedModifier) {
//        if (offset == 0)
//            return sbb;
//        float spriteSize = coilShift.getTarget()
//                .getV1()
//                - coilShift.getTarget()
//                .getV0();
//        offset *= speedModifier / 2;
//        double coilScroll = -(offset + 3 / 16f) - Math.floor((offset + 3 / 16f) * -2) / 2;
//        return sbb.shiftUVScrolling(coilShift, (float) coilScroll * spriteSize);
//    }
//
//    @Override
//    public int getViewDistance() {
//        return AllConfigs.server().kinetics.maxRopeLength.get();
//    }
//
//    public SailRenderer(BlockEntityRendererProvider.Context context) {
//        super(context);
//        this.halfRope = KineticPartialModels.ROPE_HALF;
//        this.halfMagnet = KineticPartialModels.ROPE_HALF_MAGNET;
//    }
//
//    @Override
//    protected Axis getShaftAxis(SailBlockEntity be) {
//        return be.getBlockState()
//                .getValue(SailPulleyBlock.HORIZONTAL_AXIS);
//    }
//
//    @Override
//    protected PartialModel getCoil() {
//        return KineticPartialModels.SAIL_COIL;
//    }
//
//    @Override
//    protected SuperByteBuffer renderRope(SailBlockEntity be) {
//        BlockState state = be.getBlockState();// Get the axis the pulley is placed on
//        Axis axis = state.getValue(SailPulleyBlock.HORIZONTAL_AXIS);
//
//        SuperByteBuffer buffer = CachedBuffers.block(KineticItems.PULLEY_SAIL_CLOTH.getDefaultState());
//        if (axis == Axis.Z) {
//            buffer.rotateCentered((float) (Math.PI / 2), Axis.Y);
//        }
//        return buffer;
//    }
//
//    @Override
//    protected SuperByteBuffer renderMagnet(SailBlockEntity be) {
//        BlockState state = be.getBlockState();// Get the axis the pulley is placed on
//        Axis axis = state.getValue(SailPulleyBlock.HORIZONTAL_AXIS);
//
//        SuperByteBuffer buffer = CachedBuffers.block(KineticItems.PULLEY_SAIL_MAGNET.getDefaultState());
//        if (axis == Axis.Z) {
//            buffer.rotateCentered((float) (Math.PI / 2), Axis.Y);
//        }
//        return buffer;
//    }
//
//    @Override
//    protected float getOffset(SailPulleyBlockEntity be, float partialTicks) {
//        return getBlockEntityOffset(partialTicks, be);
//    }
//
//    @Override
//    protected boolean isRunning(SailPulleyBlockEntity be) {
//        return isPulleyRunning(be);
//    }
//
//    public static boolean isPulleyRunning(SailPulleyBlockEntity be) {
//        return be.running || be.mirrorParent != null || be.isVirtual();
//    }
//
//    @Override
//    protected SpriteShiftEntry getCoilShift() {
//        return KineticSpriteShifts.SAIL_COIL;
//    }
//
//    public static float getBlockEntityOffset(float partialTicks, SailPulleyBlockEntity blockEntity) {
//        float offset = blockEntity.getInterpolatedOffset(partialTicks);
//
//        AbstractContraptionEntity attachedContraption = blockEntity.getAttachedContraption();
//        if (attachedContraption != null) {
//            SailPulleyContraption c = (SailPulleyContraption) attachedContraption.getContraption();
//            double entityPos = Mth.lerp(partialTicks, attachedContraption.yOld, attachedContraption.getY());
//            offset = (float) -(entityPos - c.anchor.getY() - c.getInitialOffset());
//        }
//
//        return offset;
//    }
//
//}
