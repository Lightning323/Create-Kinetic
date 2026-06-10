package org.lightning323.createkinetic.content.joystick;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.lightning323.createkinetic.CreateKinetic;

public record C2SJoystickUseMouseInput(BlockPos pos, boolean useMouseInput) implements CustomPacketPayload {
   public static final Type<C2SJoystickUseMouseInput> TYPE = new Type(ResourceLocation.fromNamespaceAndPath(CreateKinetic.MOD_ID, "joystick_use_mouse_input"));
   public static final StreamCodec<FriendlyByteBuf, C2SJoystickUseMouseInput> STREAM_CODEC;

   public Type<? extends CustomPacketPayload> type() {
      return TYPE;
   }

   public static void handleOnServer(C2SJoystickUseMouseInput msg, IPayloadContext ctx) {
      Player var3 = ctx.player();
      if (var3 instanceof ServerPlayer player) {
         BlockEntity be = player.level().getBlockEntity(msg.pos);
         if (be instanceof JoystickBlockEntity joystick) {
            joystick.setUseMouseInput(msg.useMouseInput);
         }

      }
   }

   static {
      STREAM_CODEC = StreamCodec.composite(BlockPos.STREAM_CODEC, C2SJoystickUseMouseInput::pos, ByteBufCodecs.BOOL, C2SJoystickUseMouseInput::useMouseInput, C2SJoystickUseMouseInput::new);
   }
}
