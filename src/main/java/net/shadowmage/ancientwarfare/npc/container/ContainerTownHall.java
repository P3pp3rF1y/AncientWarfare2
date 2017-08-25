package net.shadowmage.ancientwarfare.npc.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.Constants;
import net.shadowmage.ancientwarfare.core.container.ContainerTileBase;
import net.shadowmage.ancientwarfare.core.util.EntityTools;
import net.shadowmage.ancientwarfare.npc.gamedata.HeadquartersTracker;
import net.shadowmage.ancientwarfare.npc.tile.TileTeleportHub;
import net.shadowmage.ancientwarfare.npc.tile.TileTownHall;
import net.shadowmage.ancientwarfare.npc.tile.TileTownHall.NpcDeathEntry;

import java.util.ArrayList;
import java.util.List;

public class ContainerTownHall extends ContainerTileBase<TileTownHall> {

    List<NpcDeathEntry> deathList = new ArrayList<NpcDeathEntry>();
    public ContainerTownHall(EntityPlayer player, int x, int y, int z) {
        super(player, x, y, z);
        int xPos, yPos;
        for (int i = 0; i < tileEntity.getSizeInventory(); i++) {
            xPos = (i % 9) * 18 + 8;
            yPos = (i / 9) * 18 + 8 + 16;
            addSlotToContainer(new Slot(tileEntity, i, xPos, yPos));
        }
        addPlayerSlots(8 + 3 * 18 + 8 + 16);
        if (!player.world.isRemote) {
            deathList.addAll(tileEntity.getDeathList());
            tileEntity.addViewer(this);
        }
    }

    @Override
    public void handlePacketData(NBTTagCompound tag) {
        if (tag.hasKey("playerName")) {
            if (!tileEntity.getWorld().isRemote) {
                int[] tpHubPos = HeadquartersTracker.get(tileEntity.getWorld()).getTeleportHubPosition(tileEntity.getWorld());
                if (tpHubPos != null) {
                    TileEntity te = tileEntity.getWorld().getTileEntity(tpHubPos[0], tpHubPos[1], tpHubPos[2]);
                    if (te instanceof TileTeleportHub) {
                        String playerName = tag.getString("playerName");
                        List<EntityPlayerMP> playerList = MinecraftServer.getServer().getConfigurationManager().playerEntityList;
                        for (EntityPlayerMP entityPlayer : playerList) {
                            if (entityPlayer.getName().equals(playerName)) {
                                final float randomPitch = (float) (Math.random() * (1.1f - 0.9f) + 0.9f);
                                tileEntity.getWorld().playSoundAtEntity(entityPlayer, "ancientwarfare:teleport.out", 0.6F, randomPitch);
                                ((TileTeleportHub) te).addArrival(playerName);
                                EntityTools.teleportPlayerToBlock(entityPlayer, entityPlayer.world, tpHubPos, false);
                                entityPlayer.world.playSoundAtEntity(entityPlayer, "ancientwarfare:teleport.in", 0.6F, randomPitch);
                            }
                        }
                    }
                }
            }
            // we don't need to do anything else
            return;
        }
        if (tag.hasKey("deathList")) {
            deathList.clear();
            NBTTagList list = tag.getTagList("deathList", Constants.NBT.TAG_COMPOUND);
            for (int i = 0; i < list.tagCount(); i++) {
                deathList.add(new NpcDeathEntry(list.getCompoundTagAt(i)));
            }
            refreshGui();
        } else if (tag.hasKey("clear")) {
            tileEntity.clearDeathNotices();
        }
        
        if (tag.hasKey("isHq")) {
            tileEntity.isHq = tag.getBoolean("isHq");
            refreshGui();
        }
        
        if (tag.hasKey("tpHubPos")) {
            tileEntity.tpHubPos = tag.getIntArray("tpHubPos");
        }
        
        if (tag.hasKey("range")) {
            tileEntity.setRange(tag.getInteger("range"));
            refreshGui();
        }
        
        if (tag.hasKey("name")) {
            tileEntity.name = tag.getString("name");
            refreshGui();
        }
        
        if (!tileEntity.getWorld().isRemote){
            tileEntity.markDirty();
        }
    }

    @Override
    public void sendInitData() {
        sendHqDataToClient();
        sendTownHallDataToClient(false);
    }

    @Override
    public void onContainerClosed(EntityPlayer par1EntityPlayer) {
        super.onContainerClosed(par1EntityPlayer);
        tileEntity.removeViewer(this);
    }
    
    public void onTownHallDeathListUpdated() {
        this.deathList.clear();
        this.deathList.addAll(tileEntity.getDeathList());
        sendTownHallDataToClient(true);
    }

    public void setRange(int value){
        tileEntity.setRange(value);
        NBTTagCompound tag = new NBTTagCompound();
        tag.setInteger("range", value);
        sendDataToServer(tag);
    }
    
    public void setName(String name){
        tileEntity.name = name;
        NBTTagCompound tag = new NBTTagCompound();
        tag.setString("name", name);
        sendDataToServer(tag);
    }
    
    public void teleportPlayer(String playerName) {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setString("playerName", playerName);
        sendDataToServer(tag);
    }

    public void clearList(){
        NBTTagCompound tag = new NBTTagCompound();
        tag.setBoolean("clear", true);
        sendDataToServer(tag);
    }
    
    private void sendHqDataToClient() {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setBoolean("isHq", tileEntity.isHq);
        int[] tpHubPos = HeadquartersTracker.get(tileEntity.getWorld()).getTeleportHubPosition(tileEntity.getWorld());
        if (tpHubPos != null)
            tag.setIntArray("tpHubPos", tpHubPos);
        sendDataToClient(tag);
    }

    private void sendTownHallDataToClient(boolean onlyDeathList) {
        NBTTagList list = new NBTTagList();
        for (NpcDeathEntry entry : deathList) {
            list.appendTag(entry.writeToNBT(new NBTTagCompound()));
        }
        NBTTagCompound tag = new NBTTagCompound();
        tag.setTag("deathList", list);
        if (!onlyDeathList) {
            tag.setInteger("range", tileEntity.getRange());
            tag.setString("name", tileEntity.name);
        }
        sendDataToClient(tag);
    }

    public List<NpcDeathEntry> getDeathList() {
        return deathList;
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer par1EntityPlayer, int slotClickedIndex) {
        @Nonnull ItemStack slotStackCopy = ItemStack.EMPTY;
        Slot theSlot = this.getSlot(slotClickedIndex);
        if (theSlot != null && theSlot.getHasStack()) {
            @Nonnull ItemStack slotStack = theSlot.getStack();
            slotStackCopy = slotStack.copy();
            if (slotClickedIndex < tileEntity.getSizeInventory())//book slot
            {
                if (!this.mergeItemStack(slotStack, tileEntity.getSizeInventory(), tileEntity.getSizeInventory() + playerSlots, false))//merge into player inventory
                {
                    return null;
                }
            } else {
                if (!this.mergeItemStack(slotStack, 0, tileEntity.getSizeInventory(), false))//merge into player inventory
                {
                    return null;
                }
            }
            if (slotStack.getCount() == 0) {
                theSlot.putStack(ItemStack.EMPTY);
            } else {
                theSlot.onSlotChanged();
            }
            if (slotStack.getCount() == slotStackCopy.getCount()) {
                return null;
            }
            theSlot.onPickupFromSlot(par1EntityPlayer, slotStack);
        }
        return slotStackCopy;
    }

}
