package org.zipcoder.createkinetic.enderChests;

import net.minecraft.SharedConstants;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import static org.zipcoder.createkinetic.Createkinetic.MODID;

public class CustomEndChestScreen extends AbstractContainerScreen<CustomChestMenu> implements MenuAccess<CustomChestMenu> {
    private static ResourceLocation CONTAINER_BACKGROUND;
    private final int containerRows;
    private final ChestType chestType;
    final int BUTTON_SIZE = 16;

    public CustomEndChestScreen(CustomChestMenu menu, Inventory p_98410_, Component p_98411_) {
        super(menu, p_98410_, p_98411_);
        this.containerRows = menu.getRowCount();
        this.imageHeight = 256;

        if (menu.getTotalPages() > 1) {
            this.titleLabelX = 8 + BUTTON_SIZE + 2;
//            textWidth = this.font.width(p_98410_.getDisplayName());
//            this.titleLabelX = leftPos + (imageWidth - textWidth) / 2;
        } else {
            this.titleLabelX = 8;
        }

        this.previousPage = -1;
        this.inventoryLabelY = menu.inventoryTextY;
        this.chestType = menu.chestType;
        CONTAINER_BACKGROUND = new ResourceLocation(MODID, "textures/gui/container/" + chestType.name().toLowerCase() + ".png");
    }

    @Override
    protected void init() {
        super.init();

        if (menu.getTotalPages() > 1) {
            // Next button
            this.addRenderableWidget(Button.builder(Component.literal(">"), button -> {
                if (menu.getPage() < menu.getTotalPages() - 1) {
                    menu.setPage(menu.getPage() + 1);
                }
            }).bounds(leftPos + imageWidth - 20, topPos + 5, BUTTON_SIZE, BUTTON_SIZE).build());

            // Prev button
            this.addRenderableWidget(Button.builder(Component.literal("<"), button -> {
                if (menu.getPage() > 0) {
                    menu.setPage(menu.getPage() - 1);
                }
            }).bounds(leftPos + 5, topPos + 5, BUTTON_SIZE, BUTTON_SIZE).build());
        }
    }

    String pageText = "";
    int textWidth = 0;
    int previousPage = -1;

    public void render(GuiGraphics p_282060_, int p_282533_, int p_281661_, float p_281873_) {
        // this.renderBackground(p_282060_);
        if ((SharedConstants.getCurrentVersion()).getProtocolVersion() < 765)
            p_282060_.fillGradient(0, 0, this.width, this.height, -1072689136, -804253680);

        if (menu.getTotalPages() > 1) {
            int page = menu.getPage() + 1;
            pageText = "Page " + (page) + "/" + menu.getTotalPages();
            if (page != previousPage) {//We only want to calculate the width if the page has changed
                previousPage = page;
                textWidth = this.font.width(pageText);
            }

            int centerX = leftPos + (imageWidth - textWidth) / 2;
            p_282060_.drawString(this.font, pageText, centerX, topPos + 18, 0x404040);
        }


        super.render(p_282060_, p_282533_, p_281661_, p_281873_);
        this.renderTooltip(p_282060_, p_282533_, p_281661_);
    }

    protected void renderBg(GuiGraphics p_283694_, float p_282334_, int p_282603_, int p_282158_) {
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        p_283694_.blit(CONTAINER_BACKGROUND, i, j, 0, 0, this.imageWidth, imageHeight);
    }
}