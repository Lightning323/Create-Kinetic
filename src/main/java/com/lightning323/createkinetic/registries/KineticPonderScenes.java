package com.lightning323.createkinetic.registries;

import com.simibubi.create.infrastructure.ponder.AllCreatePonderTags;
import com.simibubi.create.infrastructure.ponder.scenes.PulleyScenes;
import com.tterrag.registrate.util.entry.ItemProviderEntry;
import com.tterrag.registrate.util.entry.RegistryEntry;
import net.createmod.ponder.api.registration.PonderSceneRegistrationHelper;
import net.minecraft.resources.ResourceLocation;

public class KineticPonderScenes {

	public static void register(PonderSceneRegistrationHelper<ResourceLocation> helper) {
		PonderSceneRegistrationHelper<ItemProviderEntry<?>> HELPER = helper.withKeyFunction(RegistryEntry::getId);

		HELPER.forComponents(KineticBlocks.SAIL_PULLEY)
				.addStoryBoard("rope_pulley/anchor", PulleyScenes::movement, AllCreatePonderTags.KINETIC_APPLIANCES,
						AllCreatePonderTags.MOVEMENT_ANCHOR)
				.addStoryBoard("rope_pulley/modes", PulleyScenes::movementModes)
				.addStoryBoard("rope_pulley/multi_rope", PulleyScenes::multiRope)
				.addStoryBoard("rope_pulley/attachment", PulleyScenes::attachment);

	}
}