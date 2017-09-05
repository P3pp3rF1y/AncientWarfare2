package net.shadowmage.ancientwarfare.npc.gamedata;

import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;
import net.minecraftforge.common.util.Constants.NBT;
import net.shadowmage.ancientwarfare.core.interop.ModAccessors;
import net.shadowmage.ancientwarfare.npc.block.BlockHeadquarters;
import net.shadowmage.ancientwarfare.npc.block.BlockTeleportHub;
import net.shadowmage.ancientwarfare.npc.tile.TileTownHall;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class HeadquartersTracker extends WorldSavedData {
    public static final String ID = "AW2_HeadquartersTracker";
    
    public static HeadquartersTracker get(World world) {
        HeadquartersTracker hqTracker = (HeadquartersTracker) world.perWorldStorage.loadData(HeadquartersTracker.class, ID);
        if (hqTracker == null) {
            hqTracker = new HeadquartersTracker();
            world.perWorldStorage.setData(ID, hqTracker);
        }
        return hqTracker;
    }
    
    private Map<String, int[]> playerHeadquarters = new HashMap<>();
    private int[] teleportHubPosition = null;
    
    public HeadquartersTracker() {
        super(ID);
    }
    
    public HeadquartersTracker(String id) {
        super(id);
    }
    
    /*
     * 
     * @return An int array of x/y/z co-ords of the owner's HQ position, or null if it doesn't exist 
     */
    public int[] getHqPos(String ownerName, World world) {
        int[] hqPos = playerHeadquarters.get(ownerName);
        if (hqPos == null || hqPos.length != 3 || !isBlockHq(ownerName, world, hqPos[0], hqPos[1], hqPos[2]))
            return null;
        else
            return hqPos;
    }
    
    public synchronized boolean validateCurrentHq(String ownerName, World world) {
        int[] hqPos = getHqPos(ownerName, world);
        if (hqPos == null) {
            // HQ position was assigned but no longer valid
            playerHeadquarters.put(ownerName, new int[] {});
            markDirty();
            return false;
        } else
            return true;
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
        TextComponentTranslation notificationMsg = new TextComponentTranslation("ftbu_aw2.notification.townhall_newhq.msg");
        List<TextComponentTranslation> notificationTooltip = new ArrayList<>();
        notificationTooltip.add(new TextComponentTranslation("ftbu_aw2.notification.chunk_position", posX, posZ));
        notificationTooltip.add(new TextComponentTranslation("ftbu_aw2.notification.townhall_newhq.tooltip.1"));
        notificationTooltip.add(new TextComponentTranslation("ftbu_aw2.notification.townhall_newhq.tooltip.2"));
        notificationTooltip.add(new TextComponentTranslation("ftbu_aw2.notification.townhall_newhq.tooltip.3"));
        notificationTooltip.add(new TextComponentTranslation("ftbu_aw2.notification.click_to_remove"));
        ModAccessors.FTBU.notifyPlayer(EnumChatFormatting.BLUE, ownerName, notificationTitle, notificationMsg, notificationTooltip);
    }
    
    public static void notifyHqMissing(String ownerName) {
        String notificationTitle = "ftbu_aw2.notification.townhall_hqmissing";
        TextComponentTranslation notificationMsg = new TextComponentTranslation("ftbu_aw2.notification.townhall_hqmissing.msg");
        List<TextComponentTranslation> notificationTooltip = new ArrayList<>();
        notificationTooltip.add(new TextComponentTranslation("ftbu_aw2.notification.townhall_hqmissing.tooltip.1"));
        notificationTooltip.add(new TextComponentTranslation("ftbu_aw2.notification.townhall_hqmissing.tooltip.2"));
        notificationTooltip.add(new TextComponentTranslation("ftbu_aw2.notification.townhall_hqmissing.tooltip.3"));
        ModAccessors.FTBU.notifyPlayer(EnumChatFormatting.GOLD, ownerName, notificationTitle, notificationMsg, notificationTooltip);
    }
    
    public int[] getTeleportHubPosition(World world) {
        if (teleportHubPosition != null) {
            if (!(world.getBlock(teleportHubPosition[0], teleportHubPosition[1], teleportHubPosition[2]) instanceof BlockTeleportHub))
                teleportHubPosition = null;
        }
        return teleportHubPosition;
    }
    
    public void setTeleportHubPosition(int posX, int posY, int posZ) {
        teleportHubPosition = new int[]{posX, posY, posZ};
        markDirty();
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
        if (compound.hasKey("teleportHubPosition")) {
            int[] tpHub = compound.getIntArray("teleportHubPosition");
            setTeleportHubPosition(tpHub[0], tpHub[1], tpHub[2]);
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
        if (teleportHubPosition != null && teleportHubPosition.length == 3)
            compound.setIntArray("teleportHubPosition", teleportHubPosition);
    }
}
