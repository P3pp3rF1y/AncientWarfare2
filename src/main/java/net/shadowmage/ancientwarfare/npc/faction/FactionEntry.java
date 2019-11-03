package net.shadowmage.ancientwarfare.npc.faction;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;
import net.shadowmage.ancientwarfare.npc.AncientWarfareNPC;
import net.shadowmage.ancientwarfare.npc.registry.FactionRegistry;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class FactionEntry implements Iterable<Map.Entry<String, Integer>> {
	private HashMap<String, Integer> factionStandings = new HashMap<>();

	public FactionEntry(NBTTagCompound tag) {
		this();
		readFromNBT(tag);
	}

	public FactionEntry() {
		for (String name : FactionRegistry.getFactionNames()) {
			setStandingFor(name, AncientWarfareNPC.statics.getPlayerDefaultStanding(name));
		}
	}


	public int getStandingFor(String factionName) {
		if (factionStandings.containsKey(factionName)) {
			return factionStandings.get(factionName);
		}
		return 0;
	}

	public void setStandingFor(String factionName, int standing) {
		factionStandings.put(factionName, standing);
	}

	public void adjustStandingFor(String factionName, int adjustment) {
		if (factionStandings.containsKey(factionName)) {
			setStandingFor(factionName, getStandingFor(factionName) + adjustment);
		}
	}

	public final void readFromNBT(NBTTagCompound tag) {
		NBTTagList entryList = tag.getTagList("entryList", Constants.NBT.TAG_COMPOUND);
		NBTTagCompound entryTag;
		String name;
		for (int i = 0; i < entryList.tagCount(); i++) {
			entryTag = entryList.getCompoundTagAt(i);
			name = entryTag.getString("name");
			setStandingFor(name, entryTag.getInteger("standing"));
		}
	}

	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		NBTTagList entryList = new NBTTagList();
		for (Map.Entry<String, Integer> entry : this.factionStandings.entrySet()) {
			NBTTagCompound entryTag = new NBTTagCompound();
			entryTag.setString("name", entry.getKey());
			entryTag.setInteger("standing", entry.getValue());
			entryList.appendTag(entryTag);
		}
		tag.setTag("entryList", entryList);
		return tag;
	}

	@Override
	public Iterator<Map.Entry<String, Integer>> iterator() {
		return factionStandings.entrySet().iterator();
	}
}
