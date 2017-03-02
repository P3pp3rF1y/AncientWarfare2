package net.shadowmage.ancientwarfare.npc.gamedata;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;
import net.minecraftforge.common.util.Constants.NBT;
import net.shadowmage.ancientwarfare.core.gamedata.ChunkClaims;
import net.shadowmage.ancientwarfare.core.interop.ModAccessors;
import net.shadowmage.ancientwarfare.npc.block.AWNPCBlockLoader;
import net.shadowmage.ancientwarfare.npc.block.BlockHeadquarters;
import net.shadowmage.ancientwarfare.npc.block.BlockTownHall;
import net.shadowmage.ancientwarfare.npc.tile.TileTownHall;

public class HeadquartersTracker extends WorldSavedData {
    public static final String ID = "AW2_HeadquartersTracker";
    
    private static HashMap<Integer, Boolean> IS_STALE = new HashMap<Integer, Boolean>();
    private static HashMap<Integer, HeadquartersTracker> INSTANCES = new HashMap<Integer, HeadquartersTracker>();
    
    public static HeadquartersTracker get(World world) {
        int dimId = world.provider.dimensionId;
        Boolean isStale = IS_STALE.get(dimId);
        if (isStale == null || isStale) {
            // use per-dimension storage to let each dimension have it's own HQ
            HeadquartersTracker instance = (HeadquartersTracker) world.perWorldStorage.loadData(HeadquartersTracker.class, ID);
            if (instance == null) {
                instance = new HeadquartersTracker();
                world.setItemData(ID, instance);
            }
            INSTANCES.put(dimId, instance);
            IS_STALE.put(dimId, false);
        }
        return INSTANCES.get(dimId);
    }
    
    private Map<String, int[]> playerHeadquarters = new HashMap<String, int[]>();
    
    public HeadquartersTracker() {
        super(ID);
    }
    
    public HeadquartersTracker(String id) {
        super(id);
    }
    
    public synchronized boolean validateCurrentHq(String ownerName, World world) {
        int[] hqPos = playerHeadquarters.get(ownerName);
        if (hqPos == null || hqPos.length != 3) {
            return false;
        }
        boolean isValid = isBlockHq(ownerName, world, hqPos[0], hqPos[1], hqPos[2]);
        if (!isValid) {
            // HQ position was assigned but no longer valid
            playerHeadquarters.put(ownerName, new int[] {});
            markDirty();
        }
        return isValid;
    }
    
    public synchronized boolean isBlockHq(String ownerName, World world, int posX, int posY, int posZ) {
        Block block = world.getBlock(posX, posY, posZ);
        if (block instanceof BlockHeadquarters) {
            String townHallOwnerName = ((TileTownHall)world.getTileEntity(posX, posY, posZ)).getOwnerName();
            if (townHallOwnerName != null && !townHallOwnerName.isEmpty()) {
                // only confirm the owner if the block actually has the owner set (not the case when first placed)
                if (!townHallOwnerName.equals(ownerName)) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }
    
    public synchronized void setNewHq(String ownerName, World world, int posX, int posY, int posZ) {
        if (isBlockHq(ownerName, world, posX, posY, posZ)) {
            playerHeadquarters.put(ownerName, new int[] {posX, posY, posZ});
            markDirty();
        }
    }
    
    public static void notifyHqNew(String ownerName, int posX, int posZ) {
        String notificationTitle = "ftbu_aw2.notification.townhall_newhq";
        ChatComponentTranslation notificationMsg = new ChatComponentTranslation("ftbu_aw2.notification.townhall_newhq.msg");
        List<ChatComponentTranslation> notificationTooltip = new ArrayList<ChatComponentTranslation>();
        notificationTooltip.add(new ChatComponentTranslation("ftbu_aw2.notification.chunk_position", posX, posZ));
        notificationTooltip.add(new ChatComponentTranslation("ftbu_aw2.notification.click_to_remove"));
        ModAccessors.FTBU.notifyPlayer(EnumChatFormatting.BLUE, ownerName, notificationTitle, notificationMsg, notificationTooltip);
    }
    
    public static void notifyHqMissing(String ownerName) {
        String notificationTitle = "ftbu_aw2.notification.townhall_hqmissing";
        ChatComponentTranslation notificationMsg = new ChatComponentTranslation("ftbu_aw2.notification.townhall_hqmissing.msg");
        List<ChatComponentTranslation> notificationTooltip = new ArrayList<ChatComponentTranslation>();
        notificationTooltip.add(new ChatComponentTranslation("ftbu_aw2.notification.townhall_hqmissing.tooltip.1"));
        notificationTooltip.add(new ChatComponentTranslation("ftbu_aw2.notification.townhall_hqmissing.tooltip.2"));
        notificationTooltip.add(new ChatComponentTranslation("ftbu_aw2.notification.townhall_hqmissing.tooltip.3"));
        ModAccessors.FTBU.notifyPlayer(EnumChatFormatting.GOLD, ownerName, notificationTitle, notificationMsg, notificationTooltip);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        playerHeadquarters.clear();
        NBTTagList playerHeadquartersNbt = compound.getTagList("playerHeadquarters", NBT.TAG_COMPOUND);
        for (int i = 0; i < playerHeadquartersNbt.tagCount(); i++) {
            NBTTagCompound playerHeadquartersEntry = playerHeadquartersNbt.getCompoundTagAt(i);
            String ownerName = playerHeadquartersEntry.getString("ownerName");
            int[] hqPos = playerHeadquartersEntry.getIntArray("hqPos");
            playerHeadquarters.put(ownerName, hqPos);
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound compound) {
        NBTTagList playerHeadquartersTag = new NBTTagList();
        for(Entry<String, int[]> entry : playerHeadquarters.entrySet()) {
            NBTTagCompound playerHeadquartersEntry = new NBTTagCompound();
            playerHeadquartersEntry.setString("ownerName", entry.getKey());
            if (entry.getValue().length > 0)
                playerHeadquartersEntry.setIntArray("hqPos", entry.getValue());
            playerHeadquartersTag.appendTag(playerHeadquartersEntry);
        }
        compound.setTag("playerHeadquarters", playerHeadquartersTag);
    }
}
