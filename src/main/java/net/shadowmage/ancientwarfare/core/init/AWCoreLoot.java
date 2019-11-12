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
	private static final List<String> CHEST_TABLES = ImmutableList.of("abandoned_mineshaft", "desert_pyramid", "igloo_chest", "jungle_temple", "simple_dungeon", "stronghold_corridor", "village_blacksmith");
	private static final List<String> FACTION_CHEST_TABLES = ImmutableList.of("empire", "norska", "sarkonid", "xoltec", "witchbane", "nogg", "buffloka", "zimba", "kong", "orc", "brigand", "pirate", "evil", "good", "wizardly", "elf", "dwarf", "hobbit", "undead", "demon", "barbarian", "smingol", "rakshasa", "vyncan", "sealsker", "guild", "klown", "coven", "minossian", "icelord", "lizardman", "amazon", "ishtari", "monster", "beast", "ent", "gnome", "gremlin", "giant", "vampire");

	public static void init() {
		for (String s : CHEST_TABLES) {
			LootTableList.register(new ResourceLocation(AncientWarfareCore.MOD_ID, "inject/chests/" + s));
		}
		for (String s : FACTION_CHEST_TABLES) {
			LootTableList.register(new ResourceLocation(AncientWarfareCore.MOD_ID, "factions/chests/" + s));
		}
	}

	private static final String CHESTS_PREFIX = "minecraft:chests/";

	@SubscribeEvent
	public static void lootLoad(LootTableLoadEvent evt) {
		String name = evt.getName().toString();

		if (name.startsWith(CHESTS_PREFIX) && CHEST_TABLES.contains(name.substring(CHESTS_PREFIX.length()))) {
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
