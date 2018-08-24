package net.shadowmage.ancientwarfare.npc.datafixes;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.datafix.IFixableData;
import net.shadowmage.ancientwarfare.npc.init.AWNPCItems;

import static net.shadowmage.ancientwarfare.npc.datafixes.FactionExpansionEntityFixer.RENAMES;

public class FactionExpansionItemFixer implements IFixableData {
	private static final String FACTION_TAG = "faction";

	@Override
	public int getFixVersion() {
		return 5;
	}

	@Override
	public NBTTagCompound fixTagCompound(NBTTagCompound compound) {
		String id = compound.getString("id");

		//noinspection ConstantConditions
		if (id.equals(AWNPCItems.NPC_SPAWNER.getRegistryName().toString())) {
			NBTTagCompound tag = compound.getCompoundTag("tag");
			if (tag.hasKey(FACTION_TAG) && RENAMES.containsKey(tag.getString(FACTION_TAG))) {
				tag.setString(FACTION_TAG, RENAMES.get(tag.getString(FACTION_TAG)));
			}
		}

		return compound;
	}
}
