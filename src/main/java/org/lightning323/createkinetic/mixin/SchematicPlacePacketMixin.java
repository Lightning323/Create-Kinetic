package org.lightning323.createkinetic.mixin;

import com.simibubi.create.content.schematics.packet.SchematicPlacePacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import org.lightning323.createkinetic.content.creative_tools.interaction.GluedContraptionMoverService;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SchematicPlacePacket.class)
public abstract class SchematicPlacePacketMixin {
    @Shadow
    public abstract ItemStack stack();

    @Inject(method = "handle", at = @At("TAIL"))
    private void assemblystick$finalizeMove(final ServerPlayer player, final CallbackInfo ci) {
        if (player == null) {
            return;
        }
        GluedContraptionMoverService.finalizePlacedMove(player, stack());
    }
}
