package net.shadowmage.ancientwarfare.npc.faction;

import net.minecraft.nbt.NBTTagCompound;

public final class PlayerFactionEntry extends FactionEntry {
	public final String playerName;

	public PlayerFactionEntry(NBTTagCompound tag) {
		super(tag);
		playerName = tag.getString("playerName");
	}

	public PlayerFactionEntry(String playerName) {
		super();
		this.playerName = playerName;
	}

	@Override
	public final NBTTagCompound writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		tag.setString("playerName", playerName);
		return tag;
	}
}
