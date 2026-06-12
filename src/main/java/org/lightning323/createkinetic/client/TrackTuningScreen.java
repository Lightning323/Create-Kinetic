/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.components.Button
 *  net.minecraft.client.gui.components.events.GuiEventListener
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.network.chat.Component
 */
package org.lightning323.createkinetic.client;

import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.lightning323.createkinetic.CreateKinetic;

public class TrackTuningScreen
extends Screen {
    private int profileIndex = 0;
    private int elementIndex = 0;
    private int axisIndex = 0;

    public TrackTuningScreen() {
        super((Component)Component.literal((String)"Tracks tuning"));
    }

    protected void init() {
        this.rebuild();
    }

    private void rebuild() {
        this.clearWidgets();
        int x = 8;
        int y = this.height - 172;
        this.addRenderableWidget(Button.builder(Component.literal("Profile: " + this.profile().name), button -> {
            this.profileIndex = (this.profileIndex + 1) % TrackRenderTuning.PROFILES.length;
            this.elementIndex = Math.min(this.elementIndex, this.profile().elements.length - 1);
            this.rebuild();
        }).bounds(8, y, 200, 20).build());
        this.addRenderableWidget(Button.builder(Component.literal("Element: " + this.element().name), button -> {
            this.elementIndex = (this.elementIndex + 1) % this.profile().elements.length;
            this.rebuild();
        }).bounds(8, y += 24, 200, 20).build());
        this.addRenderableWidget(Button.builder(Component.literal("Axis: " + this.axisName()), button -> {
            this.axisIndex = (this.axisIndex + 1) % 8;
            this.rebuild();
        }).bounds(8, y += 24, 200, 20).build());
        this.addRenderableWidget(Button.builder(Component.literal("-0.125"), button -> this.adjust(-0.125f)).bounds(8, y += 24, 96, 20).build());
        this.addRenderableWidget(Button.builder(Component.literal("+0.125"), button -> this.adjust(0.125f)).bounds(112, y, 96, 20).build());
        this.addRenderableWidget(Button.builder(Component.literal("-0.025"), button -> this.adjust(-0.025f)).bounds(8, y += 24, 96, 20).build());
        this.addRenderableWidget(Button.builder(Component.literal("+0.025"), button -> this.adjust(0.025f)).bounds(112, y, 96, 20).build());
        this.addRenderableWidget(Button.builder(Component.literal("Copy + save tuning"), button -> {
            String dump = TrackRenderTuning.dump();
            Minecraft.getInstance().keyboardHandler.setClipboard(dump);
            TrackRenderTuning.save(Minecraft.getInstance().gameDirectory.toPath().resolve("config/"+ CreateKinetic.MOD_ID+"-render-tuning.txt"));
            if (Minecraft.getInstance().player != null) {
                Minecraft.getInstance().player.displayClientMessage(Component.literal("Tracks tuning copied to clipboard and saved."), false);
            }
            LogUtils.getLogger().info(dump);
        }).bounds(8, y += 24, 200, 20).build());
        this.addRenderableWidget(Button.builder(Component.literal("Close"), button -> this.onClose()).bounds(8, y += 24, 200, 20).build());
    }

    private TrackRenderTuning.Profile profile() {
        return TrackRenderTuning.PROFILES[this.profileIndex];
    }

    private TrackRenderTuning.Element element() {
        return this.profile().elements[this.elementIndex];
    }

    private String axisName() {
        return switch (this.axisIndex) {
            case 0 -> "X";
            case 1 -> "Y";
            case 2 -> "Z";
            case 3 -> "Scale X";
            case 4 -> "Scale Y";
            case 5 -> "Scale Z";
            case 6 -> "Slope";
            default -> "Base Slope";
        };
    }

    private void adjust(float amount) {
        if (this.axisIndex == 7) {
            TrackRenderTuning.BASE_SLOPE_DEGREES += amount * 10.0f;
        } else {
            this.element().add(this.axisIndex, amount);
        }
        TrackRenderTuning.save(Minecraft.getInstance().gameDirectory.toPath().resolve("config/"+CreateKinetic.MOD_ID+"-render-tuning.txt"));
        this.rebuild();
    }

    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
        TrackRenderTuning.Element element = this.element();
        graphics.drawString(this.font, this.title, 10, this.height - 196, 0xFFFFFF);
        graphics.drawString(this.font, (Component)Component.literal((String)("Profile: " + this.profile().name + " / " + element.name)), 10, this.height - 186, 0xDDDDDD);
        graphics.drawString(this.font, (Component)Component.literal((String)("Pos: X " + element.x + " Y " + element.y + " Z " + element.z)), 10, this.height - 176, 0xCCCCCC);
        graphics.drawString(this.font, (Component)Component.literal((String)("Scale: X " + element.scaleX + " Y " + element.scaleY + " Z " + element.scaleZ)), 10, this.height - 166, 0xCCCCCC);
        graphics.drawString(this.font, (Component)Component.literal((String)("Slope: " + element.slopeDegrees + " Base: " + TrackRenderTuning.BASE_SLOPE_DEGREES)), 10, this.height - 156, 0xCCCCCC);
    }

    public void renderBackground(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
    }

    public void renderTransparentBackground(GuiGraphics graphics) {
    }

    public boolean isPauseScreen() {
        return false;
    }
}

