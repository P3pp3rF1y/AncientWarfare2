package net.shadowmage.ancientwarfare.npc.datafixes;

import com.google.common.collect.ImmutableMap;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.Tuple;
import net.minecraft.util.datafix.IFixableData;
import net.shadowmage.ancientwarfare.npc.item.AWNPCItems;

import java.util.Map;

public class FactionSpawnerItemFixer implements IFixableData {
	private Map<String, Tuple<String, String>> factionItemFixes = new ImmutableMap.Builder<String, Tuple<String, String>>()
			.put("bandit.archer", new Tuple<>("archer", "bandit"))
			.put("bandit.archer.elite", new Tuple<>("archer.elite", "bandit"))
			.put("bandit.bard", new Tuple<>("bard", "bandit"))
			.put("bandit.cavalry", new Tuple<>("cavalry", "bandit"))
			.put("bandit.civilian.female", new Tuple<>("civilian.female", "bandit"))
			.put("bandit.civilian.male", new Tuple<>("civilian.male", "bandit"))
			.put("bandit.leader", new Tuple<>("leader", "bandit"))
			.put("bandit.leader.elite", new Tuple<>("leader.elite", "bandit"))
			.put("bandit.mounted_archer", new Tuple<>("mounted_archer", "bandit"))
			.put("bandit.priest", new Tuple<>("priest", "bandit"))
			.put("bandit.soldier", new Tuple<>("soldier", "bandit"))
			.put("bandit.soldier.elite", new Tuple<>("soldier.elite", "bandit"))
			.put("bandit.trader", new Tuple<>("trader", "bandit"))
			.put("custom_1.archer", new Tuple<>("archer", "custom_1"))
			.put("custom_1.archer.elite", new Tuple<>("archer.elite", "custom_1"))
			.put("custom_1.bard", new Tuple<>("bard", "custom_1"))
			.put("custom_1.cavalry", new Tuple<>("cavalry", "custom_1"))
			.put("custom_1.civilian.female", new Tuple<>("civilian.female", "custom_1"))
			.put("custom_1.civilian.male", new Tuple<>("civilian.male", "custom_1"))
			.put("custom_1.leader", new Tuple<>("leader", "custom_1"))
			.put("custom_1.leader.elite", new Tuple<>("leader.elite", "custom_1"))
			.put("custom_1.mounted_archer", new Tuple<>("mounted_archer", "custom_1"))
			.put("custom_1.priest", new Tuple<>("priest", "custom_1"))
			.put("custom_1.soldier", new Tuple<>("soldier", "custom_1"))
			.put("custom_1.soldier.elite", new Tuple<>("soldier.elite", "custom_1"))
			.put("custom_1.trader", new Tuple<>("trader", "custom_1"))
			.put("custom_2.archer", new Tuple<>("archer", "custom_2"))
			.put("custom_2.archer.elite", new Tuple<>("archer.elite", "custom_2"))
			.put("custom_2.bard", new Tuple<>("bard", "custom_2"))
			.put("custom_2.cavalry", new Tuple<>("cavalry", "custom_2"))
			.put("custom_2.civilian.female", new Tuple<>("civilian.female", "custom_2"))
			.put("custom_2.civilian.male", new Tuple<>("civilian.male", "custom_2"))
			.put("custom_2.leader", new Tuple<>("leader", "custom_2"))
			.put("custom_2.leader.elite", new Tuple<>("leader.elite", "custom_2"))
			.put("custom_2.mounted_archer", new Tuple<>("mounted_archer", "custom_2"))
			.put("custom_2.priest", new Tuple<>("priest", "custom_2"))
			.put("custom_2.soldier", new Tuple<>("soldier", "custom_2"))
			.put("custom_2.soldier.elite", new Tuple<>("soldier.elite", "custom_2"))
			.put("custom_2.trader", new Tuple<>("trader", "custom_2"))
			.put("custom_3.archer", new Tuple<>("archer", "custom_3"))
			.put("custom_3.archer.elite", new Tuple<>("archer.elite", "custom_3"))
			.put("custom_3.bard", new Tuple<>("bard", "custom_3"))
			.put("custom_3.cavalry", new Tuple<>("cavalry", "custom_3"))
			.put("custom_3.civilian.female", new Tuple<>("civilian.female", "custom_3"))
			.put("custom_3.civilian.male", new Tuple<>("civilian.male", "custom_3"))
			.put("custom_3.leader", new Tuple<>("leader", "custom_3"))
			.put("custom_3.leader.elite", new Tuple<>("leader.elite", "custom_3"))
			.put("custom_3.mounted_archer", new Tuple<>("mounted_archer", "custom_3"))
			.put("custom_3.priest", new Tuple<>("priest", "custom_3"))
			.put("custom_3.soldier", new Tuple<>("soldier", "custom_3"))
			.put("custom_3.soldier.elite", new Tuple<>("soldier.elite", "custom_3"))
			.put("custom_3.trader", new Tuple<>("trader", "custom_3"))
			.put("desert.archer", new Tuple<>("archer", "desert"))
			.put("desert.archer.elite", new Tuple<>("archer.elite", "desert"))
			.put("desert.bard", new Tuple<>("bard", "desert"))
			.put("desert.cavalry", new Tuple<>("cavalry", "desert"))
			.put("desert.civilian.female", new Tuple<>("civilian.female", "desert"))
			.put("desert.civilian.male", new Tuple<>("civilian.male", "desert"))
			.put("desert.leader", new Tuple<>("leader", "desert"))
			.put("desert.leader.elite", new Tuple<>("leader.elite", "desert"))
			.put("desert.mounted_archer", new Tuple<>("mounted_archer", "desert"))
			.put("desert.priest", new Tuple<>("priest", "desert"))
			.put("desert.soldier", new Tuple<>("soldier", "desert"))
			.put("desert.soldier.elite", new Tuple<>("soldier.elite", "desert"))
			.put("desert.trader", new Tuple<>("trader", "desert"))
			.put("native.archer", new Tuple<>("archer", "native"))
			.put("native.archer.elite", new Tuple<>("archer.elite", "native"))
			.put("native.bard", new Tuple<>("bard", "native"))
			.put("native.cavalry", new Tuple<>("cavalry", "native"))
			.put("native.civilian.female", new Tuple<>("civilian.female", "native"))
			.put("native.civilian.male", new Tuple<>("civilian.male", "native"))
			.put("native.leader", new Tuple<>("leader", "native"))
			.put("native.leader.elite", new Tuple<>("leader.elite", "native"))
			.put("native.mounted_archer", new Tuple<>("mounted_archer", "native"))
			.put("native.priest", new Tuple<>("priest", "native"))
			.put("native.soldier", new Tuple<>("soldier", "native"))
			.put("native.soldier.elite", new Tuple<>("soldier.elite", "native"))
			.put("native.trader", new Tuple<>("trader", "native"))
			.put("pirate.archer", new Tuple<>("archer", "pirate"))
			.put("pirate.archer.elite", new Tuple<>("archer.elite", "pirate"))
			.put("pirate.bard", new Tuple<>("bard", "pirate"))
			.put("pirate.cavalry", new Tuple<>("cavalry", "pirate"))
			.put("pirate.civilian.female", new Tuple<>("civilian.female", "pirate"))
			.put("pirate.civilian.male", new Tuple<>("civilian.male", "pirate"))
			.put("pirate.leader", new Tuple<>("leader", "pirate"))
			.put("pirate.leader.elite", new Tuple<>("leader.elite", "pirate"))
			.put("pirate.mounted_archer", new Tuple<>("mounted_archer", "pirate"))
			.put("pirate.priest", new Tuple<>("priest", "pirate"))
			.put("pirate.soldier", new Tuple<>("soldier", "pirate"))
			.put("pirate.soldier.elite", new Tuple<>("soldier.elite", "pirate"))
			.put("pirate.trader", new Tuple<>("trader", "pirate"))
			.put("viking.archer", new Tuple<>("archer", "viking"))
			.put("viking.archer.elite", new Tuple<>("archer.elite", "viking"))
			.put("viking.bard", new Tuple<>("bard", "viking"))
			.put("viking.cavalry", new Tuple<>("cavalry", "viking"))
			.put("viking.civilian.female", new Tuple<>("civilian.female", "viking"))
			.put("viking.civilian.male", new Tuple<>("civilian.male", "viking"))
			.put("viking.leader", new Tuple<>("leader", "viking"))
			.put("viking.leader.elite", new Tuple<>("leader.elite", "viking"))
			.put("viking.mounted_archer", new Tuple<>("mounted_archer", "viking"))
			.put("viking.priest", new Tuple<>("priest", "viking"))
			.put("viking.soldier", new Tuple<>("soldier", "viking"))
			.put("viking.soldier.elite", new Tuple<>("soldier.elite", "viking"))
			.put("viking.trader", new Tuple<>("trader", "viking"))
			.build();

	@Override
	public int getFixVersion() {
		return 3;
	}

	@Override
	public NBTTagCompound fixTagCompound(NBTTagCompound compound) {
		String id = compound.getString("id");

		//noinspection ConstantConditions
		if (id.equals(AWNPCItems.npcSpawner.getRegistryName().toString())) {
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
