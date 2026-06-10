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

public record C2SJoystickButton(BlockPos pos, boolean pressed) implements CustomPacketPayload {
   public static final Type<C2SJoystickButton> TYPE = new Type(ResourceLocation.fromNamespaceAndPath(CreateKinetic.MOD_ID, "joystick_button"));
   public static final StreamCodec<FriendlyByteBuf, C2SJoystickButton> STREAM_CODEC;

   public Type<? extends CustomPacketPayload> type() {
      return TYPE;
   }

   public static void handleOnServer(C2SJoystickButton msg, IPayloadContext ctx) {
      Player var3 = ctx.player();
      if (var3 instanceof ServerPlayer player) {
         BlockEntity be = player.level().getBlockEntity(msg.pos);
         if (be instanceof JoystickBlockEntity joystick) {
            if (joystick.checkUser(player.getUUID())) {
               joystick.setButtonPressedFromController(msg.pressed);
            }
         }

      }
   }

   static {
      STREAM_CODEC = StreamCodec.composite(BlockPos.STREAM_CODEC, C2SJoystickButton::pos, ByteBufCodecs.BOOL, C2SJoystickButton::pressed, C2SJoystickButton::new);
   }
}
