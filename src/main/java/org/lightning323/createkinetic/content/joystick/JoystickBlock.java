package org.lightning323.createkinetic.content.joystick;

import org.lightning323.createkinetic.KineticBlockEntityTypes;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.foundation.block.IBE;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class JoystickBlock extends Block implements EntityBlock, IBE<JoystickBlockEntity>, IWrenchable {
   public static final BooleanProperty POWERED;
   public static final DirectionProperty FACING;
   public static final BooleanProperty IN_USE;
   public static final VoxelShape CHASSIS_SHAPE;
   public static final VoxelShape HANDLE_CORE_SHAPE;
   public static final VoxelShape BUTTON_SHAPE;
   public static final VoxelShape HANDLE_SHAPE;
   private static final VoxelShape COLLISION;

   public JoystickBlock(Properties properties) {
      super(properties);
      this.registerDefaultState((BlockState)((BlockState)((BlockState)this.defaultBlockState().setValue(POWERED, false)).setValue(IN_USE, false)).setValue(FACING, Direction.SOUTH));
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
      builder.add(new Property[]{POWERED, IN_USE, FACING});
   }

   public BlockState getStateForPlacement(BlockPlaceContext context) {
      return (BlockState)this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
   }

   public BlockState rotate(BlockState state, Rotation rotation) {
      return (BlockState)state.setValue(FACING, rotation.rotate((Direction)state.getValue(FACING)));
   }

   public BlockState mirror(BlockState state, Mirror mirror) {
      return this.rotate(state, mirror.getRotation((Direction)state.getValue(FACING)));
   }

   public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
      return CHASSIS_SHAPE;
   }

   public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
      return COLLISION;
   }

   protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
      if (hand == InteractionHand.MAIN_HAND && stack.isEmpty()) {
         BlockEntity var9 = level.getBlockEntity(pos);
         if (var9 instanceof JoystickBlockEntity) {
            JoystickBlockEntity be = (JoystickBlockEntity)var9;
            if (player.isShiftKeyDown()) {
               if (be.hasController()) {
                  if (level.isClientSide) {
                     player.displayClientMessage(Component.translatable("message.createkinetic.joystick.busy"), true);
                  }

                  return ItemInteractionResult.sidedSuccess(level.isClientSide);
               } else {
                  if (!level.isClientSide && player instanceof ServerPlayer) {
                     ServerPlayer server = (ServerPlayer)player;
                     server.openMenu(be, (buf) -> buf.writeBlockPos(pos));
                  }

                  return ItemInteractionResult.sidedSuccess(level.isClientSide);
               }
            } else {
               UUID uuid = player.getUUID();
               if (be.checkAndStartUsing(uuid)) {
                  return ItemInteractionResult.sidedSuccess(level.isClientSide);
               } else {
                  if (level.isClientSide && !be.checkUser(uuid)) {
                     player.displayClientMessage(Component.translatable("message.createkinetic.joystick.busy"), true);
                  }

                  return ItemInteractionResult.sidedSuccess(level.isClientSide);
               }
            }
         } else {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
         }
      } else {
         return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
      }
   }

   public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
      return KineticBlockEntityTypes.JOYSTICK.create(pos, state);
   }

   public Class<JoystickBlockEntity> getBlockEntityClass() {
      return JoystickBlockEntity.class;
   }

   public BlockEntityType<? extends JoystickBlockEntity> getBlockEntityType() {
      return (BlockEntityType) KineticBlockEntityTypes.JOYSTICK.get();
   }

   static {
      POWERED = BlockStateProperties.POWERED;
      FACING = BlockStateProperties.HORIZONTAL_FACING;
      IN_USE = BooleanProperty.create("in_use");
      CHASSIS_SHAPE = Shapes.or(box((double)0.0F, (double)0.0F, (double)0.0F, (double)16.0F, (double)3.0F, (double)16.0F), box((double)5.0F, (double)3.0F, (double)5.0F, (double)11.0F, (double)4.0F, (double)11.0F));
      HANDLE_CORE_SHAPE = Shapes.or(box((double)6.0F, (double)3.0F, (double)6.0F, (double)10.0F, (double)5.0F, (double)10.0F), new VoxelShape[]{box((double)7.0F, (double)5.0F, (double)7.0F, (double)9.0F, (double)15.0F, (double)9.0F), box((double)6.75F, (double)15.0F, (double)6.75F, (double)9.25F, (double)21.0F, (double)9.25F)});
      BUTTON_SHAPE = box((double)7.0F, (double)21.0F, (double)7.0F, (double)9.0F, (double)22.0F, (double)9.0F);
      HANDLE_SHAPE = Shapes.or(HANDLE_CORE_SHAPE, BUTTON_SHAPE);
      COLLISION = box((double)0.0F, (double)0.0F, (double)0.0F, (double)16.0F, (double)4.0F, (double)16.0F);
   }
}
