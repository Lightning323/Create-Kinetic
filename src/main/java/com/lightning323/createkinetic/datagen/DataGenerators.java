package com.lightning323.createkinetic.datagen;

import com.lightning323.createkinetic.CreateKinetic;
import com.lightning323.createkinetic.KineticPonderPlugin;
import com.simibubi.create.AllKeys;
import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.Create;
import com.simibubi.create.foundation.advancement.AllAdvancements;
import com.simibubi.create.foundation.ponder.CreatePonderPlugin;
import com.simibubi.create.infrastructure.data.CreateRegistrateTags;
import com.simibubi.create.infrastructure.data.TagLangGenerator;
import com.tterrag.registrate.providers.ProviderType;
import net.createmod.ponder.foundation.PonderIndex;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.valkyrienskies.core.api.ships.Ship;

import java.util.function.BiConsumer;

@Mod.EventBusSubscriber(modid = CreateKinetic.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class DataGenerators {

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator gen = event.getGenerator();
        PackOutput output = gen.getPackOutput();
        addExtraRegistrateData();

        // Register your Recipe Provider here
        if (event.includeServer()) {
            gen.addProvider(true, new KineticRecipes(gen));
        }
    }

    private static void addExtraRegistrateData() {
        CreateKinetic.REGISTRATE.addDataGenerator(ProviderType.LANG, provider -> {
            BiConsumer<String, String> langConsumer = provider::add;
            providePonderLang(langConsumer);
            new TagLangGenerator(langConsumer).generate();
        });
    }

    private static void providePonderLang(BiConsumer<String, String> consumer) {
        // Register this since FMLClientSetupEvent does not run during datagen
        PonderIndex.addPlugin(new KineticPonderPlugin());
        PonderIndex.getLangAccess().provideLang(CreateKinetic.MOD_ID, consumer);
    }
}