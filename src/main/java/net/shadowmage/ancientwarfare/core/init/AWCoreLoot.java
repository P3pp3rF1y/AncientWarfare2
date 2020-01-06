package net.shadowmage.ancientwarfare.core.init;

import com.google.common.collect.ImmutableList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootEntry;
import net.minecraft.world.storage.loot.LootEntryTable;
import net.minecraft.world.storage.loot.LootPool;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraft.world.storage.loot.RandomValueRange;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.shadowmage.ancientwarfare.core.AncientWarfareCore;

import java.util.List;

@Mod.EventBusSubscriber(modid = AncientWarfareCore.MOD_ID)
public class AWCoreLoot {
	private static final List<String> CHEST_INJECT_TABLES = ImmutableList.of("abandoned_mineshaft", "desert_pyramid", "igloo_chest", "jungle_temple", "simple_dungeon", "stronghold_corridor", "village_blacksmith");
	private static final List<String> CHEST_TABLES = ImmutableList.of("amazon", "ammo_catapult_trebuchet", "ammo_cannon", "ammo_ballista", "ammo_hwacha", "archivist", "barbarian", "beast", "beast_treasure_hoard", "brigand", "buffloka", "buffloka_forest", "buffloka_plains", "castle_armoury_chainmail", "castle_armoury_diamond", "castle_armoury_gold", "castle_armoury_iron", "castle_armoury_stone_leather", "castle_pantry_meat_cooked", "castle_pantry_meat_raw", "castle_pantry_mixed", "castle_pantry_vegetable", "castle_treasury_high_value", "castle_treasury_low_value", "castle_treasury_medium_value", "coven", "demon", "dwarf", "elf", "empire", "empire_court", "enchanted_book", "ent", "evil", "giant", "gnome", "goblin", "good", "gremlin", "guardian", "guild", "hobbit", "", "icelord", "ishtari", "klown", "kong", "lizardman", "malice", "minossian", "monster", "monster_treasure_hoard", "nogg", "norska", "orc", "pirate", "pirate_provisions", "pirate_treasure", "potion_beneficial", "potion_harmful", "rakshasa", "research_note", "sarkonid", "sealsker", "smingol", "undead", "vampire", "village_blacksmith", "village_church", "village_farm_chicken", "village_farm_cow", "village_farm_mixed", "village_farm_pig", "village_farm_sheep", "village_farm_sugarcane", "village_farm_vegetable", "village_farm_wheat", "village_fisherman", "village_library", "village_magician", "village_mill", "village_mine", "vyncan", "vyncan_jungle", "vyncan_mesa", "witchbane", "wizardly", "xoltec", "xoltec_terracotta", "zimba", "zimba_ancient_mines");


	public static void init() {
		for (String s : CHEST_INJECT_TABLES) {
			LootTableList.register(new ResourceLocation(AncientWarfareCore.MOD_ID, "inject/chests/" + s));
		}
		for (String s : CHEST_TABLES) {
			LootTableList.register(new ResourceLocation(AncientWarfareCore.MOD_ID, "chests/" + s));
		}
	}

	private static final String CHESTS_PREFIX = "minecraft:chests/";

	@SubscribeEvent
	public static void lootLoad(LootTableLoadEvent evt) {
		String name = evt.getName().toString();

		if (name.startsWith(CHESTS_PREFIX) && CHEST_INJECT_TABLES.contains(name.substring(CHESTS_PREFIX.length()))) {
			String file = name.substring("minecraft:".length());
			evt.getTable().addPool(getInjectPool(file));
		}
	}

	private static LootPool getInjectPool(String entryName) {
		return new LootPool(new LootEntry[] {getInjectEntry(entryName)}, new LootCondition[0], new RandomValueRange(1), new RandomValueRange(0, 1), AncientWarfareCore.MOD_ID + "_inject_pool");
	}

	private static LootEntryTable getInjectEntry(String name) {
		return new LootEntryTable(new ResourceLocation(AncientWarfareCore.MOD_ID, "inject/" + name), 1, 0, new LootCondition[0], AncientWarfareCore.MOD_ID + "_inject_entry");
	}
}
