package net.shadowmage.ancientwarfare.npc.datafixes;

import com.google.common.collect.ImmutableMap;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.Tuple;
import net.minecraft.util.datafix.IFixableData;
import net.shadowmage.ancientwarfare.npc.init.AWNPCItems;

import java.util.Map;

import static net.shadowmage.ancientwarfare.core.entity.AWEntityRegistry.*;

public class FactionSpawnerItemFixer implements IFixableData {
	private Map<String, Tuple<String, String>> factionItemFixes = new ImmutableMap.Builder<String, Tuple<String, String>>()
			.put("bandit.archer", new Tuple<>(NPC_FACTION_ARCHER, "bandit"))
			.put("bandit.archer.elite", new Tuple<>(NPC_FACTION_ARCHER_ELITE, "bandit"))
			.put("bandit.bard", new Tuple<>(NPC_FACTION_BARD, "bandit"))
			.put("bandit.cavalry", new Tuple<>(NPC_FACTION_CAVALRY, "bandit"))
			.put("bandit.civilian.female", new Tuple<>(NPC_FACTION_CIVILIAN_FEMALE, "bandit"))
			.put("bandit.civilian.male", new Tuple<>(NPC_FACTION_CIVILIAN_MALE, "bandit"))
			.put("bandit.leader", new Tuple<>(NPC_FACTION_COMMANDER, "bandit"))
			.put("bandit.leader.elite", new Tuple<>(NPC_FACTION_LEADER_ELITE, "bandit"))
			.put("bandit.mounted_archer", new Tuple<>(NPC_FACTION_MOUNTED_ARCHER, "bandit"))
			.put("bandit.priest", new Tuple<>(NPC_FACTION_PRIEST, "bandit"))
			.put("bandit.soldier", new Tuple<>(NPC_FACTION_SOLDIER, "bandit"))
			.put("bandit.soldier.elite", new Tuple<>(NPC_FACTION_SOLDIER_ELITE, "bandit"))
			.put("bandit.trader", new Tuple<>(NPC_FACTION_TRADER, "bandit"))
			.put("custom_1.archer", new Tuple<>(NPC_FACTION_ARCHER, "custom_1"))
			.put("custom_1.archer.elite", new Tuple<>(NPC_FACTION_ARCHER_ELITE, "custom_1"))
			.put("custom_1.bard", new Tuple<>(NPC_FACTION_BARD, "custom_1"))
			.put("custom_1.cavalry", new Tuple<>(NPC_FACTION_CAVALRY, "custom_1"))
			.put("custom_1.civilian.female", new Tuple<>(NPC_FACTION_CIVILIAN_FEMALE, "custom_1"))
			.put("custom_1.civilian.male", new Tuple<>(NPC_FACTION_CIVILIAN_MALE, "custom_1"))
			.put("custom_1.leader", new Tuple<>(NPC_FACTION_COMMANDER, "custom_1"))
			.put("custom_1.leader.elite", new Tuple<>(NPC_FACTION_LEADER_ELITE, "custom_1"))
			.put("custom_1.mounted_archer", new Tuple<>(NPC_FACTION_MOUNTED_ARCHER, "custom_1"))
			.put("custom_1.priest", new Tuple<>(NPC_FACTION_PRIEST, "custom_1"))
			.put("custom_1.soldier", new Tuple<>(NPC_FACTION_SOLDIER, "custom_1"))
			.put("custom_1.soldier.elite", new Tuple<>(NPC_FACTION_SOLDIER_ELITE, "custom_1"))
			.put("custom_1.trader", new Tuple<>(NPC_FACTION_TRADER, "custom_1"))
			.put("custom_2.archer", new Tuple<>(NPC_FACTION_ARCHER, "custom_2"))
			.put("custom_2.archer.elite", new Tuple<>(NPC_FACTION_ARCHER_ELITE, "custom_2"))
			.put("custom_2.bard", new Tuple<>(NPC_FACTION_BARD, "custom_2"))
			.put("custom_2.cavalry", new Tuple<>(NPC_FACTION_CAVALRY, "custom_2"))
			.put("custom_2.civilian.female", new Tuple<>(NPC_FACTION_CIVILIAN_FEMALE, "custom_2"))
			.put("custom_2.civilian.male", new Tuple<>(NPC_FACTION_CIVILIAN_MALE, "custom_2"))
			.put("custom_2.leader", new Tuple<>(NPC_FACTION_COMMANDER, "custom_2"))
			.put("custom_2.leader.elite", new Tuple<>(NPC_FACTION_LEADER_ELITE, "custom_2"))
			.put("custom_2.mounted_archer", new Tuple<>(NPC_FACTION_MOUNTED_ARCHER, "custom_2"))
			.put("custom_2.priest", new Tuple<>(NPC_FACTION_PRIEST, "custom_2"))
			.put("custom_2.soldier", new Tuple<>(NPC_FACTION_SOLDIER, "custom_2"))
			.put("custom_2.soldier.elite", new Tuple<>(NPC_FACTION_SOLDIER_ELITE, "custom_2"))
			.put("custom_2.trader", new Tuple<>(NPC_FACTION_TRADER, "custom_2"))
			.put("custom_3.archer", new Tuple<>(NPC_FACTION_ARCHER, "custom_3"))
			.put("custom_3.archer.elite", new Tuple<>(NPC_FACTION_ARCHER_ELITE, "custom_3"))
			.put("custom_3.bard", new Tuple<>(NPC_FACTION_BARD, "custom_3"))
			.put("custom_3.cavalry", new Tuple<>(NPC_FACTION_CAVALRY, "custom_3"))
			.put("custom_3.civilian.female", new Tuple<>(NPC_FACTION_CIVILIAN_FEMALE, "custom_3"))
			.put("custom_3.civilian.male", new Tuple<>(NPC_FACTION_CIVILIAN_MALE, "custom_3"))
			.put("custom_3.leader", new Tuple<>(NPC_FACTION_COMMANDER, "custom_3"))
			.put("custom_3.leader.elite", new Tuple<>(NPC_FACTION_LEADER_ELITE, "custom_3"))
			.put("custom_3.mounted_archer", new Tuple<>(NPC_FACTION_MOUNTED_ARCHER, "custom_3"))
			.put("custom_3.priest", new Tuple<>(NPC_FACTION_PRIEST, "custom_3"))
			.put("custom_3.soldier", new Tuple<>(NPC_FACTION_SOLDIER, "custom_3"))
			.put("custom_3.soldier.elite", new Tuple<>(NPC_FACTION_SOLDIER_ELITE, "custom_3"))
			.put("custom_3.trader", new Tuple<>(NPC_FACTION_TRADER, "custom_3"))
			.put("desert.archer", new Tuple<>(NPC_FACTION_ARCHER, "desert"))
			.put("desert.archer.elite", new Tuple<>(NPC_FACTION_ARCHER_ELITE, "desert"))
			.put("desert.bard", new Tuple<>(NPC_FACTION_BARD, "desert"))
			.put("desert.cavalry", new Tuple<>(NPC_FACTION_CAVALRY, "desert"))
			.put("desert.civilian.female", new Tuple<>(NPC_FACTION_CIVILIAN_FEMALE, "desert"))
			.put("desert.civilian.male", new Tuple<>(NPC_FACTION_CIVILIAN_MALE, "desert"))
			.put("desert.leader", new Tuple<>(NPC_FACTION_COMMANDER, "desert"))
			.put("desert.leader.elite", new Tuple<>(NPC_FACTION_LEADER_ELITE, "desert"))
			.put("desert.mounted_archer", new Tuple<>(NPC_FACTION_MOUNTED_ARCHER, "desert"))
			.put("desert.priest", new Tuple<>(NPC_FACTION_PRIEST, "desert"))
			.put("desert.soldier", new Tuple<>(NPC_FACTION_SOLDIER, "desert"))
			.put("desert.soldier.elite", new Tuple<>(NPC_FACTION_SOLDIER_ELITE, "desert"))
			.put("desert.trader", new Tuple<>(NPC_FACTION_TRADER, "desert"))
			.put("native.archer", new Tuple<>(NPC_FACTION_ARCHER, "native"))
			.put("native.archer.elite", new Tuple<>(NPC_FACTION_ARCHER_ELITE, "native"))
			.put("native.bard", new Tuple<>(NPC_FACTION_BARD, "native"))
			.put("native.cavalry", new Tuple<>(NPC_FACTION_CAVALRY, "native"))
			.put("native.civilian.female", new Tuple<>(NPC_FACTION_CIVILIAN_FEMALE, "native"))
			.put("native.civilian.male", new Tuple<>(NPC_FACTION_CIVILIAN_MALE, "native"))
			.put("native.leader", new Tuple<>(NPC_FACTION_COMMANDER, "native"))
			.put("native.leader.elite", new Tuple<>(NPC_FACTION_LEADER_ELITE, "native"))
			.put("native.mounted_archer", new Tuple<>(NPC_FACTION_MOUNTED_ARCHER, "native"))
			.put("native.priest", new Tuple<>(NPC_FACTION_PRIEST, "native"))
			.put("native.soldier", new Tuple<>(NPC_FACTION_SOLDIER, "native"))
			.put("native.soldier.elite", new Tuple<>(NPC_FACTION_SOLDIER_ELITE, "native"))
			.put("native.trader", new Tuple<>(NPC_FACTION_TRADER, "native"))
			.put("pirate.archer", new Tuple<>(NPC_FACTION_ARCHER, "pirate"))
			.put("pirate.archer.elite", new Tuple<>(NPC_FACTION_ARCHER_ELITE, "pirate"))
			.put("pirate.bard", new Tuple<>(NPC_FACTION_BARD, "pirate"))
			.put("pirate.cavalry", new Tuple<>(NPC_FACTION_CAVALRY, "pirate"))
			.put("pirate.civilian.female", new Tuple<>(NPC_FACTION_CIVILIAN_FEMALE, "pirate"))
			.put("pirate.civilian.male", new Tuple<>(NPC_FACTION_CIVILIAN_MALE, "pirate"))
			.put("pirate.leader", new Tuple<>(NPC_FACTION_COMMANDER, "pirate"))
			.put("pirate.leader.elite", new Tuple<>(NPC_FACTION_LEADER_ELITE, "pirate"))
			.put("pirate.mounted_archer", new Tuple<>(NPC_FACTION_MOUNTED_ARCHER, "pirate"))
			.put("pirate.priest", new Tuple<>(NPC_FACTION_PRIEST, "pirate"))
			.put("pirate.soldier", new Tuple<>(NPC_FACTION_SOLDIER, "pirate"))
			.put("pirate.soldier.elite", new Tuple<>(NPC_FACTION_SOLDIER_ELITE, "pirate"))
			.put("pirate.trader", new Tuple<>(NPC_FACTION_TRADER, "pirate"))
			.put("viking.archer", new Tuple<>(NPC_FACTION_ARCHER, "viking"))
			.put("viking.archer.elite", new Tuple<>(NPC_FACTION_ARCHER_ELITE, "viking"))
			.put("viking.bard", new Tuple<>(NPC_FACTION_BARD, "viking"))
			.put("viking.cavalry", new Tuple<>(NPC_FACTION_CAVALRY, "viking"))
			.put("viking.civilian.female", new Tuple<>(NPC_FACTION_CIVILIAN_FEMALE, "viking"))
			.put("viking.civilian.male", new Tuple<>(NPC_FACTION_CIVILIAN_MALE, "viking"))
			.put("viking.leader", new Tuple<>(NPC_FACTION_COMMANDER, "viking"))
			.put("viking.leader.elite", new Tuple<>(NPC_FACTION_LEADER_ELITE, "viking"))
			.put("viking.mounted_archer", new Tuple<>(NPC_FACTION_MOUNTED_ARCHER, "viking"))
			.put("viking.priest", new Tuple<>(NPC_FACTION_PRIEST, "viking"))
			.put("viking.soldier", new Tuple<>(NPC_FACTION_SOLDIER, "viking"))
			.put("viking.soldier.elite", new Tuple<>(NPC_FACTION_SOLDIER_ELITE, "viking"))
			.put("viking.trader", new Tuple<>(NPC_FACTION_TRADER, "viking"))
			.build();

	@Override
	public int getFixVersion() {
		return 3;
	}

	@Override
	public NBTTagCompound fixTagCompound(NBTTagCompound compound) {
		String id = compound.getString("id");

		//noinspection ConstantConditions
		if (id.equals(AWNPCItems.NPC_SPAWNER.getRegistryName().toString())) {
			NBTTagCompound tag = compound.getCompoundTag("tag");
			String npcType = tag.getString("npcType");
			if (factionItemFixes.containsKey(npcType)) {
				tag.setString("npcType", factionItemFixes.get(npcType).getFirst());
				tag.setString("faction", factionItemFixes.get(npcType).getSecond());
			}
		}
		return compound;
	}
}
