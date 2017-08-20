package net.shadowmage.ancientwarfare.npc.gamedata;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.WorldSavedData;
import net.minecraftforge.common.util.Constants;
import net.shadowmage.ancientwarfare.npc.faction.FactionEntry;

import java.util.HashMap;

public class FactionData extends WorldSavedData {

    private HashMap<String, FactionEntry> playerFactionEntries = new HashMap<String, FactionEntry>();

    public FactionData(String par1Str) {
        super(par1Str);
    }

    public void onPlayerLogin(EntityPlayer player) {
        String name = player.getName();
        if (!playerFactionEntries.containsKey(name)) {
            playerFactionEntries.put(name, new FactionEntry(name));
            markDirty();
        }
    }

    public FactionEntry getEntryFor(String playerName) {
        return playerFactionEntries.get(playerName);
    }

    public int getStandingFor(String playerName, String faction) {
        if (playerFactionEntries.containsKey(playerName)) {
            return playerFactionEntries.get(playerName).getStandingFor(faction);
        }
        return 0;
    }

    public void adjustStandingFor(String playerName, String faction, int adjustment) {
        if (playerFactionEntries.containsKey(playerName)) {
            playerFactionEntries.get(playerName).adjustStandingFor(faction, adjustment);
            markDirty();
        }
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
        FactionEntry entry;
        for (int i = 0; i < entryList.tagCount(); i++) {
            entry = new FactionEntry(entryList.getCompoundTagAt(i));
            playerFactionEntries.put(entry.playerName, entry);
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        NBTTagList entryList = new NBTTagList();
        for (FactionEntry entry : this.playerFactionEntries.values()) {
            entryList.appendTag(entry.writeToNBT(new NBTTagCompound()));
        }
        tag.setTag("entryList", entryList);
    }

}
