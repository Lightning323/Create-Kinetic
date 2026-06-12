package org.lightning323.createkinetic.content.blocks.gyroscope;

import com.simibubi.create.content.kinetics.base.KineticBlock;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.lightning323.createkinetic.registry.KineticBlockEntityTypes;

public class GyroscopeBlock extends KineticBlock implements IBE<GyroscopeBlockEntity> {
   public static final EnumProperty<Direction> FACING;
   private static final VoxelShape OUTLINE;
   private static final VoxelShape COLLISION_DOWN;
   private static final VoxelShape COLLISION_UP;

   public GyroscopeBlock(Properties properties) {
      super(properties);
      this.registerDefaultState((BlockState)this.defaultBlockState().setValue(FACING, Direction.DOWN));
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
      builder.add(new Property[]{FACING});
      super.createBlockStateDefinition(builder);
   }

   public BlockState getStateForPlacement(BlockPlaceContext context) {
      Direction clicked = context.getClickedFace();
      Direction facing = clicked == Direction.DOWN ? Direction.UP : Direction.DOWN;
      return (BlockState)this.defaultBlockState().setValue(FACING, facing);
   }

   public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
      return OUTLINE;
   }

   public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
      return state.getValue(FACING) == Direction.DOWN ? COLLISION_DOWN : COLLISION_UP;
   }

   public Axis getRotationAxis(BlockState state) {
      return Axis.Y;
   }

   public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
      return face == state.getValue(FACING);
   }

   public Class<GyroscopeBlockEntity> getBlockEntityClass() {
      return GyroscopeBlockEntity.class;
   }

   public BlockEntityType<? extends GyroscopeBlockEntity> getBlockEntityType() {
      return (BlockEntityType) KineticBlockEntityTypes.GYROSCOPE.get();
   }

   static {
      FACING = EnumProperty.create("facing", Direction.class, new Direction[]{Direction.UP, Direction.DOWN});
      OUTLINE = box((double)0.0F, (double)0.0F, (double)0.0F, (double)16.0F, (double)16.0F, (double)16.0F);
      COLLISION_DOWN = box((double)0.0F, (double)0.0F, (double)0.0F, (double)16.0F, (double)9.0F, (double)16.0F);
      COLLISION_UP = box((double)0.0F, (double)7.0F, (double)0.0F, (double)16.0F, (double)16.0F, (double)16.0F);
   }
}
