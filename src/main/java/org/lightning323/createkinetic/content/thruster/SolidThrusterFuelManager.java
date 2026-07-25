package org.lightning323.createkinetic.content.thruster;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.tags.TagKey;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.lightning323.createkinetic.CreateKinetic;
import org.slf4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

public class SolidThrusterFuelManager extends SimpleJsonResourceReloadListener {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    public static final String DIRECTORY = "solid_thruster_fuels";

    private static Map<Item, ItemThrusterProperties> fuelPropertiesMap = new HashMap<>();
    private static List<TagFuelEntry> tagFuelEntries = new ArrayList<>();
    private static Map<Item, ItemThrusterProperties> scriptedFuelPropertiesMap = new HashMap<>();
    private static Map<ResourceLocation, ItemThrusterProperties> scriptedFuelPropertiesById = new HashMap<>();
    private static Set<ResourceLocation> removedFuelIds = new HashSet<>();
    private static Map<ResourceLocation, JsonElement> cachedDatapack = null;

    private record TagFuelEntry(TagKey<Item> tag, ItemThrusterProperties properties) {}

    public SolidThrusterFuelManager() {
        super(GSON, DIRECTORY);
    }

    public static Map<Item, ItemThrusterProperties> getFuelPropertiesMap() {
        Map<Item, ItemThrusterProperties> merged = new HashMap<>(fuelPropertiesMap);
        expandTagsInto(merged, tagFuelEntries);
        merged.putAll(scriptedFuelPropertiesMap);
        for (ResourceLocation removed : removedFuelIds) {
            Item item = BuiltInRegistries.ITEM.get(removed);
            if (item != null && item != Items.AIR) {
                merged.remove(item);
            }
        }
        return merged;
    }

    public static Set<ResourceLocation> getRemovedFuelIds() {
        return Set.copyOf(removedFuelIds);
    }

    @Nullable
    public static ItemThrusterProperties getProperties(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return null;
        }
        return getProperties(stack.getItem());
    }

    @Nullable
    public static ItemThrusterProperties getProperties(Item item) {
        if (item == null || item == Items.AIR) {
            return null;
        }
        ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(item);
        if (itemId != null && removedFuelIds.contains(itemId)) {
            return null;
        }
        ItemThrusterProperties scripted = scriptedFuelPropertiesMap.get(item);
        if (scripted != null) {
            return scripted;
        }
        if (itemId != null) {
            ItemThrusterProperties byId = scriptedFuelPropertiesById.get(itemId);
            if (byId != null) {
                return byId;
            }
        }
        ItemThrusterProperties direct = fuelPropertiesMap.get(item);
        if (direct != null) {
            return direct;
        }
        for (TagFuelEntry entry : tagFuelEntries) {
            if (stackMatchesTag(item, entry.tag())) {
                return entry.properties();
            }
        }
        return null;
    }

    private static boolean stackMatchesTag(Item item, TagKey<Item> tag) {
        return BuiltInRegistries.ITEM.getTag(tag)
            .map(holderSet -> holderSet.contains(BuiltInRegistries.ITEM.wrapAsHolder(item)))
            .orElse(false);
    }

    private static void expandTagsInto(Map<Item, ItemThrusterProperties> target, List<TagFuelEntry> tags) {
        for (TagFuelEntry entry : tags) {
            BuiltInRegistries.ITEM.getTag(entry.tag()).ifPresent(set -> {
                for (Holder<Item> holder : set) {
                    Item item = holder.value();
                    if (item != Items.AIR && !target.containsKey(item)) {
                        target.put(item, entry.properties());
                    }
                }
            });
        }
    }

    public static float getEfficiency(Item item) {
        if (item == null || item == Items.AIR) {
            return 1.0f;
        }
        ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(item);
        if (itemId == null) {
            return 1.0f;
        }
        return 1.0f;
    }

    @Override
    protected void apply(@Nonnull Map<ResourceLocation, JsonElement> object, @Nonnull ResourceManager resourceManager, @Nonnull ProfilerFiller profiler) {
        profiler.push(CreateKinetic.ID + ":loading_solid_thruster_fuels");
        cachedDatapack = new HashMap<>(object);
        ParseResult result = parseFuelProperties(cachedDatapack);
        fuelPropertiesMap = result.itemMap();
        tagFuelEntries = result.tagEntries();
        logReloadSummary("datapack_reload", result);
        profiler.pop();
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
    }

    public static void updateClient(Map<ResourceLocation, ItemThrusterProperties> fuelMap, Set<ResourceLocation> removed) {
        Map<Item, ItemThrusterProperties> newMap = new HashMap<>();
        fuelMap.forEach((rl, props) -> {
            Item item = BuiltInRegistries.ITEM.get(rl);
            if (item != null && item != Items.AIR) {
                newMap.put(item, props);
            }
        });
        fuelPropertiesMap = newMap;
        tagFuelEntries = List.of();
        removedFuelIds = new HashSet<>(removed);
    }

    public static void clearScriptedFuels() {
        scriptedFuelPropertiesMap.clear();
        scriptedFuelPropertiesById.clear();
        removedFuelIds.clear();
        syncToClients();
    }

    public static boolean registerScriptedFuel(String itemId, Map<String, Object> settings) {
        if (settings == null) {
            LOGGER.warn("[{}] KubeJS solid fuel registration failed: settings null for '{}'.", CreateKinetic.ID, itemId);
            return false;
        }
        ResourceLocation itemLocation = ResourceLocation.tryParse(itemId);
        if (itemLocation == null) {
            LOGGER.warn("[{}] KubeJS solid fuel registration failed: invalid item id '{}'.", CreateKinetic.ID, itemId);
            return false;
        }
        return registerScriptedFuelInternal(
            itemLocation,
            getFloatSetting(settings, "thrustMultiplier", "thrust_multiplier", 1.0f),
            getFloatSetting(settings, "consumptionMultiplier", "consumption_multiplier", 1.0f),
            getStringSetting(settings, "particle", "particle", "plume"),
            getStringListSetting(settings, "overrideTextures", "override_textures"),
            getColorSetting(settings, "overrideColor", "override_color"),
            getBooleanSetting(settings, "useItemColor", "use_item_color", false)
        );
    }

    public static boolean overrideFuel(String itemId, Map<String, Object> settings) {
        return registerScriptedFuel(itemId, settings);
    }

    public static boolean removeFuel(String fuelId) {
        ResourceLocation itemLocation = ResourceLocation.tryParse(fuelId);
        if (itemLocation == null) {
            LOGGER.warn("[{}] KubeJS solid fuel removal failed: invalid item id '{}'.", CreateKinetic.ID, fuelId);
            return false;
        }
        removedFuelIds.add(itemLocation);
        Item removedItem = BuiltInRegistries.ITEM.get(itemLocation);
        if (removedItem != null && removedItem != Items.AIR) {
            scriptedFuelPropertiesMap.remove(removedItem);
        }
        scriptedFuelPropertiesById.remove(itemLocation);
        syncToClients();
        return true;
    }

    public static void rebuildAfterCommonConfigReload() {
        if (cachedDatapack == null) {
            return;
        }
        ParseResult result = parseFuelProperties(cachedDatapack);
        fuelPropertiesMap = result.itemMap();
        tagFuelEntries = result.tagEntries();
        logReloadSummary("common_config_reload", result);
        syncToClients();
    }

    private static ParseResult parseFuelProperties(Map<ResourceLocation, JsonElement> object) {
        Map<Item, ItemThrusterProperties> itemMap = new HashMap<>();
        List<TagFuelEntry> tags = new ArrayList<>();
        int parsed = 0;
        int skippedMod = 0;
        int skippedInvalid = 0;

        for (Map.Entry<ResourceLocation, JsonElement> entry : object.entrySet()) {
            ResourceLocation file = entry.getKey();
            SolidThrusterFuelDefinition.CODEC.parse(JsonOps.INSTANCE, entry.getValue())
                .resultOrPartial(err -> LOGGER.error("[{}] Failed to parse solid thruster fuel {}: {}", CreateKinetic.ID, file, err))
                .ifPresent(definition -> {
                    if (definition.requiredMod().isPresent() && !ModList.get().isLoaded(definition.requiredMod().get())) {
                        return;
                    }
                    if (definition.itemId().isPresent() == definition.itemTagId().isPresent()) {
                        LOGGER.error("[{}] Solid thruster fuel {} must define exactly one of 'item' or 'item_tag'.", CreateKinetic.ID, file);
                        return;
                    }
                    ItemThrusterProperties properties = toProperties(definition);
                    if (definition.isItemEntry()) {
                        Item item = definition.getItem();
                        if (item == Items.AIR) {
                            return;
                        }
                        itemMap.put(item, properties);
                    } else {
                        tags.add(new TagFuelEntry(definition.getItemTag(), properties));
                    }
                });
        }

        return new ParseResult(itemMap, tags, parsed, skippedMod, skippedInvalid);
    }

    private static ItemThrusterProperties toProperties(SolidThrusterFuelDefinition definition) {
        return new ItemThrusterProperties(
            definition.thrustMultiplier(),
            definition.consumptionMultiplier(),
            definition.particle(),
            definition.overrideTextures(),
            definition.overrideColor().map(SolidThrusterFuelManager::sanitizeColor).orElse(null),
            definition.useItemColor()
        );
    }

    @Nullable
    public static SolidThrusterFuelDefinition findDefinitionForStack(ItemStack stack) {
        if (stack.isEmpty()) {
            return null;
        }
        if (cachedDatapack == null) {
            return null;
        }
        Item item = stack.getItem();
        for (JsonElement json : cachedDatapack.values()) {
            var result = SolidThrusterFuelDefinition.CODEC.parse(JsonOps.INSTANCE, json);
            if (result.isError()) {
                continue;
            }
            SolidThrusterFuelDefinition def = result.getOrThrow();
            if (def.requiredMod().isPresent() && !ModList.get().isLoaded(def.requiredMod().get())) {
                continue;
            }
            if (def.isItemEntry() && def.getItem() == item) {
                return def;
            }
            if (def.isTagEntry() && stackMatchesTag(item, def.getItemTag())) {
                return def;
            }
        }
        return null;
    }

    public static int resolveBurnTicks(ItemStack stack) {
        SolidThrusterFuelDefinition def = findDefinitionForStack(stack);
        if (def != null) {
            return def.resolveBurnTicks(stack);
        }
        int smelting = stack.getBurnTime(net.minecraft.world.item.crafting.RecipeType.SMELTING);
        return Math.max(1, smelting > 0 ? smelting : 1600);
    }

    private static void syncToClients() {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
    }

    private static void logReloadSummary(String context, ParseResult result) {
        LOGGER.info(
            "[{}] Solid thruster fuel reload ({}) complete: items={}, tags={}, mergedItems={}, scripted={}, removed={}",
            CreateKinetic.ID,
            context,
            result.itemMap().size(),
            result.tagEntries().size(),
            getFuelPropertiesMap().size(),
            scriptedFuelPropertiesMap.size(),
            removedFuelIds.size()
        );
    }

    private static Integer sanitizeColor(Integer color) {
        return color == null ? null : color & 0xFFFFFF;
    }

    private static boolean registerScriptedFuelInternal(ResourceLocation itemId, float thrustMultiplier, float consumptionMultiplier,
                                                        String particleName, List<String> overrideTextureIds, Integer overrideColor, boolean useItemColor) {
        Item item = BuiltInRegistries.ITEM.get(itemId);
        if (item == null || item == Items.AIR) {
            LOGGER.warn("[{}] KubeJS solid fuel registration failed: item '{}' is not registered.", CreateKinetic.ID, itemId);
            return false;
        }
        ThrusterParticleType particleType = ThrusterParticleType.fromString(particleName);
        List<ResourceLocation> textureOverrides = parseTextureOverrides(overrideTextureIds);
        ItemThrusterProperties properties = new ItemThrusterProperties(
            thrustMultiplier, consumptionMultiplier, particleType, textureOverrides, sanitizeColor(overrideColor), useItemColor);
        scriptedFuelPropertiesMap.put(item, properties);
        scriptedFuelPropertiesById.put(itemId, properties);
        removedFuelIds.remove(itemId);
        syncToClients();
        return true;
    }

    private static List<ResourceLocation> parseTextureOverrides(List<String> ids) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }
        return ids.stream().map(ResourceLocation::tryParse).filter(Objects::nonNull).toList();
    }

    private static float getFloatSetting(Map<String, Object> settings, String camel, String snake, float fallback) {
        Object value = settings.containsKey(camel) ? settings.get(camel) : settings.get(snake);
        if (value == null) {
            return fallback;
        }
        if (value instanceof Number number) {
            return number.floatValue();
        }
        try {
            return Float.parseFloat(value.toString());
        } catch (NumberFormatException ignored) {
            return fallback;
        }
    }

    private static String getStringSetting(Map<String, Object> settings, String camel, String snake, String fallback) {
        Object value = settings.containsKey(camel) ? settings.get(camel) : settings.get(snake);
        return value == null ? fallback : value.toString();
    }

    private static List<String> getStringListSetting(Map<String, Object> settings, String camel, String snake) {
        Object value = settings.containsKey(camel) ? settings.get(camel) : settings.get(snake);
        if (value == null) {
            return List.of();
        }
        if (value instanceof List<?> list) {
            return list.stream().map(Object::toString).toList();
        }
        return List.of(value.toString());
    }

    private static Integer getColorSetting(Map<String, Object> settings, String camel, String snake) {
        Object value = settings.containsKey(camel) ? settings.get(camel) : settings.get(snake);
        if (value == null) {
            return null;
        }
        if (value instanceof Number number) {
            return sanitizeColor(number.intValue());
        }
        String str = value.toString().trim().toLowerCase(Locale.ROOT);
        if (str.startsWith("#")) {
            str = str.substring(1);
        } else if (str.startsWith("0x")) {
            str = str.substring(2);
        }
        try {
            return sanitizeColor(Integer.parseInt(str, 16));
        } catch (NumberFormatException ignored) {
            return null;
        }
    }

    private static boolean getBooleanSetting(Map<String, Object> settings, String camel, String snake, boolean fallback) {
        Object value = settings.containsKey(camel) ? settings.get(camel) : settings.get(snake);
        if (value == null) {
            return fallback;
        }
        if (value instanceof Boolean bool) {
            return bool;
        }
        return Boolean.parseBoolean(value.toString());
    }

    private record ParseResult(
        Map<Item, ItemThrusterProperties> itemMap,
        List<TagFuelEntry> tagEntries,
        int parsedEntries,
        int skippedMissingModEntries,
        int skippedInvalidEntries
    ) {
        private ParseResult {
            itemMap = Objects.requireNonNull(itemMap);
            tagEntries = Objects.requireNonNull(tagEntries);
        }
    }
}
