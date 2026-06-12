package org.lightning323.createkinetic;

import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.item.ItemDescription;
import com.simibubi.create.foundation.item.KineticStats;
import com.simibubi.create.foundation.item.TooltipModifier;
import com.tterrag.registrate.builders.ItemBuilder;
import com.tterrag.registrate.util.nullness.NonNullFunction;
import com.tterrag.registrate.util.nullness.NonNullSupplier;
import dev.simulated_team.simulated.registrate.SimulatedRegistrate;
import net.createmod.catnip.lang.FontHelper;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;

import java.util.HashMap;
import java.util.function.Supplier;

public final class KineticRegistrate extends CreateRegistrate {
   private KineticRegistrate(String modid) {
      super(modid);
   }

   public static KineticRegistrate create(String modid) {
      return new KineticRegistrate(modid);
   }


   /**
    * Simulated registrate is how we make a custom creative tab,
    * <p>
    * assets/modid/simulated/sections/tracks.json:
    * (Note that this file must be REMOVED if you dont want a custom tab)
    * {
    * "priority": 2100,
    * "title": {
    * "text": { "translate": "tracks.simulated_section.tracks" },
    * "background": "#aa2f2d26",
    * "color": "#ffe8decf",
    * "secondary_color": "#ffc7b9a2"
    * },
    * "sprite": "tracks:tracks",
    * "only_animate_on_hover": true
    * }
    * <p>
    * <p>
    * the tab items are registered like so:
    * private static final SimulatedRegistrate REGISTRATE = CreateKinetic.getRegistrate();
    * public static final ItemEntry<Item> SMALL_SUSPENSION_TRACK = REGISTRATE.item("small_suspension_track", Item::new).register();
    * public static final ItemEntry<Item> SUSPENSION_TRACK = ((ItemBuilder)REGISTRATE.item("suspension_track", Item::new).transform((NonNullFunction)CreativeTabItemTransforms.VisibilityType.INVISIBLE.applyItem())).register();
    * public static final ItemEntry<Item> LARGE_SUSPENSION_TRACK = ((ItemBuilder)REGISTRATE.item("large_suspension_track", Item::new).transform((NonNullFunction)CreativeTabItemTransforms.VisibilityType.INVISIBLE.applyItem())).register();
    *
    * @param modid
    * @return
    */
   public static NonNullSupplier<SimulatedRegistrate> getSimulatedRegistrate(String modid) {
      return NonNullSupplier.lazy(() -> (SimulatedRegistrate) new SimulatedRegistrate(CreateKinetic.path(modid), modid)
              .defaultCreativeTab((ResourceKey) null));
   }

   public static NonNullSupplier<KineticRegistrate> getKineticRegistrate(String modid) {
      return NonNullSupplier.lazy(() -> (KineticRegistrate) ((CreateRegistrate) KineticRegistrate.create(modid)
              .defaultCreativeTab((ResourceKey) null)).setTooltipModifierFactory((item) ->
              (new ItemDescription.Modifier(item, FontHelper.Palette.STANDARD_CREATE))
                      .andThen(TooltipModifier.mapNull(KineticStats.create(item)))));
   }
}