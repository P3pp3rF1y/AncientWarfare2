package net.shadowmage.ancientwarfare.npc.tile;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.ForgeChunkManager.Type;
import net.minecraftforge.common.util.Constants;
import net.shadowmage.ancientwarfare.core.interfaces.IInteractableTile;
import net.shadowmage.ancientwarfare.core.interop.ModAccessors;
import net.shadowmage.ancientwarfare.core.inventory.InventoryBasic;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.tile.TileOwned;
import net.shadowmage.ancientwarfare.core.util.BlockPosition;
import net.shadowmage.ancientwarfare.core.util.InventoryTools;
import net.shadowmage.ancientwarfare.npc.AncientWarfareNPC;
import net.shadowmage.ancientwarfare.npc.config.AWNPCStatics;
import net.shadowmage.ancientwarfare.npc.container.ContainerTownHall;
import net.shadowmage.ancientwarfare.npc.entity.NpcPlayerOwned;
import net.shadowmage.ancientwarfare.npc.item.ItemNpcSpawner;

import java.util.ArrayList;
import java.util.List;

public class TileTownHall extends TileOwned implements IInventory, IInteractableTile {

    public boolean alarmActive = false;
    
    private int broadcastRange = 80;
    private int updateDelayTicks = 0;

    private List<NpcDeathEntry> deathNotices = new ArrayList<TileTownHall.NpcDeathEntry>();

    private final InventoryBasic inventory = new InventoryBasic(27);

    private List<ContainerTownHall> viewers = new ArrayList<ContainerTownHall>();
    
    private ForgeChunkManager.Ticket ticket;

    public TileTownHall(){
        super("owner");
    }

    @Override
    public void updateEntity() {
        if (worldObj.isRemote)
            return;
        updateDelayTicks--;
        if (updateDelayTicks <= 0) {
            broadcast();
            updateDelayTicks = AWNPCStatics.townUpdateFreq;
            if (AWNPCStatics.townChunkLoadRadius > -1)
                loadChunks();
        }
    }

    private void loadChunks() {
        while(ticket == null)
            ticket = ForgeChunkManager.requestTicket(AncientWarfareNPC.instance, worldObj, Type.NORMAL);
        if (ticket==null)
            return; // no permision?
            
        ticket.getModData().setInteger("blockX", xCoord);
        ticket.getModData().setInteger("blockY", yCoord);
        ticket.getModData().setInteger("blockZ", zCoord);
       
        for (int chunkX = (xCoord>>4) - AWNPCStatics.townChunkLoadRadius; chunkX <= (xCoord>>4) + AWNPCStatics.townChunkLoadRadius; chunkX++)
            for (int chunkZ = (zCoord>>4) - AWNPCStatics.townChunkLoadRadius; chunkZ <= (zCoord>>4) + AWNPCStatics.townChunkLoadRadius; chunkZ++)
                ForgeChunkManager.forceChunk(ticket, new ChunkCoordIntPair(chunkX, chunkZ));
    }
    
    public void unloadChunks() {
        for (int chunkX = (xCoord>>4) - AWNPCStatics.townChunkLoadRadius; chunkX <= (xCoord>>4) + AWNPCStatics.townChunkLoadRadius; chunkX++)
            for (int chunkZ = (zCoord>>4) - AWNPCStatics.townChunkLoadRadius; chunkZ <= (zCoord>>4) + AWNPCStatics.townChunkLoadRadius; chunkZ++)
                ForgeChunkManager.unforceChunk(ticket, new ChunkCoordIntPair(chunkX, chunkZ));
    }
    
    public void loadTicket(ForgeChunkManager.Ticket ticket) {
        if (this.ticket == null)
               this.ticket = ticket;
        loadChunks();
    }
    
    public void addViewer(ContainerTownHall viewer) {
        if (!viewers.contains(viewer)) {
            viewers.add(viewer);
        }
    }

    public void removeViewer(ContainerTownHall viewer) {
        while (viewers.contains(viewer)) {
            viewers.remove(viewer);
        }
    }

    private void broadcast() {
        List<NpcPlayerOwned> npcs = getNpcsInArea();
        BlockPosition pos = new BlockPosition(xCoord, yCoord, zCoord);
        for (NpcPlayerOwned npc : npcs) {
            if (npc.hasCommandPermissions(getOwnerName())) {
                npc.handleTownHallBroadcast(this, pos);
            }
        }
    }

    public void clearDeathNotices() {
        deathNotices.clear();
        informViewers();
    }

    public void informViewers() {
        for (ContainerTownHall cth : viewers) {
            cth.onTownHallDeathListUpdated();
        }
    }

    public void handleNpcDeath(NpcPlayerOwned npc, DamageSource source) {
        boolean canRes = true;//TODO set canRes  based on distance from town-hall?
        NpcDeathEntry entry = new NpcDeathEntry(npc, source, canRes);
        deathNotices.add(entry);
        informViewers();
    }

    @SuppressWarnings("unchecked")
    private List<NpcPlayerOwned> getNpcsInArea() {
        AxisAlignedBB bb = AxisAlignedBB.getBoundingBox(xCoord - broadcastRange, yCoord - broadcastRange / 2, zCoord - broadcastRange, xCoord + broadcastRange + 1, yCoord + broadcastRange / 2 + 1, zCoord + broadcastRange + 1);
        return worldObj.getEntitiesWithinAABB(NpcPlayerOwned.class, bb);
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        inventory.readFromNBT(tag.getCompoundTag("inventory"));
        NBTTagList entryList = tag.getTagList("deathNotices", Constants.NBT.TAG_COMPOUND);
        NpcDeathEntry entry;
        for (int i = 0; i < entryList.tagCount(); i++) {
            entry = new NpcDeathEntry(entryList.getCompoundTagAt(i));
            deathNotices.add(entry);
        }
        if(tag.hasKey("range")){
            setRange(tag.getInteger("range"));
        }
        if (tag.hasKey("alarmActive"))
            alarmActive = (tag.getBoolean("alarmActive"));
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        tag.setTag("inventory", inventory.writeToNBT(new NBTTagCompound()));
        NBTTagList entryList = new NBTTagList();
        for (NpcDeathEntry entry : deathNotices) {
            entryList.appendTag(entry.writeToNBT(new NBTTagCompound()));
        }
        tag.setTag("deathNotices", entryList);
        tag.setInteger("range", broadcastRange);
        tag.setBoolean("alarmActive", alarmActive);
        
    }

    @Override
    public int getSizeInventory() {
        return inventory.getSizeInventory();
    }

    @Override
    public ItemStack getStackInSlot(int var1) {
        return inventory.getStackInSlot(var1);
    }

    @Override
    public ItemStack decrStackSize(int var1, int var2) {
        ItemStack stack = inventory.decrStackSize(var1, var2);
        if(stack!=null)
            markDirty();
        return stack;
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int var1) {
        return inventory.getStackInSlotOnClosing(var1);
    }

    @Override
    public void setInventorySlotContents(int var1, ItemStack var2) {
        inventory.setInventorySlotContents(var1, var2);
        markDirty();
    }

    @Override
    public String getInventoryName() {
        return inventory.getInventoryName();
    }

    @Override
    public boolean hasCustomInventoryName() {
        return inventory.hasCustomInventoryName();
    }

    @Override
    public int getInventoryStackLimit() {
        return inventory.getInventoryStackLimit();
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer var1) {
        return true;
    }

    @Override
    public void openInventory() {
    }

    @Override
    public void closeInventory() {
    }

    @Override
    public boolean isItemValidForSlot(int var1, ItemStack var2) {
        return inventory.isItemValidForSlot(var1, var2);
    }

    public static class NpcDeathEntry {
        public ItemStack stackToSpawn;
        public String npcType;
        public String npcName;
        public String deathCause;
        public boolean resurrected;
        public boolean canRes;
        public boolean beingResurrected;

        public NpcDeathEntry(NBTTagCompound tag) {
            readFromNBT(tag);
        }

        public NpcDeathEntry(NpcPlayerOwned npc, DamageSource source, boolean canRes) {
            this.stackToSpawn = ItemNpcSpawner.getSpawnerItemForNpc(npc);
            this.npcType = npc.getNpcFullType();
            this.npcName = npc.getCustomNameTag();
            this.deathCause = source.damageType;
            this.canRes = canRes;
        }

        public final void readFromNBT(NBTTagCompound tag) {
            stackToSpawn = InventoryTools.readItemStack(tag.getCompoundTag("spawnerStack"));
            npcType = tag.getString("npcType");
            npcName = tag.getString("npcName");
            deathCause = tag.getString("deathCause");
            resurrected = tag.getBoolean("resurrected");
            canRes = tag.getBoolean("canRes");
        }

        public NBTTagCompound writeToNBT(NBTTagCompound tag) {
            tag.setTag("spawnerStack", InventoryTools.writeItemStack(stackToSpawn));
            tag.setString("npcType", npcType);
            tag.setString("npcName", npcName);
            tag.setString("deathCause", deathCause);
            tag.setBoolean("resurrected", resurrected);
            tag.setBoolean("canRes", canRes);
            return tag;
        }
    }

    @Override
    public boolean onBlockClicked(EntityPlayer player) {
        if (!player.worldObj.isRemote) {
            NetworkHandler.INSTANCE.openGui(player, NetworkHandler.GUI_NPC_TOWN_HALL, xCoord, yCoord, zCoord);
        }
        return true;
    }

    public List<NpcDeathEntry> getDeathList() {
        return deathNotices;
    }

    public int getRange(){
        return broadcastRange;
    }

    public void setRange(int val){
        if(val < AWNPCStatics.townMaxRange){
            broadcastRange = val;
        }else{
            broadcastRange = AWNPCStatics.townMaxRange;
        }
    }
}
