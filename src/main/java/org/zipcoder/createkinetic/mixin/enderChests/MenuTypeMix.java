package org.zipcoder.createkinetic.mixin.enderChests;

import org.zipcoder.createkinetic.enderChests.ChestMenus;
import org.zipcoder.createkinetic.enderChests.ChestType;
import org.zipcoder.createkinetic.enderChests.CustomChestMenu;
import net.minecraft.world.flag.FeatureElement;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static org.zipcoder.createkinetic.Createkinetic.MODID;


@Mixin(value = MenuType.class)
public abstract class MenuTypeMix<T extends AbstractContainerMenu> implements FeatureElement {

    @Shadow
    private static <T extends AbstractContainerMenu> MenuType<T> register(String p_39989_, MenuType.MenuSupplier<T> p_39990_) {
        return null;
    }

    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void injectedTail(CallbackInfo ci) {
        ChestMenus.IRON = register(MODID + ":iron_chest", (id, inventory) -> new CustomChestMenu(ChestType.IRON, id, inventory, () -> ChestMenus.IRON));
        ChestMenus.COPPER = register(MODID + ":copper_chest", (id, inventory) -> new CustomChestMenu(ChestType.COPPER, id, inventory, () -> ChestMenus.COPPER));
        ChestMenus.GOLD = register(MODID + ":gold_chest", (id, inventory) -> new CustomChestMenu(ChestType.GOLD, id, inventory, () -> ChestMenus.GOLD));
        ChestMenus.LAPIS = register(MODID + ":lapis_chest", (id, inventory) -> new CustomChestMenu(ChestType.LAPIS, id, inventory, () -> ChestMenus.LAPIS));
        ChestMenus.REDSTONE = register(MODID + ":redstone_chest", (id, inventory) -> new CustomChestMenu(ChestType.REDSTONE, id, inventory, () -> ChestMenus.REDSTONE));
        ChestMenus.DIAMOND = register(MODID + ":diamond_chest", (id, inventory) -> new CustomChestMenu(ChestType.DIAMOND, id, inventory, () -> ChestMenus.DIAMOND));
        ChestMenus.EMERALD = register(MODID + ":emerald_chest", (id, inventory) -> new CustomChestMenu(ChestType.EMERALD, id, inventory, () -> ChestMenus.EMERALD));
        ChestMenus.NETHERITE = register(MODID + ":netherite_chest", (id, inventory) -> new CustomChestMenu(ChestType.NETHERITE, id, inventory, () -> ChestMenus.NETHERITE));
    }
}