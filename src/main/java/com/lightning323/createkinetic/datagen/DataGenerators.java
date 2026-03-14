package com.lightning323.createkinetic.datagen;

import com.lightning323.createkinetic.CreateKinetic;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = CreateKinetic.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class DataGenerators {

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator gen = event.getGenerator();
        PackOutput output = gen.getPackOutput();

        // Register your Recipe Provider here
        if (event.includeServer()) {
            gen.addProvider(true, new KineticAssemblyRecipes(gen));
        }
    }
}