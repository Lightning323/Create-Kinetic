package com.lightning323.createkinetic.registries;

import com.lightning323.createkinetic.items.frequencyFilter.FrequencyFilterMenu;
import com.lightning323.createkinetic.items.frequencyFilter.FrequencyFilterScreen;
import com.simibubi.create.Create;
import com.simibubi.create.content.logistics.filter.PackageFilterMenu;
import com.simibubi.create.content.logistics.filter.PackageFilterScreen;
import com.tterrag.registrate.builders.MenuBuilder;
import com.tterrag.registrate.util.entry.MenuEntry;
import com.tterrag.registrate.util.nullness.NonNullSupplier;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.world.inventory.AbstractContainerMenu;

import static com.lightning323.createkinetic.CreateKinetic.REGISTRATE;

public class KineticMenus {

    public static final MenuEntry<FrequencyFilterMenu> FREQUENCY_FILTER =
            register("package_filter", FrequencyFilterMenu::new, () -> FrequencyFilterScreen::new);

    private static <C extends AbstractContainerMenu, S extends Screen & MenuAccess<C>> MenuEntry<C> register(
            String name, MenuBuilder.ForgeMenuFactory<C> factory, NonNullSupplier<MenuBuilder.ScreenFactory<C, S>> screenFactory) {
        return REGISTRATE
                .menu(name, factory, screenFactory)
                .register();
    }

    public static void register() {
    }

}
