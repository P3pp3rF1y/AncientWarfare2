package net.shadowmage.ancientwarfare.npc.tile;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.chunk.Chunk;
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
import net.shadowmage.ancientwarfare.npc.block.BlockTownHall;
import net.shadowmage.ancientwarfare.npc.config.AWNPCStatics;
import net.shadowmage.ancientwarfare.npc.container.ContainerTownHall;
import net.shadowmage.ancientwarfare.npc.entity.NpcPlayerOwned;
import net.shadowmage.ancientwarfare.npc.item.ItemNpcSpawner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import ftb.utils.world.LMPlayerServer;
import ftb.utils.world.LMWorldServer;

public class TileTownHall extends TileOwned implements IInventory, IInteractableTile {

    public boolean alarmActive = false;
    
    private int broadcastRange = AWNPCStatics.townMaxRange;
    private int updateDelayTicks = 0;
    private int activeCheckTicks = 0;
    private boolean isActive = true;
    private boolean isNeglected = false;
    private int neglectedChecksSoFar = 0;
    
    public boolean isHq = false;
    
    private String oldOwner = null;
    
    private int ticketRetry = 0;
    private final int ticketRetryMax = 20 * 60 * 10; // 10 minutes (something long, player can always try again by re-placing)

    private final List<NpcDeathEntry> deathNotices = new ArrayList<TileTownHall.NpcDeathEntry>();

    private final InventoryBasic inventory = new InventoryBasic(27);

    private final List<ContainerTownHall> viewers = new ArrayList<ContainerTownHall>();
    
    private ForgeChunkManager.Ticket ticket;

    public TileTownHall(){
        super("owner");
    }

    @Override
    public void updateEntity() {
        if (worldObj == null || worldObj.isRemote)
            return;
        
        if (!isHq && AWNPCStatics.townActiveNpcSearch && AWNPCStatics.townChunkLoadRadius > -1) {
            if (--activeCheckTicks <= 0) {
                activeCheckTicks = (int) (AWNPCStatics.townActiveNpcSearchLimit * 20 * 60 * AWNPCStatics.townActiveNpcSearchRateFactor);
                int nearbyValidEntity = isNpcOrPlayerNearby(isActive);
                if (nearbyValidEntity < 2) {
                    // no valid npc/player nearby
                    neglectedChecksSoFar++;
                    if (neglectedChecksSoFar * AWNPCStatics.townActiveNpcSearchRateFactor >= AWNPCStatics.townActiveNpcSearchLimit) {
                        // town hall has advanced a stage of neglect
                        neglectedChecksSoFar = 0;
                        String notificationTitle = "";
                        ChatComponentTranslation notificationMsg = null;
                        List<ChatComponentTranslation> notificationTooltip = new ArrayList<ChatComponentTranslation>();
                        
                        if (isNeglected && isActive) {
                            // neglected flag already set, abandon the town hall
                            notificationTitle = "ftbu_aw2.notification.townhall_abandoned";
                            notificationMsg = new ChatComponentTranslation("ftbu_aw2.notification.townhall_abandoned.msg");
                            notificationTooltip.add(new ChatComponentTranslation("ftbu_aw2.notification.townhall_abandoned.tooltip.1"));
                            notificationTooltip.add(new ChatComponentTranslation("ftbu_aw2.notification.townhall_abandoned.tooltip.2"));
                            notificationTooltip.add(new ChatComponentTranslation("ftbu_aw2.notification.townhall_abandoned.tooltip.3"));
                            notificationTooltip.add(new ChatComponentTranslation("ftbu_aw2.notification.chunk_position", xCoord>>4 , zCoord>>4));
                            notificationTooltip.add(new ChatComponentTranslation("ftbu_aw2.notification.click_to_remove"));
                            
                            isActive = false;
                            isNeglected = false;
                            //ModAccessors.FTBU.unclaimChunks(worldObj, getOwnerName(), xCoord, yCoord, zCoord);
                            unloadChunks();
                        } else if (isActive) {
                            // send neglect warning, reset timer and set neglected flag
                            notificationTitle = "ftbu_aw2.notification.townhall_neglected";
                            notificationMsg = new ChatComponentTranslation("ftbu_aw2.notification.townhall_neglected.msg", AWNPCStatics.townActiveNpcSearchLimit);
                            if (nearbyValidEntity == 0)
                                notificationTooltip.add(new ChatComponentTranslation("ftbu_aw2.notification.townhall_neglected.tooltip.1"));
                            else
                                notificationTooltip.add(new ChatComponentTranslation("ftbu_aw2.notification.townhall_neglected.tooltip.1.alt"));
                            notificationTooltip.add(new ChatComponentTranslation("ftbu_aw2.notification.townhall_neglected.tooltip.2", AWNPCStatics.townActiveNpcSearchLimit));
                            notificationTooltip.add(new ChatComponentTranslation("ftbu_aw2.notification.townhall_neglected.tooltip.3"));
                            notificationTooltip.add(new ChatComponentTranslation("ftbu_aw2.notification.chunk_position", xCoord>>4 , zCoord>>4));
                            notificationTooltip.add(new ChatComponentTranslation("ftbu_aw2.notification.click_to_remove"));
                            
                            isNeglected = true;
                        } else {
                            // town hall is inactive and there's no players or NPC's nearby, don't do anything
                            return;
                        }
                        // notify player of the neglect/abandonment
                        ModAccessors.FTBU.notifyPlayer(EnumChatFormatting.RED, getOwnerName(), notificationTitle, notificationMsg, notificationTooltip);
                    } else {
                        // neglected check has increased, but not advanced a stage yet...
                    }
                } else {
                    // town hall has a player/NPC in range
                    neglectedChecksSoFar = 0;
                    checkAndNotifyOwner();
                    isActive = true;
                    isNeglected = false;
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
    
    
    private void checkAndNotifyOwner() {
        if (isActive && !isNeglected)
            return;
        
        boolean ownerUnchanged = getOwnerName().equals(oldOwner) || oldOwner == null;
        if (ownerUnchanged) {
            // same owner as before (or no old owner, so assume it's the same)
            // show a "claim secured" type message to the current owner, regardless if neglected/abandoned
            String notificationTitle = "ftbu_aw2.notification.townhall_secured";
            ChatComponentTranslation notificationMsg = new ChatComponentTranslation("ftbu_aw2.notification.townhall_secured.msg");
            List<ChatComponentTranslation> notificationTooltip = new ArrayList<ChatComponentTranslation>();
            notificationTooltip.add(new ChatComponentTranslation("ftbu_aw2.notification.chunk_position", xCoord>>4 , zCoord>>4));
            notificationTooltip.add(new ChatComponentTranslation("ftbu_aw2.notification.click_to_remove"));
            ModAccessors.FTBU.notifyPlayer(EnumChatFormatting.GREEN, getOwnerName(), notificationTitle, notificationMsg, notificationTooltip);
        } else {
            // new owner. Notify both players of the capture
            String notificationTitle = "ftbu_aw2.notification.townhall_captured";
            ChatComponentTranslation notificationMsg = new ChatComponentTranslation("ftbu_aw2.notification.townhall_captured.msg.lost", getOwnerName());
            List<ChatComponentTranslation> notificationTooltip = new ArrayList<ChatComponentTranslation>();
            notificationTooltip.add(new ChatComponentTranslation("ftbu_aw2.notification.chunk_position", xCoord>>4 , zCoord>>4));
            notificationTooltip.add(new ChatComponentTranslation("ftbu_aw2.notification.click_to_remove"));
            
            ModAccessors.FTBU.notifyPlayer(EnumChatFormatting.RED, oldOwner, notificationTitle, notificationMsg, notificationTooltip);
            
            notificationMsg = new ChatComponentTranslation("ftbu_aw2.notification.townhall_captured.msg.gained", oldOwner);
            ModAccessors.FTBU.notifyPlayer(EnumChatFormatting.GREEN, getOwnerName(), notificationTitle, notificationMsg, notificationTooltip);
        }
        
        // manually claim the chunks immediately, don't wait for the worker thread
        LMPlayerServer lmPlayerServer = LMWorldServer.inst.getPlayer(getOwnerName());
        if (lmPlayerServer != null) {
            // we can only do this if the player is actually online 
            Chunk thisChunk = worldObj.getChunkFromBlockCoords(this.xCoord, this.zCoord);
            for (int chunkX = thisChunk.xPosition - AWNPCStatics.townChunkClaimRadius; chunkX <= thisChunk.xPosition + AWNPCStatics.townChunkClaimRadius; chunkX++) {
                for (int chunkZ = thisChunk.zPosition - AWNPCStatics.townChunkClaimRadius; chunkZ <= thisChunk.zPosition + AWNPCStatics.townChunkClaimRadius; chunkZ++) {
                    lmPlayerServer.claimChunk(worldObj.provider.dimensionId, chunkX, chunkZ);
                }
            }
        }
            
        oldOwner = null;
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
        Chunk thisChunk = this.worldObj.getChunkFromBlockCoords(xCoord, zCoord);
        
        int minX = thisChunk.xPosition * 16 - AWNPCStatics.townChunkLoadRadius * 16;
        int minY = 0;
        int minZ = thisChunk.zPosition * 16 - AWNPCStatics.townChunkLoadRadius * 16;
        int maxX = thisChunk.xPosition * 16 + (AWNPCStatics.townChunkLoadRadius + 1) * 16;
        int maxY = this.worldObj.getActualHeight();
        int maxZ = thisChunk.zPosition * 16 + (AWNPCStatics.townChunkLoadRadius + 1) * 16;
        
        AxisAlignedBB bb = AxisAlignedBB.getBoundingBox(minX, minY, minZ, maxX, maxY, maxZ);
        List<EntityLivingBase> nearbyEntities = worldObj.getEntitiesWithinAABB(EntityLivingBase.class, bb);
        
        // only used if keepOwner is false
        Map<OwnerInfo, Integer> ownerCounts = new HashMap<OwnerInfo, Integer>();
        
        int retVal = 0;
        
        if (!getOwnerName().equals(oldOwner) && oldOwner != null)
            keepOwner = true; // old owner is either the same or not set, so force-preserve the current owner
        
        for (EntityLivingBase nearbyEntity : nearbyEntities) {
            if (nearbyEntity instanceof EntityPlayer) {
                if (keepOwner) {
                    if (ModAccessors.FTBU.areFriends(((EntityPlayer)nearbyEntity).getCommandSenderName(), getOwnerName())) {
                        if (Math.abs(yCoord - nearbyEntity.posY) < AWNPCStatics.townActiveNpcSearchHeight)
                            return 2;
                        else
                            retVal = 1;
                    }
                } else {
                    if (Math.abs(yCoord - nearbyEntity.posY) < AWNPCStatics.townActiveNpcSearchHeight)
                        ownerCounts.put(new OwnerInfo(((EntityPlayer)nearbyEntity).getCommandSenderName(), ((EntityPlayer)nearbyEntity).getUniqueID()), 1);
                }
            } else if (nearbyEntity instanceof NpcPlayerOwned) {
                if (keepOwner) {
                    if (((NpcPlayerOwned)nearbyEntity).hasCommandPermissions(getOwnerName())) {
                        if (((NpcPlayerOwned)nearbyEntity).getFoodRemaining() > 0) {
                            if (Math.abs(yCoord - nearbyEntity.posY) < AWNPCStatics.townActiveNpcSearchHeight)
                                return 2;
                            else
                                retVal = 1;
                        }
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
                            if (((NpcPlayerOwned)nearbyEntity).getFoodRemaining() > 0) {
                                if (Math.abs(yCoord - nearbyEntity.posY) < AWNPCStatics.townActiveNpcSearchHeight)
                                    // this previously-found player can command this npc, give them a point
                                    owner.setValue(owner.getValue() + 1);
                            }
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
    
    public boolean isInactive() {
        return !isActive;
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
        if (tag.hasKey("isActive"))
            isActive = (tag.getBoolean("isActive"));
        if (tag.hasKey("isNeglected"))
            isNeglected = (tag.getBoolean("isNeglected"));
        if (tag.hasKey("oldOwner"))
            oldOwner = (tag.getString("oldOwner"));
        if (tag.hasKey("isHq"))
            isHq = (tag.getBoolean("isHq"));
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
        tag.setBoolean("isActive", isActive);
        tag.setBoolean("isNeglected", isNeglected);
        if (oldOwner != null)
            tag.setString("oldOwner", oldOwner);
        tag.setBoolean("isHq", isHq);
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
            if (!player.getCommandSenderName().equals(getOwnerName())) {
                // different player to the owner has used the town hall
                if (!ModAccessors.FTBU.areFriends(player.getCommandSenderName(), getOwnerName())) {
                    // players are NOT friends
                    if (this.isHq) {
                        // is a HQ, drop it instead of claiming
                        BlockTownHall block = (BlockTownHall) worldObj.getBlock(xCoord, yCoord, zCoord); 
                        block.dropBlock(worldObj, xCoord, yCoord, zCoord, block);
                        return true;
                    } else {
                        //  capture town hall
                        oldOwner = getOwnerName();
                        setOwner(player);
                    }
                    // just unclaim the chunks. A player who had a pre-existing stake (i.e. via a nearby town hall) will get priority
                    //ModAccessors.FTBU.unclaimChunks(worldObj, oldOwner, xCoord, yCoord, zCoord);
                }
            }
            
            forceUpdate();
            
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
