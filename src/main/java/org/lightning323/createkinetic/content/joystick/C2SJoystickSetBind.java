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

public record C2SJoystickSetBind(BlockPos pos, int slot, String keyName) implements CustomPacketPayload {
   public static final Type<C2SJoystickSetBind> TYPE = new Type(ResourceLocation.fromNamespaceAndPath(CreateKinetic.MOD_ID, "joystick_set_bind"));
   public static final StreamCodec<FriendlyByteBuf, C2SJoystickSetBind> STREAM_CODEC;

   public Type<? extends CustomPacketPayload> type() {
      return TYPE;
   }

   public static void handleOnServer(C2SJoystickSetBind msg, IPayloadContext ctx) {
      Player var3 = ctx.player();
      if (var3 instanceof ServerPlayer player) {
         if (msg.slot >= 0 && msg.slot < JoystickBlockEntity.BIND_COUNT) {
            BlockEntity be = player.level().getBlockEntity(msg.pos);
            if (be instanceof JoystickBlockEntity) {
               JoystickBlockEntity joystick = (JoystickBlockEntity)be;
               joystick.setBinding(msg.slot, msg.keyName);
            }

         }
      }
   }

   static {
      STREAM_CODEC = StreamCodec.composite(BlockPos.STREAM_CODEC, C2SJoystickSetBind::pos, ByteBufCodecs.VAR_INT, C2SJoystickSetBind::slot, ByteBufCodecs.STRING_UTF8, C2SJoystickSetBind::keyName, C2SJoystickSetBind::new);
   }
}
