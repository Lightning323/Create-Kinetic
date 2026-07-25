package org.lightning323.createkinetic.content.redstone_converter;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;
import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.ScrollValueBehaviour;
import dev.engine_room.flywheel.lib.transform.TransformStack;
import net.createmod.catnip.math.AngleHelper;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.Vec3;
import org.lightning323.createkinetic.registries.PropulsionBlockEntities;

import java.util.List;

public class RedstoneConverterBlockEntity extends SmartBlockEntity {
    public ScrollValueBehaviour outputStrength;

    public RedstoneConverterBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public RedstoneConverterBlockEntity(BlockPos pos, BlockState state) {
        this(PropulsionBlockEntities.REDSTONE_CONVERTER_BLOCK_ENTITY.get(), pos, state);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        outputStrength = new ScrollValueBehaviour(
            Component.translatable("createkinetic.redstone_converter.output_strength"),
            this,
            new ConverterValueBoxSlot()
        ).between(1, 15);
        outputStrength.setValue(15);
        outputStrength.withCallback(this::onStrengthChanged);
        behaviours.add(outputStrength);
    }

    private void onStrengthChanged(int value) {
        if (level == null || level.isClientSide) return;
        level.updateNeighborsAt(worldPosition, getBlockState().getBlock());
        level.updateNeighborsAt(worldPosition.relative(getBlockState().getValue(RedstoneConverterBlock.FACING).getOpposite()), getBlockState().getBlock());
        sendData();
    }

    public static class ConverterValueBoxSlot extends ValueBoxTransform {
        @Override
        public Vec3 getLocalOffset(LevelAccessor level, BlockPos pos, BlockState state) {
            return VecHelper.voxelSpace(8, 7.5f, 8);
        }

        @Override
        public void rotate(LevelAccessor level, BlockPos pos, BlockState state, PoseStack ms) {
            float yRot = AngleHelper.horizontalAngle(state.getValue(BlockStateProperties.HORIZONTAL_FACING)) + 180;
            TransformStack.of(ms)
                .rotateYDegrees(yRot)
                .rotateXDegrees(90);
        }
    }
}
