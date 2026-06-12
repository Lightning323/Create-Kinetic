package org.lightning323.createkinetic.content.blocks.joystick;

import org.lightning323.createkinetic.registry.KineticMenuTypes;
import com.simibubi.create.Create;
import com.simibubi.create.content.redstone.link.IRedstoneLinkable;
import com.simibubi.create.content.redstone.link.RedstoneLinkNetworkHandler.Frequency;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.api.block.BlockEntitySubLevelActor;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.createmod.catnip.animation.LerpedFloat;
import net.createmod.catnip.animation.LerpedFloat.Chaser;
import net.createmod.catnip.data.Couple;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

public class JoystickBlockEntity extends SmartBlockEntity implements MenuProvider, BlockEntitySubLevelActor {
   public static final int MAX_STEP = 15;
   public static final int BIND_COUNT;
   public static final int BIND_BUTTON_INDEX;
   public static final int BUTTON_SLOT_1;
   public static final int BUTTON_SLOT_2;
   public static final int FREQUENCY_SLOTS;
   private static final float TILT_CHASE_RATE = 0.35F;
   private static final float BUTTON_CHASE_RATE = 0.35F;
   private static final String DEFAULT_BUTTON_BIND = "key.mouse.left";
   private static final int TRANSMITTER_COUNT;
   private static final int BUTTON_TRANSMITTER_INDEX;
   private static final Map<UUID, JoystickBlockEntity> SERVER_ACTIVE_SESSIONS;
   private @Nullable UUID currentUser;
   private boolean powered;
   private byte tiltX;
   private byte tiltY;
   private boolean buttonPressed;
   private boolean showHud = true;
   private boolean useMouseInput = true;
   private boolean springBack = false;
   private final String[] bindings = createDefaultBindings();
   private final LerpedFloat lerpedTiltX = LerpedFloat.linear();
   private final LerpedFloat lerpedTiltY = LerpedFloat.linear();
   private final LerpedFloat lerpedButtonPress = LerpedFloat.linear();
   private final ItemStackHandler frequencyItems;
   private final Transmitter[] transmitters;
   private boolean networkRegistered;
   private final int[] lastTransmittedStrength;

   public boolean isPowered() {
      return this.powered;
   }

   public float tiltXAt(float partialTicks) {
      return this.lerpedTiltX.getValue(partialTicks);
   }

   public float tiltYAt(float partialTicks) {
      return this.lerpedTiltY.getValue(partialTicks);
   }

   public float buttonPressAt(float partialTicks) {
      return this.lerpedButtonPress.getValue(partialTicks);
   }

   public ItemStackHandler getFrequencyItems() {
      return this.frequencyItems;
   }

   public JoystickBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
      super(type, pos, state);
      this.frequencyItems = new ItemStackHandler(FREQUENCY_SLOTS) {
         protected void onContentsChanged(int slot) {
            JoystickBlockEntity.this.onFrequencySlotChanged(slot);
         }
      };
      this.transmitters = new Transmitter[TRANSMITTER_COUNT];
      this.lastTransmittedStrength = new int[TRANSMITTER_COUNT];

      for(JoystickDirection d : JoystickDirection.VALUES) {
         this.transmitters[d.index] = new DirectionalTransmitter(d);
      }

      this.transmitters[BUTTON_TRANSMITTER_INDEX] = new ButtonTransmitter();
   }

   public void addBehaviours(List<BlockEntityBehaviour> list) {
   }

   public void initialize() {
      super.initialize();
      this.registerAllTransmitters();
   }

   public void tick() {
      super.tick();
      if (this.level != null) {
         if (this.level.isClientSide) {
            this.lerpedTiltX.chase((double)this.tiltX, (double)0.35F, Chaser.EXP);
            this.lerpedTiltY.chase((double)this.tiltY, (double)0.35F, Chaser.EXP);
            this.lerpedButtonPress.chase(this.buttonPressed ? (double)1.0F : (double)0.0F, (double)0.35F, Chaser.EXP);
            this.lerpedTiltX.tickChaser();
            this.lerpedTiltY.tickChaser();
            this.lerpedButtonPress.tickChaser();
            JoystickClientRaycast.tickGrip(this);
         } else {
            if (this.currentUser != null) {
               Player player = this.level.getPlayerByUUID(this.currentUser);
               if (player == null || !playerInRange(player, this.level, this.getBlockPos())) {
                  this.disconnectUser();
               }
            }

            boolean desiredPowered = this.level.getBestNeighborSignal(this.worldPosition) > 0;
            boolean desiredInUse = this.currentUser != null;
            this.powered = desiredPowered;
            if (this.currentUser == null && !desiredPowered && (this.tiltX != 0 || this.tiltY != 0)) {
               this.tiltX = 0;
               this.tiltY = 0;
               this.setChanged();
               this.sendData();
            }

            BlockState state = this.getBlockState();
            BlockState target = state;
            if (state.hasProperty(JoystickBlock.POWERED) && (Boolean)state.getValue(JoystickBlock.POWERED) != desiredPowered) {
               target = (BlockState)state.setValue(JoystickBlock.POWERED, desiredPowered);
            }

            if (state.hasProperty(JoystickBlock.IN_USE) && (Boolean)state.getValue(JoystickBlock.IN_USE) != desiredInUse) {
               target = (BlockState)target.setValue(JoystickBlock.IN_USE, desiredInUse);
            }

            if (target != state) {
               this.level.setBlockAndUpdate(this.worldPosition, target);
            }

            this.pushNetworkIfChanged();
         }
      }
   }

   public static boolean playerInRange(Player player, Level world, BlockPos pos) {
      double range = player.blockInteractionRange();
      return Sable.HELPER.distanceSquaredWithSubLevels(world, player.getEyePosition(), (double)pos.getX() + (double)0.5F, (double)pos.getY() + (double)0.5F, (double)pos.getZ() + (double)0.5F) < range * range;
   }

   public boolean checkUser(UUID user) {
      return user.equals(this.currentUser);
   }

   public boolean hasController() {
      return this.currentUser != null;
   }

   public @Nullable UUID getCurrentUser() {
      return this.currentUser;
   }

   public byte getTiltX() {
      return this.tiltX;
   }

   public byte getTiltY() {
      return this.tiltY;
   }

   public boolean isButtonPressed() {
      return this.buttonPressed;
   }

   public boolean isShowHud() {
      return this.showHud;
   }

   public void setShowHud(boolean show) {
      if (this.showHud != show) {
         this.showHud = show;
         if (this.level != null && !this.level.isClientSide) {
            this.setChanged();
            this.sendData();
         }

      }
   }

   public boolean isUseMouseInput() {
      return this.useMouseInput;
   }

   public void setUseMouseInput(boolean use) {
      if (this.useMouseInput != use) {
         this.useMouseInput = use;
         if (this.level != null && !this.level.isClientSide) {
            this.setChanged();
            this.sendData();
         }

      }
   }

   public boolean isSpringBack() {
      return this.springBack;
   }

   public void setSpringBack(boolean springBack) {
      if (this.springBack != springBack) {
         this.springBack = springBack;
         if (this.level != null && !this.level.isClientSide) {
            this.setChanged();
            this.sendData();
         }

      }
   }

   public String getBinding(int i) {
      return i >= 0 && i < BIND_COUNT ? this.bindings[i] : "";
   }

   public void setBinding(int i, String keyName) {
      if (i >= 0 && i < BIND_COUNT) {
         String next = keyName == null ? "" : keyName;
         if (!next.equals(this.bindings[i])) {
            this.bindings[i] = next;
            if (this.level != null && !this.level.isClientSide) {
               this.setChanged();
               this.sendData();
            }

         }
      }
   }

   private static String[] createDefaultBindings() {
      String[] arr = new String[BIND_COUNT];
      arr[JoystickDirection.FORWARD.index] = "key.keyboard.w";
      arr[JoystickDirection.RIGHT.index] = "key.keyboard.d";
      arr[JoystickDirection.BACK.index] = "key.keyboard.s";
      arr[JoystickDirection.LEFT.index] = "key.keyboard.a";
      arr[BIND_BUTTON_INDEX] = "key.mouse.left";
      return arr;
   }

   public void clearFrequencies() {
      if (this.level != null && !this.level.isClientSide) {
         for(int i = 0; i < FREQUENCY_SLOTS; ++i) {
            if (!this.frequencyItems.getStackInSlot(i).isEmpty()) {
               this.frequencyItems.setStackInSlot(i, ItemStack.EMPTY);
            }
         }

      }
   }

   public void setTiltFromController(byte tiltX, byte tiltY) {
      if (this.level != null && !this.level.isClientSide) {
         byte clampedX = clampStep(tiltX);
         byte clampedY = clampStep(tiltY);
         if (clampedX != this.tiltX || clampedY != this.tiltY) {
            this.tiltX = clampedX;
            this.tiltY = clampedY;
            this.setChanged();
            this.sendData();
         }
      }
   }

   public void setButtonPressedFromController(boolean pressed) {
      if (this.level != null && !this.level.isClientSide) {
         if (this.buttonPressed != pressed) {
            this.buttonPressed = pressed;
            this.setChanged();
            this.sendData();
         }
      }
   }

   private static byte clampStep(byte v) {
      return (byte)Math.clamp((long)v, -15, 15);
   }

   static JoystickBlockEntity findActiveSession(UUID uuid) {
      return (JoystickBlockEntity)SERVER_ACTIVE_SESSIONS.get(uuid);
   }

   static void clearSessionsForLevel(Level level) {
      SERVER_ACTIVE_SESSIONS.values().removeIf((be) -> be.level == level || be.isRemoved());
   }

   public boolean checkAndStartUsing(UUID userID) {
      if (this.currentUser != null) {
         return false;
      } else {
         this.currentUser = userID;
         this.powered = true;
         this.buttonPressed = false;
         if (this.level == null) {
            return true;
         } else {
            if (this.level.isClientSide) {
               if (userID.equals(clientPlayerUuid())) {
                  JoystickControlClient.enter(this.worldPosition);
               }
            } else {
               SERVER_ACTIVE_SESSIONS.put(userID, this);
               this.setChanged();
               this.sendData();
            }

            return true;
         }
      }
   }

   public void disconnectUser() {
      UUID previous = this.currentUser;
      this.currentUser = null;
      if (this.level != null) {
         if (this.level.isClientSide) {
            if (previous != null && previous.equals(clientPlayerUuid())) {
               JoystickControlClient.exit("");
            }
         } else {
            if (previous != null) {
               SERVER_ACTIVE_SESSIONS.remove(previous, this);
            }

            this.buttonPressed = false;
            this.setChanged();
            this.sendData();
         }

      }
   }

   private static @Nullable UUID clientPlayerUuid() {
      Minecraft mc = Minecraft.getInstance();
      return mc.player == null ? null : mc.player.getUUID();
   }

   public void write(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
      super.write(tag, registries, clientPacket);
      tag.put("Frequencies", this.frequencyItems.serializeNBT(registries));
      tag.putBoolean("ShowHud", this.showHud);
      tag.putBoolean("UseMouseInput", this.useMouseInput);
      tag.putBoolean("SpringBack", this.springBack);
      ListTag bindList = new ListTag();

      for(String bind : this.bindings) {
         bindList.add(StringTag.valueOf(bind));
      }

      tag.put("Bindings", bindList);
      tag.putByte("TiltX", this.tiltX);
      tag.putByte("TiltY", this.tiltY);
      tag.putBoolean("ButtonPressed", this.buttonPressed);
      if (clientPacket) {
         if (this.currentUser != null) {
            tag.putUUID("CurrentUser", this.currentUser);
         }

         tag.putBoolean("Powered", this.powered);
      }

   }

   protected void read(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
      super.read(tag, registries, clientPacket);
      if (tag.contains("Frequencies")) {
         CompoundTag freqTag = tag.getCompound("Frequencies").copy();
         freqTag.putInt("Size", FREQUENCY_SLOTS);
         this.frequencyItems.deserializeNBT(registries, freqTag);
      }

      this.showHud = !tag.contains("ShowHud") || tag.getBoolean("ShowHud");
      this.useMouseInput = !tag.contains("UseMouseInput") || tag.getBoolean("UseMouseInput");
      this.springBack = tag.contains("SpringBack") && tag.getBoolean("SpringBack");
      if (tag.contains("Bindings")) {
         ListTag bindList = tag.getList("Bindings", 8);

         for(int i = 0; i < BIND_COUNT; ++i) {
            this.bindings[i] = i < bindList.size() ? bindList.getString(i) : (i == BIND_BUTTON_INDEX ? "key.mouse.left" : "");
         }
      }

      this.tiltX = tag.getByte("TiltX");
      this.tiltY = tag.getByte("TiltY");
      this.buttonPressed = tag.getBoolean("ButtonPressed");
      if (clientPacket) {
         UUID previous = this.currentUser;
         this.currentUser = tag.hasUUID("CurrentUser") ? tag.getUUID("CurrentUser") : null;
         this.powered = tag.getBoolean("Powered");
         if (this.level != null && this.level.isClientSide) {
            UUID me = clientPlayerUuid();
            if (me != null) {
               if (!me.equals(this.currentUser) || previous != null && previous.equals(me)) {
                  if (this.currentUser == null && me.equals(previous)) {
                     JoystickControlClient.exit("");
                  }
               } else {
                  JoystickControlClient.enter(this.worldPosition);
               }
            }
         }
      }

   }

   private void onFrequencySlotChanged(int slot) {
      if (this.level != null && !this.level.isClientSide) {
         this.setChanged();
         this.sendData();
         int idx = slot / 2;
         if (idx >= 0 && idx < this.transmitters.length) {
            this.transmitters[idx].rebuildFrequency();
         }

      }
   }

   public void frequenciesChanged() {
      if (this.level != null && !this.level.isClientSide) {
         this.setChanged();
         this.sendData();
      }
   }

   private void registerAllTransmitters() {
      if (this.level != null && !this.level.isClientSide && !this.networkRegistered) {
         for(Transmitter t : this.transmitters) {
            t.rebuildFrequency();
            Create.REDSTONE_LINK_NETWORK_HANDLER.addToNetwork(this.level, t);
         }

         this.networkRegistered = true;
      }
   }

   private void unregisterAllTransmitters() {
      if (this.level != null && !this.level.isClientSide && this.networkRegistered) {
         for(Transmitter t : this.transmitters) {
            Create.REDSTONE_LINK_NETWORK_HANDLER.removeFromNetwork(this.level, t);
         }

         this.networkRegistered = false;
      }
   }

   private void pushNetworkIfChanged() {
      if (this.level != null && !this.level.isClientSide && this.networkRegistered) {
         for(int i = 0; i < this.transmitters.length; ++i) {
            int current = this.transmitters[i].getTransmittedStrength();
            if (current != this.lastTransmittedStrength[i]) {
               this.lastTransmittedStrength[i] = current;
               Create.REDSTONE_LINK_NETWORK_HANDLER.updateNetworkOf(this.level, this.transmitters[i]);
            }
         }

      }
   }

   public void invalidate() {
      this.unregisterAllTransmitters();
      if (this.level != null && this.level.isClientSide) {
         JoystickControlClient.exitIfControlling(this.worldPosition);
      } else if (this.currentUser != null) {
         SERVER_ACTIVE_SESSIONS.remove(this.currentUser, this);
      }

      super.invalidate();
   }

   public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
      return new JoystickMenu((MenuType) KineticMenuTypes.JOYSTICK.get(), id, inv, this);
   }

   public Component getDisplayName() {
      return Component.translatable("block.createkinetic.joystick");
   }

   static {
      BIND_COUNT = JoystickDirection.VALUES.length + 1;
      BIND_BUTTON_INDEX = JoystickDirection.VALUES.length;
      BUTTON_SLOT_1 = JoystickDirection.VALUES.length * 2;
      BUTTON_SLOT_2 = BUTTON_SLOT_1 + 1;
      FREQUENCY_SLOTS = BUTTON_SLOT_2 + 1;
      TRANSMITTER_COUNT = JoystickDirection.VALUES.length + 1;
      BUTTON_TRANSMITTER_INDEX = JoystickDirection.VALUES.length;
      SERVER_ACTIVE_SESSIONS = new ConcurrentHashMap();
   }

   private abstract class Transmitter implements IRedstoneLinkable {
      final int firstSlot;
      final int secondSlot;
      private Couple<Frequency> key;

      Transmitter(final int firstSlot, final int secondSlot) {
         this.key = Couple.create(Frequency.EMPTY, Frequency.EMPTY);
         this.firstSlot = firstSlot;
         this.secondSlot = secondSlot;
      }

      void rebuildFrequency() {
         ItemStack a = JoystickBlockEntity.this.frequencyItems.getStackInSlot(this.firstSlot);
         ItemStack b = JoystickBlockEntity.this.frequencyItems.getStackInSlot(this.secondSlot);
         Couple<Frequency> newKey = Couple.create(Frequency.of(a), Frequency.of(b));
         if (!newKey.equals(this.key)) {
            if (JoystickBlockEntity.this.networkRegistered) {
               Create.REDSTONE_LINK_NETWORK_HANDLER.removeFromNetwork(JoystickBlockEntity.this.level, this);
               this.key = newKey;
               Create.REDSTONE_LINK_NETWORK_HANDLER.addToNetwork(JoystickBlockEntity.this.level, this);
            } else {
               this.key = newKey;
            }

         }
      }

      public void setReceivedStrength(int power) {
      }

      public boolean isListening() {
         return false;
      }

      public boolean isAlive() {
         return !JoystickBlockEntity.this.isRemoved();
      }

      public Couple<Frequency> getNetworkKey() {
         return this.key;
      }

      public BlockPos getLocation() {
         return JoystickBlockEntity.this.worldPosition;
      }
   }

   private final class DirectionalTransmitter extends Transmitter {
      private final JoystickDirection direction;

      DirectionalTransmitter(final JoystickDirection direction) {
         super(direction.firstSlot(), direction.secondSlot());
         this.direction = direction;
      }

      public int getTransmittedStrength() {
         return this.direction.strengthFor(JoystickBlockEntity.this.tiltX, JoystickBlockEntity.this.tiltY);
      }
   }

   private final class ButtonTransmitter extends Transmitter {
      ButtonTransmitter() {
         super(JoystickBlockEntity.BUTTON_SLOT_1, JoystickBlockEntity.BUTTON_SLOT_2);
      }

      public int getTransmittedStrength() {
         return JoystickBlockEntity.this.buttonPressed ? 15 : 0;
      }
   }
}
