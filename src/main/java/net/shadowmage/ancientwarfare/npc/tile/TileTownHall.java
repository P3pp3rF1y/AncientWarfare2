package net.shadowmage.ancientwarfare.npc.tile;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.ForgeChunkManager.Type;
import net.minecraftforge.common.util.Constants;
import net.shadowmage.ancientwarfare.core.AncientWarfareCore;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class TileTownHall extends TileOwned implements IInventory, IInteractableTile {

    public boolean alarmActive = false;
    
    private int broadcastRange = AWNPCStatics.townMaxRange;
    private int updateDelayTicks = 0;
    private int activeCheckTicks = 0;
    private boolean isActive = true;
    private boolean isNeglected = false;
    
    private String oldOwner = null;
    
    private int ticketRetry = 0;
    private int ticketRetryMax = 20 * 60 * 10; // 10 minutes (something long, player can always try again by re-placing)

    private List<NpcDeathEntry> deathNotices = new ArrayList<TileTownHall.NpcDeathEntry>();

    private final InventoryBasic inventory = new InventoryBasic(27);

    private List<ContainerTownHall> viewers = new ArrayList<ContainerTownHall>();
    
    private ForgeChunkManager.Ticket ticket;

    public TileTownHall(){
        super("owner");
    }

    @Override
    public void updateEntity() {
        if (worldObj == null || worldObj.isRemote)
            return;
        
        if (AWNPCStatics.townActiveNpcSearch && AWNPCStatics.townChunkLoadRadius > -1) {
            if (--activeCheckTicks <= 0) {
                activeCheckTicks = (int) (AWNPCStatics.townActiveNpcSearchCooldown * 20 * 60);
                int nearbyValidEntity = isNpcOrPlayerNearby(isActive); 
                if (nearbyValidEntity == 2) {
                    if (!isActive) {
                        isActive = true;
                        doRestore();
                        forceUpdate();
                    }
                    if (isNeglected)
                        isNeglected = false;
                } else {
                    // notification vars
                    String notificationTitle = "";
                    IChatComponent notificationMsg = null;
                    List<IChatComponent> notificationTooltip = new ArrayList<IChatComponent>();
                    
                    if (isNeglected) {
                        ModAccessors.FTBU.unclaimChunks(worldObj, getOwnerName(), xCoord, yCoord, zCoord);
                        unloadChunks();
                        
                        notificationTitle = "ftbu_aw2.notification.townhall_abandoned";
                        notificationMsg = ModAccessors.FTBU.chatComponent("ftbu_aw2.notification.townhall_abandoned.msg");
                        notificationTooltip.add(ModAccessors.FTBU.chatComponent("ftbu_aw2.notification.townhall_abandoned.tooltip.1"));
                        notificationTooltip.add(ModAccessors.FTBU.chatComponent("ftbu_aw2.notification.townhall_abandoned.tooltip.2"));
                        notificationTooltip.add(ModAccessors.FTBU.chatComponent("ftbu_aw2.notification.townhall_abandoned.tooltip.3"));
                        notificationTooltip.add(ModAccessors.FTBU.chatComponent("ftbu_aw2.notification.chunk_position", xCoord>>4 , zCoord>>4));
                        notificationTooltip.add(ModAccessors.FTBU.chatComponent("ftbu_aw2.notification.click_to_remove"));
                        
                        isActive = false;
                    } else {
                        // warn player of neglected townhall
                        notificationTitle = "ftbu_aw2.notification.townhall_neglected";
                        notificationMsg = ModAccessors.FTBU.chatComponent("ftbu_aw2.notification.townhall_neglected.msg", AWNPCStatics.townActiveNpcSearchCooldown);
                        if (nearbyValidEntity == 0)
                            notificationTooltip.add(ModAccessors.FTBU.chatComponent("ftbu_aw2.notification.townhall_neglected.tooltip.1"));
                        else
                            notificationTooltip.add(ModAccessors.FTBU.chatComponent("ftbu_aw2.notification.townhall_neglected.tooltip.1.alt"));
                        notificationTooltip.add(ModAccessors.FTBU.chatComponent("ftbu_aw2.notification.townhall_neglected.tooltip.2", AWNPCStatics.townActiveNpcSearchCooldown));
                        notificationTooltip.add(ModAccessors.FTBU.chatComponent("ftbu_aw2.notification.townhall_neglected.tooltip.3"));
                        notificationTooltip.add(ModAccessors.FTBU.chatComponent("ftbu_aw2.notification.chunk_position", xCoord>>4 , zCoord>>4));
                        notificationTooltip.add(ModAccessors.FTBU.chatComponent("ftbu_aw2.notification.click_to_remove"));
                        
                        isNeglected = true;
                    }
                    ModAccessors.FTBU.notifyPlayer(getOwnerName(), notificationTitle, notificationMsg, notificationTooltip);
                }
            }
        }

        if (--updateDelayTicks <= 0 && isActive) {
            broadcast();
            updateDelayTicks = AWNPCStatics.townUpdateFreq;
            if (AWNPCStatics.townChunkLoadRadius > -1)
                loadChunks();
        }
    }
    
    public void forceUpdate() {
        updateDelayTicks = 0;
        activeCheckTicks = 0;
    }
    
    @Override
    public void markDirty() {
        super.markDirty();
        forceUpdate();
    }
    
    private void doRestore() {
        if (oldOwner != null) {
            String notificationTitle = "ftbu_aw2.notification.townhall_captured";
            IChatComponent notificationMsg = ModAccessors.FTBU.chatComponent("ftbu_aw2.notification.townhall_captured.msg", getOwnerName());
            List<IChatComponent> notificationTooltip = new ArrayList<IChatComponent>();
            notificationTooltip.add(ModAccessors.FTBU.chatComponent("ftbu_aw2.notification.chunk_position", xCoord>>4 , zCoord>>4));
            notificationTooltip.add(ModAccessors.FTBU.chatComponent("ftbu_aw2.notification.click_to_remove"));
            ModAccessors.FTBU.notifyPlayer(getOwnerName(), notificationTitle, notificationMsg, notificationTooltip);
            oldOwner = null;
        }
        ModAccessors.FTBU.claimChunks(worldObj, getOwnerName(), xCoord, yCoord, zCoord);
    }

    private void loadChunks() {
        if (--ticketRetry > 0)
            return;
        ticketRetry = ticketRetryMax;
        if (ticket == null) {
            ticket = ForgeChunkManager.requestTicket(AncientWarfareNPC.instance, worldObj, Type.NORMAL);
            if (ticket == null) {
                // no tickets available
                AncientWarfareCore.log.error("Town Hall at " + xCoord + "x" + yCoord + "x" + zCoord + " has requested a chunk load ticket but Forge rejected it - probably because of a Forge config limit. Will try again in " + (ticketRetryMax / 60 / 20) + " minutes.");
                return;
            }
        }

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
        AxisAlignedBB bb = AxisAlignedBB.getBoundingBox(xCoord - broadcastRange, yCoord - broadcastRange / 2, zCoord - broadcastRange, xCoord + broadcastRange + 1, yCoord + broadcastRange / 2 + 1, zCoord + broadcastRange + 1);
        List<NpcPlayerOwned> entities = worldObj.getEntitiesWithinAABB(NpcPlayerOwned.class, bb);
        if (entities.size() > 0) {
            BlockPosition pos = new BlockPosition(xCoord, yCoord, zCoord);
            for (Entity entity : entities) {
                if (((NpcPlayerOwned)entity).hasCommandPermissions(getOwnerName())) {
                    ((NpcPlayerOwned)entity).handleTownHallBroadcast(this, pos);
                }
            }
        }
    }
    
    /**
     * 
     * @return 0 if no valid entity in range. 1 if within x/z but outside y, 2 if within x/y/z
     */
    private int isNpcOrPlayerNearby(boolean keepOwner) {
        int minX = (xCoord >> 4) * 16 - (AWNPCStatics.townChunkLoadRadius * 16);
        int minY = 0;
        int minZ = (zCoord >> 4) * 16 - (AWNPCStatics.townChunkLoadRadius * 16);
        int maxX = (xCoord >> 4) * 16 + (AWNPCStatics.townChunkLoadRadius * 16);
        int maxY = this.worldObj.getActualHeight();
        int maxZ = (zCoord >> 4) * 16 + (AWNPCStatics.townChunkLoadRadius * 16);
        
        
        AxisAlignedBB bb = AxisAlignedBB.getBoundingBox(minX, minY, minZ, maxX, maxY, maxZ);
        List<EntityLivingBase> nearbyEntities = worldObj.getEntitiesWithinAABB(EntityLivingBase.class, bb);
        
        // only used if keepOwner is false
        Map<OwnerInfo, Integer> ownerCounts = new HashMap<OwnerInfo, Integer>();
        
        int retVal = 0;
        
        for (EntityLivingBase nearbyEntity : nearbyEntities) {
            if (nearbyEntity instanceof EntityPlayer) {
                if (keepOwner) {
                    if (((EntityPlayer)nearbyEntity).getCommandSenderName().equals(getOwnerName())) {
                        if (Math.abs(yCoord - nearbyEntity.posY) > AWNPCStatics.townActiveNpcSearchHeight)
                            retVal = 1;
                        else
                            return 2;
                    }
                } else {
                    if (Math.abs(yCoord - nearbyEntity.posY) > AWNPCStatics.townActiveNpcSearchHeight)
                        ownerCounts.put(new OwnerInfo(((EntityPlayer)nearbyEntity).getCommandSenderName(), ((EntityPlayer)nearbyEntity).getUniqueID()), 1);
                }
            } else if (nearbyEntity instanceof NpcPlayerOwned) {
                if (keepOwner) {
                    if (((NpcPlayerOwned)nearbyEntity).hasCommandPermissions(getOwnerName())) {
                        if (Math.abs(yCoord - nearbyEntity.posY) > AWNPCStatics.townActiveNpcSearchHeight)
                            retVal = 1;
                        else
                            return 2;
                    }
                } else {
                    boolean foundNpcOwner = false;
                    // loop over each of the previously-found players
                    for (Map.Entry<OwnerInfo, Integer> owner : ownerCounts.entrySet()) {
                        if (owner.getKey().equals(getOwnerName())) {
                            // this entity's owner is a previously-found player
                            foundNpcOwner = true;
                        }
                        if (((NpcPlayerOwned)nearbyEntity).hasCommandPermissions(owner.getKey().ownerName))
                            if (Math.abs(yCoord - nearbyEntity.posY) > AWNPCStatics.townActiveNpcSearchHeight)
                                // this previously-found player can command this npc, give them a point
                                owner.setValue(owner.getValue() + 1);
                    }
                    if (!foundNpcOwner)
                        // an NPC was found nearby with no nearby owner, so give the owner a point
                        ownerCounts.put(new OwnerInfo(((NpcPlayerOwned)nearbyEntity).getOwnerName(), ((NpcPlayerOwned)nearbyEntity).getOwnerUuid()), 1);
                }
            }
        }
        
        if (!keepOwner) {
            // find the ownername with the largest score
            String winnerName = null;
            UUID winnerUuid = null;
            int winnerScore = 0;
            for (Map.Entry<OwnerInfo, Integer> owner : ownerCounts.entrySet()) {
                if (owner.getValue() > winnerScore) {
                    winnerName = owner.getKey().ownerName;
                    winnerUuid = owner.getKey().ownerUuid;
                    winnerScore = owner.getValue();
                    retVal = 2;
                }
            }
            if (retVal == 2) {
                oldOwner = getOwnerName();
                this.setOwner(winnerName, winnerUuid);
            }
        }
        
        return retVal;
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
            // force an update
            updateDelayTicks = 0;
            activeCheckTicks = 0;
            if (!player.getCommandSenderName().equals(getOwnerName())) {
                // different player to the owner has used the town hall
                if (!ModAccessors.FTBU.areFriends(player.getCommandSenderName(), getOwnerName())) {
                    // players are NOT friends - capture the town hall!
                    oldOwner = getOwnerName();
                    setOwner(player);
                    // just unclaim the chunks. A player who had a pre-existing stake (i.e. via a nearby town hall) will get priority
                    ModAccessors.FTBU.unclaimChunks(worldObj, oldOwner, xCoord, yCoord, zCoord);
                }
            }
            
            doRestore();
            
            // open GUI
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
    
    public class OwnerInfo {
        public final String ownerName;
        public final UUID ownerUuid;
        
        public OwnerInfo(String ownerName, UUID ownerUuid) {
            this.ownerName = ownerName;
            this.ownerUuid = ownerUuid;
        }
    }
}
