package org.lightning323.createkinetic.mixin.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import org.lightning323.createkinetic.content.blocks.joystick.JoystickControlClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

@Mixin(Minecraft.class)
public class MinecraftMixin {

    @Inject(method = "setScreen", at = @At("TAIL"))
    private void onSetScreen(@Nullable Screen screen, CallbackInfo ci) {
        if (screen != null) {
            JoystickControlClient.requestExit();
        }
    }
}