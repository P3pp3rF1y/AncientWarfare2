package net.shadowmage.ancientwarfare.npc.faction;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;
import net.shadowmage.ancientwarfare.npc.registry.FactionRegistry;

import java.util.HashMap;

public final class FactionEntry {

	public final String playerName;
	private HashMap<String, FactionStanding> factionStandings = new HashMap<>();

	public FactionEntry(NBTTagCompound tag) {
		playerName = tag.getString("playerName");
		for (String name : FactionRegistry.getFactionNames()) {
			factionStandings.put(name, new FactionStanding(FactionRegistry.getFaction(name).getPlayerDefaultStanding()));
		}
		readFromNBT(tag);
	}

	public FactionEntry(String playerName) {
		this.playerName = playerName;
		for (String name : FactionRegistry.getFactionNames()) {
			factionStandings.put(name, new FactionStanding(FactionRegistry.getFaction(name).getPlayerDefaultStanding()));
		}
	}

	public int getStandingFor(String factionName) {
		if (factionStandings.containsKey(factionName)) {
			return factionStandings.get(factionName).standing;
		}
		return 0;
	}

	public void setStandingFor(String factionName, int standing) {
		if (!factionStandings.containsKey(factionName)) {
			factionStandings.put(factionName, new FactionStanding(standing));
		}
		factionStandings.get(factionName).standing = standing;
	}

	public void adjustStandingFor(String factionName, int adjustment) {
		if (factionStandings.containsKey(factionName)) {
			FactionStanding standing = factionStandings.get(factionName);
			standing.standing += adjustment;
		}
	}

	public final void readFromNBT(NBTTagCompound tag) {
		NBTTagList entryList = tag.getTagList("entryList", Constants.NBT.TAG_COMPOUND);
		NBTTagCompound entryTag;
		String name;
		for (int i = 0; i < entryList.tagCount(); i++) {
			entryTag = entryList.getCompoundTagAt(i);
			name = entryTag.getString("name");
			if (!factionStandings.containsKey(name)) {
				factionStandings.put(name, new FactionStanding(FactionRegistry.getFaction(name).getPlayerDefaultStanding()));
			}
			factionStandings.get(name).standing = entryTag.getInteger("standing");
		}
	}

	public final NBTTagCompound writeToNBT(NBTTagCompound tag) {
		tag.setString("playerName", playerName);
		NBTTagList entryList = new NBTTagList();
		NBTTagCompound entryTag;
		for (String name : this.factionStandings.keySet()) {
			entryTag = new NBTTagCompound();
			entryTag.setString("name", name);
			entryTag.setInteger("standing", this.factionStandings.get(name).standing);
		}
		tag.setTag("entryList", entryList);
		return tag;
	}

	private static class FactionStanding {
		int standing;

		private FactionStanding(int standing) {
			this.standing = standing;
		}
	}

}
