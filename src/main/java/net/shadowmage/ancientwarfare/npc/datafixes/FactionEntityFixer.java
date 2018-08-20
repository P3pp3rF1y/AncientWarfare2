package net.shadowmage.ancientwarfare.npc.datafixes;

import com.google.common.collect.ImmutableMap;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.Tuple;
import net.minecraft.util.datafix.IFixableData;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent.MissingMappings;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.shadowmage.ancientwarfare.npc.AncientWarfareNPC;

import java.util.Map;

import static net.shadowmage.ancientwarfare.core.entity.AWEntityRegistry.*;

public class FactionEntityFixer implements IFixableData {
	private Map<String, Tuple<String, String>> factionFixes = new ImmutableMap.Builder<String, Tuple<String, String>>()
			.put("ancientwarfarenpc:bandit.archer", new Tuple<>(AncientWarfareNPC.MOD_ID + ":" + NPC_FACTION_ARCHER, "bandit"))
			.put("ancientwarfarenpc:bandit.archer.elite", new Tuple<>(AncientWarfareNPC.MOD_ID + ":" + NPC_FACTION_ARCHER_ELITE, "bandit"))
			.put("ancientwarfarenpc:bandit.bard", new Tuple<>(AncientWarfareNPC.MOD_ID + ":" + NPC_FACTION_BARD, "bandit"))
			.put("ancientwarfarenpc:bandit.cavalry", new Tuple<>(AncientWarfareNPC.MOD_ID + ":" + NPC_FACTION_CAVALRY, "bandit"))
			.put("ancientwarfarenpc:bandit.civilian.female", new Tuple<>(AncientWarfareNPC.MOD_ID + ":" + NPC_FACTION_CIVILIAN_FEMALE, "bandit"))
			.put("ancientwarfarenpc:bandit.civilian.male", new Tuple<>(AncientWarfareNPC.MOD_ID + ":" + NPC_FACTION_CIVILIAN_MALE, "bandit"))
			.put("ancientwarfarenpc:bandit.leader", new Tuple<>(AncientWarfareNPC.MOD_ID + ":" + NPC_FACTION_COMMANDER, "bandit"))
			.put("ancientwarfarenpc:bandit.leader.elite", new Tuple<>(AncientWarfareNPC.MOD_ID + ":" + NPC_FACTION_LEADER_ELITE, "bandit"))
			.put("ancientwarfarenpc:bandit.mounted_archer", new Tuple<>(AncientWarfareNPC.MOD_ID + ":" + NPC_FACTION_MOUNTED_ARCHER, "bandit"))
			.put("ancientwarfarenpc:bandit.priest", new Tuple<>(AncientWarfareNPC.MOD_ID + ":" + NPC_FACTION_PRIEST, "bandit"))
			.put("ancientwarfarenpc:bandit.soldier", new Tuple<>(AncientWarfareNPC.MOD_ID + ":" + NPC_FACTION_SOLDIER, "bandit"))
			.put("ancientwarfarenpc:bandit.soldier.elite", new Tuple<>(AncientWarfareNPC.MOD_ID + ":" + NPC_FACTION_SOLDIER_ELITE, "bandit"))
			.put("ancientwarfarenpc:bandit.trader", new Tuple<>(AncientWarfareNPC.MOD_ID + ":" + NPC_FACTION_TRADER, "bandit"))
			.put("ancientwarfarenpc:custom_1.archer", new Tuple<>(AncientWarfareNPC.MOD_ID + ":" + NPC_FACTION_ARCHER, "custom_1"))
			.put("ancientwarfarenpc:custom_1.archer.elite", new Tuple<>(AncientWarfareNPC.MOD_ID + ":" + NPC_FACTION_ARCHER_ELITE, "custom_1"))
			.put("ancientwarfarenpc:custom_1.bard", new Tuple<>(AncientWarfareNPC.MOD_ID + ":" + NPC_FACTION_BARD, "custom_1"))
			.put("ancientwarfarenpc:custom_1.cavalry", new Tuple<>(AncientWarfareNPC.MOD_ID + ":" + NPC_FACTION_CAVALRY, "custom_1"))
			.put("ancientwarfarenpc:custom_1.civilian.female", new Tuple<>(AncientWarfareNPC.MOD_ID + ":" + NPC_FACTION_CIVILIAN_FEMALE, "custom_1"))
			.put("ancientwarfarenpc:custom_1.civilian.male", new Tuple<>(AncientWarfareNPC.MOD_ID + ":" + NPC_FACTION_CIVILIAN_MALE, "custom_1"))
			.put("ancientwarfarenpc:custom_1.leader", new Tuple<>(AncientWarfareNPC.MOD_ID + ":" + NPC_FACTION_COMMANDER, "custom_1"))
			.put("ancientwarfarenpc:custom_1.leader.elite", new Tuple<>(AncientWarfareNPC.MOD_ID + ":" + NPC_FACTION_LEADER_ELITE, "custom_1"))
			.put("ancientwarfarenpc:custom_1.mounted_archer", new Tuple<>(AncientWarfareNPC.MOD_ID + ":" + NPC_FACTION_MOUNTED_ARCHER, "custom_1"))
			.put("ancientwarfarenpc:custom_1.priest", new Tuple<>(AncientWarfareNPC.MOD_ID + ":" + NPC_FACTION_PRIEST, "custom_1"))
			.put("ancientwarfarenpc:custom_1.soldier", new Tuple<>(AncientWarfareNPC.MOD_ID + ":" + NPC_FACTION_SOLDIER, "custom_1"))
			.put("ancientwarfarenpc:custom_1.soldier.elite", new Tuple<>(AncientWarfareNPC.MOD_ID + ":" + NPC_FACTION_SOLDIER_ELITE, "custom_1"))
			.put("ancientwarfarenpc:custom_1.trader", new Tuple<>(AncientWarfareNPC.MOD_ID + ":" + NPC_FACTION_TRADER, "custom_1"))
			.put("ancientwarfarenpc:custom_2.archer", new Tuple<>(AncientWarfareNPC.MOD_ID + ":" + NPC_FACTION_ARCHER, "custom_2"))
			.put("ancientwarfarenpc:custom_2.archer.elite", new Tuple<>(AncientWarfareNPC.MOD_ID + ":" + NPC_FACTION_ARCHER_ELITE, "custom_2"))
			.put("ancientwarfarenpc:custom_2.bard", new Tuple<>(AncientWarfareNPC.MOD_ID + ":" + NPC_FACTION_BARD, "custom_2"))
			.put("ancientwarfarenpc:custom_2.cavalry", new Tuple<>(AncientWarfareNPC.MOD_ID + ":" + NPC_FACTION_CAVALRY, "custom_2"))
			.put("ancientwarfarenpc:custom_2.civilian.female", new Tuple<>(AncientWarfareNPC.MOD_ID + ":" + NPC_FACTION_CIVILIAN_FEMALE, "custom_2"))
			.put("ancientwarfarenpc:custom_2.civilian.male", new Tuple<>(AncientWarfareNPC.MOD_ID + ":" + NPC_FACTION_CIVILIAN_MALE, "custom_2"))
			.put("ancientwarfarenpc:custom_2.leader", new Tuple<>(AncientWarfareNPC.MOD_ID + ":" + NPC_FACTION_COMMANDER, "custom_2"))
			.put("ancientwarfarenpc:custom_2.leader.elite", new Tuple<>(AncientWarfareNPC.MOD_ID + ":" + NPC_FACTION_LEADER_ELITE, "custom_2"))
			.put("ancientwarfarenpc:custom_2.mounted_archer", new Tuple<>(AncientWarfareNPC.MOD_ID + ":" + NPC_FACTION_MOUNTED_ARCHER, "custom_2"))
			.put("ancientwarfarenpc:custom_2.priest", new Tuple<>(AncientWarfareNPC.MOD_ID + ":" + NPC_FACTION_PRIEST, "custom_2"))
			.put("ancientwarfarenpc:custom_2.soldier", new Tuple<>(AncientWarfareNPC.MOD_ID + ":" + NPC_FACTION_SOLDIER, "custom_2"))
			.put("ancientwarfarenpc:custom_2.soldier.elite", new Tuple<>(AncientWarfareNPC.MOD_ID + ":" + NPC_FACTION_SOLDIER_ELITE, "custom_2"))
			.put("ancientwarfarenpc:custom_2.trader", new Tuple<>(AncientWarfareNPC.MOD_ID + ":" + NPC_FACTION_TRADER, "custom_2"))
			.put("ancientwarfarenpc:custom_3.archer", new Tuple<>(AncientWarfareNPC.MOD_ID + ":" + NPC_FACTION_ARCHER, "custom_3"))
			.put("ancientwarfarenpc:custom_3.archer.elite", new Tuple<>(AncientWarfareNPC.MOD_ID + ":" + NPC_FACTION_ARCHER_ELITE, "custom_3"))
			.put("ancientwarfarenpc:custom_3.bard", new Tuple<>(AncientWarfareNPC.MOD_ID + ":" + NPC_FACTION_BARD, "custom_3"))
			.put("ancientwarfarenpc:custom_3.cavalry", new Tuple<>(AncientWarfareNPC.MOD_ID + ":" + NPC_FACTION_CAVALRY, "custom_3"))
			.put("ancientwarfarenpc:custom_3.civilian.female", new Tuple<>(AncientWarfareNPC.MOD_ID + ":" + NPC_FACTION_CIVILIAN_FEMALE, "custom_3"))
			.put("ancientwarfarenpc:custom_3.civilian.male", new Tuple<>(AncientWarfareNPC.MOD_ID + ":" + NPC_FACTION_CIVILIAN_MALE, "custom_3"))
			.put("ancientwarfarenpc:custom_3.leader", new Tuple<>(AncientWarfareNPC.MOD_ID + ":" + NPC_FACTION_COMMANDER, "custom_3"))
			.put("ancientwarfarenpc:custom_3.leader.elite", new Tuple<>(AncientWarfareNPC.MOD_ID + ":" + NPC_FACTION_LEADER_ELITE, "custom_3"))
			.put("ancientwarfarenpc:custom_3.mounted_archer", new Tuple<>(AncientWarfareNPC.MOD_ID + ":" + NPC_FACTION_MOUNTED_ARCHER, "custom_3"))
			.put("ancientwarfarenpc:custom_3.priest", new Tuple<>(AncientWarfareNPC.MOD_ID + ":" + NPC_FACTION_PRIEST, "custom_3"))
			.put("ancientwarfarenpc:custom_3.soldier", new Tuple<>(AncientWarfareNPC.MOD_ID + ":" + NPC_FACTION_SOLDIER, "custom_3"))
			.put("ancientwarfarenpc:custom_3.soldier.elite", new Tuple<>(AncientWarfareNPC.MOD_ID + ":" + NPC_FACTION_SOLDIER_ELITE, "custom_3"))
			.put("ancientwarfarenpc:custom_3.trader", new Tuple<>(AncientWarfareNPC.MOD_ID + ":" + NPC_FACTION_TRADER, "custom_3"))
			.put("ancientwarfarenpc:desert.archer", new Tuple<>(AncientWarfareNPC.MOD_ID + ":" + NPC_FACTION_ARCHER, "desert"))
			.put("ancientwarfarenpc:desert.archer.elite", new Tuple<>(AncientWarfareNPC.MOD_ID + ":" + NPC_FACTION_ARCHER_ELITE, "desert"))
			.put("ancientwarfarenpc:desert.bard", new Tuple<>(AncientWarfareNPC.MOD_ID + ":" + NPC_FACTION_BARD, "desert"))
			.put("ancientwarfarenpc:desert.cavalry", new Tuple<>(AncientWarfareNPC.MOD_ID + ":" + NPC_FACTION_CAVALRY, "desert"))
			.put("ancientwarfarenpc:desert.civilian.female", new Tuple<>(AncientWarfareNPC.MOD_ID + ":" + NPC_FACTION_CIVILIAN_FEMALE, "desert"))
			.put("ancientwarfarenpc:desert.civilian.male", new Tuple<>(AncientWarfareNPC.MOD_ID + ":" + NPC_FACTION_CIVILIAN_MALE, "desert"))
			.put("ancientwarfarenpc:desert.leader", new Tuple<>(AncientWarfareNPC.MOD_ID + ":" + NPC_FACTION_COMMANDER, "desert"))
			.put("ancientwarfarenpc:desert.leader.elite", new Tuple<>(AncientWarfareNPC.MOD_ID + ":" + NPC_FACTION_LEADER_ELITE, "desert"))
			.put("ancientwarfarenpc:desert.mounted_archer", new Tuple<>(AncientWarfareNPC.MOD_ID + ":" + NPC_FACTION_MOUNTED_ARCHER, "desert"))
			.put("ancientwarfarenpc:desert.priest", new Tuple<>(AncientWarfareNPC.MOD_ID + ":" + NPC_FACTION_PRIEST, "desert"))
			.put("ancientwarfarenpc:desert.soldier", new Tuple<>(AncientWarfareNPC.MOD_ID + ":" + NPC_FACTION_SOLDIER, "desert"))
			.put("ancientwarfarenpc:desert.soldier.elite", new Tuple<>(AncientWarfareNPC.MOD_ID + ":" + NPC_FACTION_SOLDIER_ELITE, "desert"))
			.put("ancientwarfarenpc:desert.trader", new Tuple<>(AncientWarfareNPC.MOD_ID + ":" + NPC_FACTION_TRADER, "desert"))
			.put("ancientwarfarenpc:native.archer", new Tuple<>(AncientWarfareNPC.MOD_ID + ":" + NPC_FACTION_ARCHER, "native"))
			.put("ancientwarfarenpc:native.archer.elite", new Tuple<>(AncientWarfareNPC.MOD_ID + ":" + NPC_FACTION_ARCHER_ELITE, "native"))
			.put("ancientwarfarenpc:native.bard", new Tuple<>(AncientWarfareNPC.MOD_ID + ":" + NPC_FACTION_BARD, "native"))
			.put("ancientwarfarenpc:native.cavalry", new Tuple<>(AncientWarfareNPC.MOD_ID + ":" + NPC_FACTION_CAVALRY, "native"))
			.put("ancientwarfarenpc:native.civilian.female", new Tuple<>(AncientWarfareNPC.MOD_ID + ":" + NPC_FACTION_CIVILIAN_FEMALE, "native"))
			.put("ancientwarfarenpc:native.civilian.male", new Tuple<>(AncientWarfareNPC.MOD_ID + ":" + NPC_FACTION_CIVILIAN_MALE, "native"))
			.put("ancientwarfarenpc:native.leader", new Tuple<>(AncientWarfareNPC.MOD_ID + ":" + NPC_FACTION_COMMANDER, "native"))
			.put("ancientwarfarenpc:native.leader.elite", new Tuple<>(AncientWarfareNPC.MOD_ID + ":" + NPC_FACTION_LEADER_ELITE, "native"))
			.put("ancientwarfarenpc:native.mounted_archer", new Tuple<>(AncientWarfareNPC.MOD_ID + ":" + NPC_FACTION_MOUNTED_ARCHER, "native"))
			.put("ancientwarfarenpc:native.priest", new Tuple<>(AncientWarfareNPC.MOD_ID + ":" + NPC_FACTION_PRIEST, "native"))
			.put("ancientwarfarenpc:native.soldier", new Tuple<>(AncientWarfareNPC.MOD_ID + ":" + NPC_FACTION_SOLDIER, "native"))
			.put("ancientwarfarenpc:native.soldier.elite", new Tuple<>(AncientWarfareNPC.MOD_ID + ":" + NPC_FACTION_SOLDIER_ELITE, "native"))
			.put("ancientwarfarenpc:native.trader", new Tuple<>(AncientWarfareNPC.MOD_ID + ":" + NPC_FACTION_TRADER, "native"))
			.put("ancientwarfarenpc:pirate.archer", new Tuple<>(AncientWarfareNPC.MOD_ID + ":" + NPC_FACTION_ARCHER, "pirate"))
			.put("ancientwarfarenpc:pirate.archer.elite", new Tuple<>(AncientWarfareNPC.MOD_ID + ":" + NPC_FACTION_ARCHER_ELITE, "pirate"))
			.put("ancientwarfarenpc:pirate.bard", new Tuple<>(AncientWarfareNPC.MOD_ID + ":" + NPC_FACTION_BARD, "pirate"))
			.put("ancientwarfarenpc:pirate.cavalry", new Tuple<>(AncientWarfareNPC.MOD_ID + ":" + NPC_FACTION_CAVALRY, "pirate"))
			.put("ancientwarfarenpc:pirate.civilian.female", new Tuple<>(AncientWarfareNPC.MOD_ID + ":" + NPC_FACTION_CIVILIAN_FEMALE, "pirate"))
			.put("ancientwarfarenpc:pirate.civilian.male", new Tuple<>(AncientWarfareNPC.MOD_ID + ":" + NPC_FACTION_CIVILIAN_MALE, "pirate"))
			.put("ancientwarfarenpc:pirate.leader", new Tuple<>(AncientWarfareNPC.MOD_ID + ":" + NPC_FACTION_COMMANDER, "pirate"))
			.put("ancientwarfarenpc:pirate.leader.elite", new Tuple<>(AncientWarfareNPC.MOD_ID + ":" + NPC_FACTION_LEADER_ELITE, "pirate"))
			.put("ancientwarfarenpc:pirate.mounted_archer", new Tuple<>(AncientWarfareNPC.MOD_ID + ":" + NPC_FACTION_MOUNTED_ARCHER, "pirate"))
			.put("ancientwarfarenpc:pirate.priest", new Tuple<>(AncientWarfareNPC.MOD_ID + ":" + NPC_FACTION_PRIEST, "pirate"))
			.put("ancientwarfarenpc:pirate.soldier", new Tuple<>(AncientWarfareNPC.MOD_ID + ":" + NPC_FACTION_SOLDIER, "pirate"))
			.put("ancientwarfarenpc:pirate.soldier.elite", new Tuple<>(AncientWarfareNPC.MOD_ID + ":" + NPC_FACTION_SOLDIER_ELITE, "pirate"))
			.put("ancientwarfarenpc:pirate.trader", new Tuple<>(AncientWarfareNPC.MOD_ID + ":" + NPC_FACTION_TRADER, "pirate"))
			.put("ancientwarfarenpc:viking.archer", new Tuple<>(AncientWarfareNPC.MOD_ID + ":" + NPC_FACTION_ARCHER, "viking"))
			.put("ancientwarfarenpc:viking.archer.elite", new Tuple<>(AncientWarfareNPC.MOD_ID + ":" + NPC_FACTION_ARCHER_ELITE, "viking"))
			.put("ancientwarfarenpc:viking.bard", new Tuple<>(AncientWarfareNPC.MOD_ID + ":" + NPC_FACTION_BARD, "viking"))
			.put("ancientwarfarenpc:viking.cavalry", new Tuple<>(AncientWarfareNPC.MOD_ID + ":" + NPC_FACTION_CAVALRY, "viking"))
			.put("ancientwarfarenpc:viking.civilian.female", new Tuple<>(AncientWarfareNPC.MOD_ID + ":" + NPC_FACTION_CIVILIAN_FEMALE, "viking"))
			.put("ancientwarfarenpc:viking.civilian.male", new Tuple<>(AncientWarfareNPC.MOD_ID + ":" + NPC_FACTION_CIVILIAN_MALE, "viking"))
			.put("ancientwarfarenpc:viking.leader", new Tuple<>(AncientWarfareNPC.MOD_ID + ":" + NPC_FACTION_COMMANDER, "viking"))
			.put("ancientwarfarenpc:viking.leader.elite", new Tuple<>(AncientWarfareNPC.MOD_ID + ":" + NPC_FACTION_LEADER_ELITE, "viking"))
			.put("ancientwarfarenpc:viking.mounted_archer", new Tuple<>(AncientWarfareNPC.MOD_ID + ":" + NPC_FACTION_MOUNTED_ARCHER, "viking"))
			.put("ancientwarfarenpc:viking.priest", new Tuple<>(AncientWarfareNPC.MOD_ID + ":" + NPC_FACTION_PRIEST, "viking"))
			.put("ancientwarfarenpc:viking.soldier", new Tuple<>(AncientWarfareNPC.MOD_ID + ":" + NPC_FACTION_SOLDIER, "viking"))
			.put("ancientwarfarenpc:viking.soldier.elite", new Tuple<>(AncientWarfareNPC.MOD_ID + ":" + NPC_FACTION_SOLDIER_ELITE, "viking"))
			.put("ancientwarfarenpc:viking.trader", new Tuple<>(AncientWarfareNPC.MOD_ID + ":" + NPC_FACTION_TRADER, "viking"))
			.build();

	public FactionEntityFixer() {
		MinecraftForge.EVENT_BUS.register(this);
	}

	@Override
	public int getFixVersion() {
		return 3;
	}

	@Override
	public NBTTagCompound fixTagCompound(NBTTagCompound compound) {
		String id = compound.getString("id");
		if (factionFixes.keySet().contains(id)) {
			Tuple<String, String> idFaction = factionFixes.get(id);
			compound.setString("id", idFaction.getFirst());
			compound.setString("factionName", idFaction.getSecond());
			if (compound.hasKey("horseAI") && compound.getCompoundTag("horseAI").getBoolean("warHorseKilled")) {
				compound.setBoolean("horseLives", false);
			}
		}

		return compound;
	}

	@SubscribeEvent
	public void missingMapping(MissingMappings<EntityEntry> event) {
		for (MissingMappings.Mapping<EntityEntry> entry : event.getAllMappings()) {
			if (factionFixes.keySet().contains(entry.key.toString())) {
				entry.ignore();
			}
		}
	}
}
