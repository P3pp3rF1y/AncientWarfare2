package net.shadowmage.ancientwarfare.npc.gamedata;

import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
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
    
    private Map<String, BlockPos> playerHeadquarters = new HashMap<>();
    private BlockPos teleportHubPosition = null;
    
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
    public BlockPos getHqPos(String ownerName, World world) {
        BlockPos hqPos = playerHeadquarters.get(ownerName);
        if (hqPos == null || !isBlockHq(ownerName, world, hqPos))
            return null;
        else
            return hqPos;
    }
    
    public synchronized boolean validateCurrentHq(String ownerName, World world) {
        BlockPos hqPos = getHqPos(ownerName, world);
        if (hqPos == null) {
            // HQ position was assigned but no longer valid
            playerHeadquarters.put(ownerName, null);
            markDirty();
            return false;
        } else
            return true;
    }
    
    public synchronized boolean isBlockHq(String ownerName, World world, BlockPos pos) {
        Block block = world.getBlockState(pos).getBlock();
        if (block instanceof BlockHeadquarters) {
            String townHallOwnerName = ((TileTownHall)world.getTileEntity(pos)).getOwnerName();
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
    
    public synchronized void setNewHq(String ownerName, World world, BlockPos pos) {
        if (isBlockHq(ownerName, world, pos)) {
            playerHeadquarters.put(ownerName, pos);
            markDirty();
        }
    }
    
    public static void notifyHqNew(String ownerName, BlockPos pos) {
        String notificationTitle = "ftbu_aw2.notification.townhall_newhq";
        TextComponentTranslation notificationMsg = new TextComponentTranslation("ftbu_aw2.notification.townhall_newhq.msg");
        List<TextComponentTranslation> notificationTooltip = new ArrayList<>();
        notificationTooltip.add(new TextComponentTranslation("ftbu_aw2.notification.chunk_position", pos.getX(), pos.getZ()));
        notificationTooltip.add(new TextComponentTranslation("ftbu_aw2.notification.townhall_newhq.tooltip.1"));
        notificationTooltip.add(new TextComponentTranslation("ftbu_aw2.notification.townhall_newhq.tooltip.2"));
        notificationTooltip.add(new TextComponentTranslation("ftbu_aw2.notification.townhall_newhq.tooltip.3"));
        notificationTooltip.add(new TextComponentTranslation("ftbu_aw2.notification.click_to_remove"));
        ModAccessors.FTBU.notifyPlayer(TextFormatting.BLUE, ownerName, notificationTitle, notificationMsg, notificationTooltip);
    }
    
    public static void notifyHqMissing(String ownerName) {
        String notificationTitle = "ftbu_aw2.notification.townhall_hqmissing";
        TextComponentTranslation notificationMsg = new TextComponentTranslation("ftbu_aw2.notification.townhall_hqmissing.msg");
        List<TextComponentTranslation> notificationTooltip = new ArrayList<>();
        notificationTooltip.add(new TextComponentTranslation("ftbu_aw2.notification.townhall_hqmissing.tooltip.1"));
        notificationTooltip.add(new TextComponentTranslation("ftbu_aw2.notification.townhall_hqmissing.tooltip.2"));
        notificationTooltip.add(new TextComponentTranslation("ftbu_aw2.notification.townhall_hqmissing.tooltip.3"));
        ModAccessors.FTBU.notifyPlayer(TextFormatting.GOLD, ownerName, notificationTitle, notificationMsg, notificationTooltip);
    }
    
    public BlockPos getTeleportHubPosition(World world) {
        if (teleportHubPosition != null) {
            if (!(world.getBlockState(teleportHubPosition).getBlock() instanceof BlockTeleportHub))
                teleportHubPosition = null;
        }
        return teleportHubPosition;
    }
    
    public void setTeleportHubPosition(BlockPos pos) {
        teleportHubPosition = pos;
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
