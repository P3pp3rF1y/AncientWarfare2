package net.shadowmage.ancientwarfare.npc.gamedata;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.util.Constants;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.npc.faction.PlayerFactionEntry;
import net.shadowmage.ancientwarfare.npc.network.PacketFactionUpdate;

import java.util.HashMap;

public class FactionData extends WorldSavedData {
	private static final String FACTION_INIT_TAG = "factionInit";
	private static final String FACTION_UPDATE_TAG = "factionUpdate";

	private HashMap<String, PlayerFactionEntry> playerFactionEntries = new HashMap<>();

	public FactionData(String par1Str) {
		super(par1Str);
	}

	public void onPlayerLogin(EntityPlayer player) {
		String name = player.getName();
		if (!playerFactionEntries.containsKey(name)) {
			playerFactionEntries.put(name, new PlayerFactionEntry(name));
			markDirty();
		}
		sendFactionEntry(player);
	}

	public PlayerFactionEntry getEntryFor(String playerName) {
		return playerFactionEntries.get(playerName);
	}

	public int getStandingFor(String playerName, String faction) {
		if (playerFactionEntries.containsKey(playerName)) {
			return playerFactionEntries.get(playerName).getStandingFor(faction);
		}
		return 0;
	}

	public void adjustStandingFor(World world, String playerName, String faction, int adjustment) {
		if (playerFactionEntries.containsKey(playerName)) {
			playerFactionEntries.get(playerName).adjustStandingFor(faction, adjustment);
			markDirty();
		}
		sendFactionUpdate(world, playerName, faction);
	}

	public void setStandingFor(String playerName, String faction, int setting) {
		if (playerFactionEntries.containsKey(playerName)) {
			playerFactionEntries.get(playerName).setStandingFor(faction, setting);
			markDirty();
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		NBTTagList entryList = tag.getTagList("entryList", Constants.NBT.TAG_COMPOUND);
		for (int i = 0; i < entryList.tagCount(); i++) {
			NBTTagCompound entryTag = entryList.getCompoundTagAt(i);
			readEntryFromNBT(entryTag);
		}
	}

	private void readEntryFromNBT(NBTTagCompound entryTag) {
		PlayerFactionEntry entry;
		entry = new PlayerFactionEntry(entryTag);
		playerFactionEntries.put(entry.playerName, entry);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		NBTTagList entryList = new NBTTagList();
		for (PlayerFactionEntry entry : this.playerFactionEntries.values()) {
			entryList.appendTag(entry.writeToNBT(new NBTTagCompound()));
		}
		tag.setTag("entryList", entryList);

		return tag;
	}

	public void handlePacketData(EntityPlayer player, NBTTagCompound tag) {
		if (tag.hasKey(FACTION_UPDATE_TAG)) {
			handleClientFactionUpdate(player, tag.getCompoundTag(FACTION_UPDATE_TAG));
		}
		if (tag.hasKey(FACTION_INIT_TAG)) {
			handleClientFactionInit(tag.getCompoundTag(FACTION_INIT_TAG));
		}
	}

	private void handleClientFactionUpdate(EntityPlayer player, NBTTagCompound tag) {
		String faction = tag.getString("faction");
		int standing = tag.getInteger("standing");
		setStandingFor(player.getName(), faction, standing);
	}

	private void handleClientFactionInit(NBTTagCompound tag) {
		readEntryFromNBT(tag);
	}

	private void sendFactionEntry(EntityPlayer player) {
		PlayerFactionEntry entry = getEntryFor(player.getName());
		NBTTagCompound tag = new NBTTagCompound();
		NBTTagCompound initTag = entry.writeToNBT(new NBTTagCompound());
		tag.setTag(FACTION_INIT_TAG, initTag);
		PacketFactionUpdate pkt = new PacketFactionUpdate(tag);
		NetworkHandler.sendToPlayer((EntityPlayerMP) player, pkt);
	}

	private void sendFactionUpdate(World world, String playerName, String factionName) {
		EntityPlayer player = world.getPlayerEntityByName(playerName);
		if (player instanceof EntityPlayerMP) {
			int standing = getStandingFor(playerName, factionName);
			NBTTagCompound tag = new NBTTagCompound();
			NBTTagCompound updateTag = new NBTTagCompound();
			updateTag.setString("faction", factionName);
			updateTag.setInteger("standing", standing);
			tag.setTag(FACTION_UPDATE_TAG, updateTag);
			PacketFactionUpdate pkt = new PacketFactionUpdate(tag);
			NetworkHandler.sendToPlayer((EntityPlayerMP) player, pkt);
		}
	}
}
