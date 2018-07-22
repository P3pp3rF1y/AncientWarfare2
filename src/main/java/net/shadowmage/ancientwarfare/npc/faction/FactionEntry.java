package net.shadowmage.ancientwarfare.npc.faction;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;
import net.shadowmage.ancientwarfare.npc.AncientWarfareNPC;
import net.shadowmage.ancientwarfare.npc.registry.FactionRegistry;

import java.util.HashMap;
import java.util.Map;

public final class FactionEntry {

	public final String playerName;
	private HashMap<String, Integer> factionStandings = new HashMap<>();

	public FactionEntry(NBTTagCompound tag) {
		playerName = tag.getString("playerName");
		for (String name : FactionRegistry.getFactionNames()) {
			factionStandings.put(name, AncientWarfareNPC.statics.getPlayerDefaultStanding(name));
		}
		readFromNBT(tag);
	}

	public FactionEntry(String playerName) {
		this.playerName = playerName;
		for (String name : FactionRegistry.getFactionNames()) {
			factionStandings.put(name, AncientWarfareNPC.statics.getPlayerDefaultStanding(name));
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

	public final NBTTagCompound writeToNBT(NBTTagCompound tag) {
		tag.setString("playerName", playerName);
		NBTTagList entryList = new NBTTagList();
		NBTTagCompound entryTag;
		for (Map.Entry<String, Integer> entry : this.factionStandings.entrySet()) {
			entryTag = new NBTTagCompound();
			entryTag.setString("name", entry.getKey());
			entryTag.setInteger("standing", entry.getValue());
		}
		tag.setTag("entryList", entryList);
		return tag;
	}
}
