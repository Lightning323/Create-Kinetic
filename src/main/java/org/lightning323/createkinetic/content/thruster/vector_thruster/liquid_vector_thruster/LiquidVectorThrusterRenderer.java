package org.lightning323.createkinetic.content.thruster.vector_thruster.liquid_vector_thruster;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.foundation.blockEntity.renderer.SmartBlockEntityRenderer;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.state.BlockState;
import org.lightning323.createkinetic.content.thruster.ThrusterDebugRenderer;
import org.lightning323.createkinetic.content.thruster.vector_thruster.VectorRedstoneLinkRenderer;
import org.lightning323.createkinetic.content.thruster.vector_thruster.ion_vector_thruster.IonVectorThrusterRenderer;
import org.lightning323.createkinetic.registries.KineticPartialModels;

public class LiquidVectorThrusterRenderer extends SmartBlockEntityRenderer<LiquidVectorThrusterBlockEntity> {

    public LiquidVectorThrusterRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected void renderSafe(LiquidVectorThrusterBlockEntity be, float partialTick, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        super.renderSafe(be, partialTick, ms, buffer, light, overlay);
        ThrusterDebugRenderer.render(be, ms, buffer);

        if (be == null || be.isRemoved()) return;

        BlockState state = be.getBlockState();
        PartialModel bodyModel = KineticPartialModels.LIQUID_VECTOR_THRUSTER_BODY;
        PartialModel flapTop = KineticPartialModels.LIQUID_VECTOR_THRUSTER_FLAP_TOP;
        PartialModel flapBottom = KineticPartialModels.LIQUID_VECTOR_THRUSTER_FLAP_BOTTOM;
        PartialModel flapLeft = KineticPartialModels.LIQUID_VECTOR_THRUSTER_FLAP_LEFT;
        PartialModel flapRight = KineticPartialModels.LIQUID_VECTOR_THRUSTER_FLAP_RIGHT;

        IonVectorThrusterRenderer.renderThruster(be, partialTick, ms, buffer, light, overlay, state, bodyModel, flapTop, flapBottom, flapLeft, flapRight);

        VectorRedstoneLinkRenderer.renderOnBlockEntity(be, partialTick, ms, buffer, light, overlay);
    }
}
