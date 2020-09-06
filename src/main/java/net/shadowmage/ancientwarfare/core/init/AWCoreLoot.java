package net.shadowmage.ancientwarfare.core.init;

import com.google.common.io.Files;
import com.google.common.io.Resources;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.item.Item;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootEntry;
import net.minecraft.world.storage.loot.LootEntryEmpty;
import net.minecraft.world.storage.loot.LootEntryItem;
import net.minecraft.world.storage.loot.LootEntryTable;
import net.minecraft.world.storage.loot.LootPool;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraft.world.storage.loot.RandomValueRange;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraft.world.storage.loot.conditions.LootConditionManager;
import net.minecraft.world.storage.loot.functions.LootFunction;
import net.minecraft.world.storage.loot.functions.LootFunctionManager;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.shadowmage.ancientwarfare.core.AncientWarfareCore;
import net.shadowmage.ancientwarfare.core.util.FileUtils;
import org.apache.commons.io.FilenameUtils;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mod.EventBusSubscriber(modid = AncientWarfareCore.MOD_ID)
public class AWCoreLoot {

	private static final String CONDITIONS_PROPERTY = "conditions";

	private AWCoreLoot() {}

	private static final Gson GSON = (new GsonBuilder()).registerTypeAdapter(RandomValueRange.class, new RandomValueRange.Serializer()).registerTypeAdapter(LootPool.class, new LootPoolSerializer()).registerTypeAdapter(LootTable.class, new LootTable.Serializer()).registerTypeHierarchyAdapter(LootEntry.class, new LootEntrySerializer()).registerTypeHierarchyAdapter(LootFunction.class, new LootFunctionManager.Serializer()).registerTypeHierarchyAdapter(LootCondition.class, new LootConditionManager.Serializer()).registerTypeHierarchyAdapter(LootContext.EntityTarget.class, new LootContext.EntityTarget.Serializer()).create();
	private static final String CHESTS_DIRECTORY = "assets/ancientwarfare/loot_tables/chests";
	private static final Map<String, Path> injectTables = new HashMap<>();
	private static final String INJECT_FOLDER = "inject";
	private static final String COMPAT_FOLDER = "compat";

	public static void init() {
		loadChestLootTables();
	}

	private static void loadChestLootTables() {
		//noinspection ConstantConditions
		FileUtils.findFiles(Loader.instance().activeModContainer().getSource(), CHESTS_DIRECTORY, (root, file) -> {
			String extension = FilenameUtils.getExtension(file.toString());

			if (!extension.equals("json")) {
				return;
			}

			Path relative = root.relativize(file);
			if (relative.getName(0).toString().equals(COMPAT_FOLDER) && relative.getNameCount() > 1) {
				String modName = relative.getName(1).toString();
				if (!Loader.isModLoaded(modName)) {
					return;
				}
				if (relative.getNameCount() > 2 && relative.getName(2).toString().equals(INJECT_FOLDER)) {
					String lootTableName = convertToRegistryName(root.resolve(COMPAT_FOLDER).resolve(modName).resolve(INJECT_FOLDER).relativize(file));
					injectTables.put(lootTableName, file);
					return;
				} else {
					LootTableList.register(new ResourceLocation(AncientWarfareCore.MOD_ID, convertToRegistryName(relative)));
					return;
				}
			} else if (relative.getName(0).toString().equals(INJECT_FOLDER)) {
				String lootTableName = convertToRegistryName(root.resolve(INJECT_FOLDER).relativize(file));
				injectTables.put(lootTableName, file);
				return;
			}

			LootTableList.register(new ResourceLocation(AncientWarfareCore.MOD_ID, convertToRegistryName(relative)));
		});
	}

	private static String convertToRegistryName(Path path) {
		return "chests/" + FilenameUtils.removeExtension(path.toString()).replaceAll("\\\\", "/");
	}

	@SubscribeEvent
	public static void lootLoad(LootTableLoadEvent evt) {
		String resPath = evt.getName().getResourcePath();

		if (injectTables.containsKey(resPath)) {
			String jsonContents;
			String jsonPath = injectTables.get(resPath).toString();
			try {
				File file = new File(jsonPath);
				if (file.exists()) {
					//noinspection UnstableApiUsage
					jsonContents = Files.toString(file, StandardCharsets.UTF_8);
				} else {
					//noinspection UnstableApiUsage
					jsonContents = Resources.toString(AWCoreLoot.class.getResource(jsonPath), StandardCharsets.UTF_8);
				}
			}
			catch (IOException e) {
				AncientWarfareCore.LOG.error("Error reading loot table json {}", jsonPath, e);
				return;
			}
			LootTable injectedLootTable = GSON.fromJson(jsonContents, LootTable.class);
			LootTable lootTable = evt.getTable();
			for (LootPool injectedPool : getLootPools(injectedLootTable)) {
				LootPool lootPool = lootTable.getPool(injectedPool.getName());
				//noinspection ConstantConditions
				if (lootPool != null) {
					injectEntriesIntoPool(injectedPool, lootPool);
				} else {
					lootTable.addPool(injectedPool);
				}
			}
		}
	}

	private static class LootPoolSerializer implements JsonDeserializer<LootPool> {
		@Override
		public LootPool deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
			JsonObject jsonobject = JsonUtils.getJsonObject(json, "loot pool");
			String name = JsonUtils.getString(jsonobject, "name");
			LootEntry[] alootentry = JsonUtils.deserializeClass(jsonobject, "entries", context, LootEntry[].class);
			LootCondition[] alootcondition = JsonUtils.deserializeClass(jsonobject, CONDITIONS_PROPERTY, new LootCondition[0], context, LootCondition[].class);
			RandomValueRange rolls = JsonUtils.deserializeClass(jsonobject, "rolls", new RandomValueRange(1, 1), context, RandomValueRange.class);
			RandomValueRange bonusRolls = JsonUtils.deserializeClass(jsonobject, "bonus_rolls", new RandomValueRange(0.0F, 0.0F), context, RandomValueRange.class);
			return new LootPool(alootentry, alootcondition, rolls, bonusRolls, name);
		}
	}

	private static class LootEntrySerializer implements JsonDeserializer<LootEntry> {
		@Override
		public LootEntry deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
			JsonObject jsonobject = JsonUtils.getJsonObject(json, "loot item");
			String s = JsonUtils.getString(jsonobject, "type");
			int weight = JsonUtils.getInt(jsonobject, "weight", 1);
			int quality = JsonUtils.getInt(jsonobject, "quality", 0);
			LootCondition[] conditions;

			if (jsonobject.has(CONDITIONS_PROPERTY)) {
				conditions = JsonUtils.deserializeClass(jsonobject, CONDITIONS_PROPERTY, context, LootCondition[].class);
			} else {
				conditions = new LootCondition[0];
			}

			switch (s) {
				case "item":
					String name = JsonUtils.getString(jsonobject, "name");
					Item item = JsonUtils.getItem(jsonobject, "name");
					LootFunction[] alootfunction;

					if (jsonobject.has("functions")) {
						alootfunction = JsonUtils.deserializeClass(jsonobject, "functions", context, LootFunction[].class);
					} else {
						alootfunction = new LootFunction[0];
					}

					return new LootEntryItem(item, weight, quality, alootfunction, conditions, name);
				case "loot_table":
					String tableName = JsonUtils.getString(json, "name");
					ResourceLocation resourcelocation = new ResourceLocation(tableName);
					return new LootEntryTable(resourcelocation, weight, quality, conditions, tableName);
				case "empty":
					return new LootEntryEmpty(weight, quality, conditions, "empty");
				default:
					throw new JsonSyntaxException("Unknown loot entry type '" + s + "'");
			}
		}
	}

	private static void injectEntriesIntoPool(LootPool injectedPool, LootPool lootPool) {
		List<LootEntry> lootEntries = getLootEntries(injectedPool);
		if (lootEntries != null) {
			for (LootEntry entry : lootEntries) {
				lootPool.addEntry(entry);
			}
		}
	}

	private static final Field POOLS = ObfuscationReflectionHelper.findField(LootTable.class, "field_186466_c");

	private static List<LootPool> getLootPools(LootTable lootTable) {
		try {
			//noinspection unchecked
			return (List<LootPool>) POOLS.get(lootTable);
		}
		catch (IllegalAccessException e) {
			AncientWarfareCore.LOG.error("Error accessing pools field of LootTable", e);
			return new ArrayList<>();
		}
	}

	private static final Field LOOT_ENTRIES = ObfuscationReflectionHelper.findField(LootPool.class, "field_186453_a");

	@Nullable
	private static List<LootEntry> getLootEntries(LootPool lootPool) {
		try {
			//noinspection unchecked
			return (List<LootEntry>) LOOT_ENTRIES.get(lootPool);
		}
		catch (IllegalAccessException e) {
			AncientWarfareCore.LOG.error("Error accessing lootEntries field of LootPool", e);
			return new ArrayList<>();
		}
	}
}
