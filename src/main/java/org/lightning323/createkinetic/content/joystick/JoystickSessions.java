package org.lightning323.createkinetic.content.joystick;

import java.util.UUID;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;

public final class JoystickSessions {
   private JoystickSessions() {
   }

   @SubscribeEvent
   public static void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
      disconnectIfControlling(event.getEntity());
   }

   @SubscribeEvent
   public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
      disconnectIfControlling(event.getEntity());
   }

   @SubscribeEvent
   public static void onLevelUnload(LevelEvent.Unload event) {
      LevelAccessor var2 = event.getLevel();
      if (var2 instanceof Level level) {
         if (!level.isClientSide) {
            JoystickBlockEntity.clearSessionsForLevel(level);
         }
      }

   }

   @SubscribeEvent
   public static void onServerStopping(ServerStoppingEvent event) {
      for(ServerLevel level : event.getServer().getAllLevels()) {
         JoystickBlockEntity.clearSessionsForLevel(level);
      }

   }

   private static void disconnectIfControlling(Player player) {
      if (!player.level().isClientSide) {
         UUID uuid = player.getUUID();
         JoystickBlockEntity be = JoystickBlockEntity.findActiveSession(uuid);
         if (be != null) {
            be.disconnectUser();
         }

      }
   }
}
