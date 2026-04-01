package com.lightning323.createkinetic.blocks.rudder;

import com.lightning323.createkinetic.items.RudderBladeItem;
import com.lightning323.createkinetic.registries.KineticBlockEntities;
import com.lightning323.createkinetic.registries.KineticItems;
import com.lightning323.createkinetic.ship.KineticShipControl;
import com.simibubi.create.AllItems;
import com.simibubi.create.content.kinetics.base.DirectionalKineticBlock;
import com.simibubi.create.foundation.block.IBE;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import com.tterrag.registrate.util.nullness.NonNullBiConsumer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ModelFile;

public class RudderBlock extends DirectionalKineticBlock implements IBE<RudderBlockEntity> {
    //, TransformableBlock {
    public RudderBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(PLANE_ROTATION, 0) // Set default direction
        );
    }

    public void onPlace(BlockState state, Level world, BlockPos pos, BlockState oldState, boolean notify) {
        if (!world.isClientSide) {//Always is server side when rotating the block
            KineticShipControl controller = KineticShipControl.getOrAddController((ServerLevel) world, pos);
            if (controller != null) controller.addRudder(pos);
        }
    }

    public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean moved) {
        if (world.isClientSide) {
            return;
        }
        if (newState.isAir() || !newState.is(state.getBlock())) {
            KineticShipControl controller = KineticShipControl.getOrAddController((ServerLevel) world, pos);
            if (controller != null) controller.removeRudder(pos);
        }
    }


    //Important for identifying the block entity
    @Override
    public Class<RudderBlockEntity> getBlockEntityClass() {
        return RudderBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends RudderBlockEntity> getBlockEntityType() {
        return KineticBlockEntities.RUDDER.get();
    }

    @Override
    public Direction.Axis getRotationAxis(BlockState state) {
        return state.getValue(FACING).getAxis();
    }

    @Override
    public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
        return face == state.getValue(FACING).getOpposite();
    }

    /**
     * Rotation / orientation related stuff
     * <p>
     * DIRECTION - tells us the direction of the block
     * FACE_ANGLE - tells us the angle of the block on the face, this is purely aesthetic and serves no functional purpose
     *
     */
    public static NonNullBiConsumer<DataGenContext<Block, RudderBlock>, RegistrateBlockstateProvider> getBlockstateDefinition() {
        return (c, p) -> {
            ModelFile model = p.models().getExistingFile(p.modLoc("block/rudder/base"));
            ModelFile modelRotated = p.models().getExistingFile(p.modLoc("block/rudder/base90"));

            p.getVariantBuilder(c.get()).forAllStates(state -> {
                Direction facing = state.getValue(DirectionalKineticBlock.FACING);
                int planeRot = state.getValue(RudderBlock.PLANE_ROTATION); // 0, 1, 2, 3

                int xRot = 90;
                int yRot = 0;

                switch (facing) {
                    case SOUTH:
                        yRot = 180;
                        break;
                    case WEST:
                        yRot = 270;
                        break;
                    case EAST:
                        yRot = 90;
                        break;
                    case UP:
                        xRot = 270 + 90;
                        break;
                    case DOWN:
                        xRot = 90 + 90;
                        break;
                }

                if (planeRot == 1 || planeRot == 3) {
                    return ConfiguredModel.builder()
                            .modelFile(modelRotated)
                            .rotationX(xRot)
                            .rotationY(yRot)
                            .build();
                } else {
                    return ConfiguredModel.builder()
                            .modelFile(model)
                            .rotationX(xRot)
                            .rotationY(yRot)
                            .build();
                }
            });
        };
    }

    //Aesthetic only property to tell the block which direction to be on a face
    public static final IntegerProperty PLANE_ROTATION = IntegerProperty.create("plane_rot", 0, 3);

    @Override
    public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn,
                                 BlockHitResult hit) {
        super.use(state, worldIn, pos, player, handIn, hit);

        BlockEntity blockEntity = worldIn.getBlockEntity(pos);

        if (blockEntity instanceof RudderBlockEntity rbe) {
            ItemStack heldItem = player.getItemInHand(handIn);
            boolean isHand = heldItem.isEmpty() && handIn == InteractionHand.MAIN_HAND;
//            boolean wrenched = AllItems.WRENCH.isIn(heldItem);
            if (heldItem.getItem() instanceof RudderBladeItem rb) {
                rbe.rudderBlade = rb;
                heldItem.shrink(1);
            } else if (rbe.rudderBlade != null) {
                ItemStack stack = new ItemStack(rbe.rudderBlade);
                player.getInventory().add(stack);
                rbe.rudderBlade = null;
            }


        }
        return InteractionResult.PASS;
    }


    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(PLANE_ROTATION);
        super.createBlockStateDefinition(builder);
    }


    @Override
    public BlockState getRotatedBlockState(BlockState originalState, Direction targetedFace) {
        Direction currentFacing = originalState.getValue(FACING);

        // HOOK: If clicking the face the block is pointing at (or the back of it)
        if (targetedFace.getAxis() == currentFacing.getAxis()) {
            int currentPlane = originalState.getValue(PLANE_ROTATION);
            int nextPlane = (currentPlane + 1) % 4; // Cycles 0, 1, 2, 3, 0...
            System.out.println("Rotating plane from " + currentPlane + " to " + nextPlane);
            return originalState.setValue(PLANE_ROTATION, nextPlane);
        }

        // Otherwise, perform the standard Create rotation (switching North to East, etc.)
        return super.getRotatedBlockState(originalState, targetedFace);
    }

}