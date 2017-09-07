package net.shadowmage.ancientwarfare.npc.tile;

import codechicken.lib.math.MathHelper;
import ftb.utils.world.LMPlayerServer;
import ftb.utils.world.LMWorldServer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
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
import net.shadowmage.ancientwarfare.npc.AncientWarfareNPC;
import net.shadowmage.ancientwarfare.npc.block.BlockTownHall;
import net.shadowmage.ancientwarfare.npc.config.AWNPCStatics;
import net.shadowmage.ancientwarfare.npc.container.ContainerTownHall;
import net.shadowmage.ancientwarfare.npc.entity.NpcPlayerOwned;
import net.shadowmage.ancientwarfare.npc.item.ItemNpcSpawner;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class TileTownHall extends TileOwned implements IInventory, IInteractableTile {

    public boolean alarmActive = false;
    
    public String name = "Unnamed";
    private int broadcastRange = AWNPCStatics.townMaxRange;
    private int updateDelayTicks = 0;
    private int activeCheckTicks = 0;
    private boolean isActive = true;
    private boolean isNeglected = false;
    private int neglectedChecksSoFar = 0;
    
    public boolean isHq = false;
    public BlockPos tpHubPos; // used in HQ GUI container
    
    private String oldOwner = null;
    
    private int ticketRetry = 0;
    private final int ticketRetryMax = 20 * 60 * 10; // 10 minutes (something long, player can always try again by re-placing)

    private final List<NpcDeathEntry> deathNotices = new ArrayList<>();

    private final InventoryBasic inventory = new InventoryBasic(27);

    private final List<ContainerTownHall> viewers = new ArrayList<>();
    
    private ForgeChunkManager.Ticket ticket;

    public TileTownHall(){
        super("owner");
    }

    @Override
    public void updateEntity() {
        if (world == null || world.isRemote)
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
                        TextComponentTranslation notificationMsg = null;
                        List<TextComponentTranslation> notificationTooltip = new ArrayList<>();
                        
                        if (isNeglected && isActive) {
                            // neglected flag already set, abandon the town hall
                            notificationTitle = "ftbu_aw2.notification.townhall_abandoned";
                            notificationMsg = new TextComponentTranslation("ftbu_aw2.notification.townhall_abandoned.msg");
                            notificationTooltip.add(new TextComponentTranslation("ftbu_aw2.notification.townhall_abandoned.tooltip.1"));
                            notificationTooltip.add(new TextComponentTranslation("ftbu_aw2.notification.townhall_abandoned.tooltip.2"));
                            notificationTooltip.add(new TextComponentTranslation("ftbu_aw2.notification.townhall_abandoned.tooltip.3"));
                            notificationTooltip.add(new TextComponentTranslation("ftbu_aw2.notification.chunk_name_and_position", name, x>>4 , z>>4));
                            notificationTooltip.add(new TextComponentTranslation("ftbu_aw2.notification.click_to_remove"));
                            
                            isActive = false;
                            isNeglected = false;
                            //ModAccessors.FTBU.unclaimChunks(world, getOwnerName(), pos);
                            unloadChunks();
                        } else if (isActive) {
                            // send neglect warning, reset timer and set neglected flag
                            notificationTitle = "ftbu_aw2.notification.townhall_neglected";
                            notificationMsg = new TextComponentTranslation("ftbu_aw2.notification.townhall_neglected.msg", AWNPCStatics.townActiveNpcSearchLimit);
                            if (nearbyValidEntity == 0)
                                notificationTooltip.add(new TextComponentTranslation("ftbu_aw2.notification.townhall_neglected.tooltip.1"));
                            else
                                notificationTooltip.add(new TextComponentTranslation("ftbu_aw2.notification.townhall_neglected.tooltip.1.alt"));
                            notificationTooltip.add(new TextComponentTranslation("ftbu_aw2.notification.townhall_neglected.tooltip.2", AWNPCStatics.townActiveNpcSearchLimit));
                            notificationTooltip.add(new TextComponentTranslation("ftbu_aw2.notification.townhall_neglected.tooltip.3"));
                            notificationTooltip.add(new TextComponentTranslation("ftbu_aw2.notification.chunk_name_and_position", name, x>>4 , z>>4));
                            notificationTooltip.add(new TextComponentTranslation("ftbu_aw2.notification.click_to_remove"));
                            
                            isNeglected = true;
                        } else {
                            // town hall is inactive and there's no players or NPC's nearby, don't do anything
                            return;
                        }
                        // notify player of the neglect/abandonment
                        ModAccessors.FTBU.notifyPlayer(TextFormatting.RED, getOwnerName(), notificationTitle, notificationMsg, notificationTooltip);
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
        forceUpdate();
        super.markDirty();
    }
    
    
    private void checkAndNotifyOwner() {
        if (isActive && !isNeglected)
            return;
        
        boolean ownerUnchanged = getOwnerName().equals(oldOwner) || oldOwner == null;
        if (ownerUnchanged) {
            // same owner as before (or no old owner, so assume it's the same)
            // show a "claim secured" type message to the current owner, regardless if neglected/abandoned
            String notificationTitle = "ftbu_aw2.notification.townhall_secured";
            TextComponentTranslation notificationMsg = new TextComponentTranslation("ftbu_aw2.notification.townhall_secured.msg");
            List<TextComponentTranslation> notificationTooltip = new ArrayList<>();
            notificationTooltip.add(new TextComponentTranslation("ftbu_aw2.notification.chunk_name_and_position", name, x>>4 , z>>4));
            notificationTooltip.add(new TextComponentTranslation("ftbu_aw2.notification.click_to_remove"));
            ModAccessors.FTBU.notifyPlayer(TextFormatting.GREEN, getOwnerName(), notificationTitle, notificationMsg, notificationTooltip);
        } else {
            // new owner. Notify both players of the capture
            String notificationTitle = "ftbu_aw2.notification.townhall_captured";
            TextComponentTranslation notificationMsg = new TextComponentTranslation("ftbu_aw2.notification.townhall_captured.msg.lost", getOwnerName());
            List<TextComponentTranslation> notificationTooltip = new ArrayList<>();
            notificationTooltip.add(new TextComponentTranslation("ftbu_aw2.notification.chunk_name_and_position", name, x>>4 , z>>4));
            notificationTooltip.add(new TextComponentTranslation("ftbu_aw2.notification.click_to_remove"));
            
            ModAccessors.FTBU.notifyPlayer(TextFormatting.RED, oldOwner, notificationTitle, notificationMsg, notificationTooltip);
            
            notificationMsg = new TextComponentTranslation("ftbu_aw2.notification.townhall_captured.msg.gained", oldOwner);
            ModAccessors.FTBU.notifyPlayer(TextFormatting.GREEN, getOwnerName(), notificationTitle, notificationMsg, notificationTooltip);
            
            
            LMPlayerServer lmPlayerServer = LMWorldServer.inst.getPlayer(getOwnerName());
            if (lmPlayerServer != null) {
                
            }
        }
        
        // manually claim the chunks immediately, don't wait for the worker thread
        LMPlayerServer lmPlayerServer = LMWorldServer.inst.getPlayer(getOwnerName());
        if (lmPlayerServer != null) {
            // we can only do this if the player is actually online 
            Chunk thisChunk = world.getChunkFromBlockCoords(this.x, this.z);
            for (int chunkX = thisChunk.xPosition - AWNPCStatics.townChunkClaimRadius; chunkX <= thisChunk.xPosition + AWNPCStatics.townChunkClaimRadius; chunkX++) {
                for (int chunkZ = thisChunk.zPosition - AWNPCStatics.townChunkClaimRadius; chunkZ <= thisChunk.zPosition + AWNPCStatics.townChunkClaimRadius; chunkZ++) {
                    lmPlayerServer.claimChunk(world.provider.getDimension(), chunkX, chunkZ);
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
            ticket = ForgeChunkManager.requestTicket(AncientWarfareNPC.instance, world, Type.NORMAL);
            if (ticket == null) {
                // no tickets available
                AncientWarfareCore.log.error("Town Hall at " + x + "x" + y + "x" + z + " has requested a chunk load ticket but Forge rejected it - probably because of a Forge config limit. Will try again in " + (ticketRetryMax / 60 / 20) + " minutes.");
                return;
            }
        }

        ticket.getModData().setInteger("blockX", x);
        ticket.getModData().setInteger("blockY", y);
        ticket.getModData().setInteger("blockZ", z);
       
        for (int chunkX = (x>>4) - AWNPCStatics.townChunkLoadRadius; chunkX <= (x>>4) + AWNPCStatics.townChunkLoadRadius; chunkX++)
            for (int chunkZ = (z>>4) - AWNPCStatics.townChunkLoadRadius; chunkZ <= (z>>4) + AWNPCStatics.townChunkLoadRadius; chunkZ++)
                ForgeChunkManager.forceChunk(ticket, new ChunkPos(chunkX, chunkZ));
    }
    
    public void unloadChunks() {
        for (int chunkX = (x>>4) - AWNPCStatics.townChunkLoadRadius; chunkX <= (x>>4) + AWNPCStatics.townChunkLoadRadius; chunkX++)
            for (int chunkZ = (z>>4) - AWNPCStatics.townChunkLoadRadius; chunkZ <= (z>>4) + AWNPCStatics.townChunkLoadRadius; chunkZ++)
                ForgeChunkManager.unforceChunk(ticket, new ChunkPos(chunkX, chunkZ));
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
        AxisAlignedBB bb = new AxisAlignedBB(x - broadcastRange, y - broadcastRange / 2, z - broadcastRange, x + broadcastRange + 1, y + broadcastRange / 2 + 1, z + broadcastRange + 1);
        List<NpcPlayerOwned> entities = world.getEntitiesWithinAABB(NpcPlayerOwned.class, bb);
        if (entities.size() > 0) {
            BlockPos pos = new BlockPos(pos);
            for (Entity entity : entities) {
                if (((NpcPlayerOwned)entity).hasCommandPermissions(getOwnerName())) {
                    ((NpcPlayerOwned)entity).handleTownHallBroadcast(this, pos);
                }
            }
        }
    }
    
    /*
     * 
     * @return 0 if no valid entity in range. 1 if within x/z but outside y, 2 if within x/y/z
     */
    private int isNpcOrPlayerNearby(boolean keepOwner) {
        Chunk thisChunk = this.world.getChunkFromBlockCoords(x, z);
        
        int minX = thisChunk.xPosition * 16 - AWNPCStatics.townChunkLoadRadius * 16;
        int minY = 0;
        int minZ = thisChunk.zPosition * 16 - AWNPCStatics.townChunkLoadRadius * 16;
        int maxX = thisChunk.xPosition * 16 + (AWNPCStatics.townChunkLoadRadius + 1) * 16;
        int maxY = this.world.getActualHeight();
        int maxZ = thisChunk.zPosition * 16 + (AWNPCStatics.townChunkLoadRadius + 1) * 16;
        
        AxisAlignedBB bb = new AxisAlignedBB(minX, minY, minZ, maxX, maxY, maxZ);
        List<EntityLivingBase> nearbyEntities = world.getEntitiesWithinAABB(EntityLivingBase.class, bb);
        
        // only used if keepOwner is false
        Map<OwnerInfo, Integer> ownerCounts = new HashMap<>();
        
        int retVal = 0;
        
        if (!getOwnerName().equals(oldOwner) && oldOwner != null)
            keepOwner = true; // old owner is either the same or not set, so force-preserve the current owner
        
        for (EntityLivingBase nearbyEntity : nearbyEntities) {
            if (nearbyEntity instanceof EntityPlayer) {
                if (keepOwner) {
                    if (ModAccessors.FTBU.areFriends(((EntityPlayer)nearbyEntity).getName(), getOwnerName())) {
                        if (Math.abs(y - nearbyEntity.posY) < AWNPCStatics.townActiveNpcSearchHeight)
                            return 2;
                        else
                            retVal = 1;
                    }
                } else {
                    if (Math.abs(y - nearbyEntity.posY) < AWNPCStatics.townActiveNpcSearchHeight)
                        ownerCounts.put(new OwnerInfo(((EntityPlayer)nearbyEntity).getName(), ((EntityPlayer)nearbyEntity).getUniqueID()), 1);
                }
            } else if (nearbyEntity instanceof NpcPlayerOwned) {
                if (keepOwner) {
                    if (((NpcPlayerOwned)nearbyEntity).hasCommandPermissions(getOwnerName())) {
                        if (((NpcPlayerOwned)nearbyEntity).getFoodRemaining() > 0) {
                            if (Math.abs(y - nearbyEntity.posY) < AWNPCStatics.townActiveNpcSearchHeight)
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
                                if (Math.abs(y - nearbyEntity.posY) < AWNPCStatics.townActiveNpcSearchHeight)
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
        if(tag.hasKey("name"))
            name = tag.getString("name");
        if(tag.hasKey("range"))
            setRange(tag.getInteger("range"));
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
        tag.setString("name", name);
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
        @Nonnull ItemStack stack = inventory.decrStackSize(var1, var2);
        if(stack!=null)
            markDirty();
        return stack;
    }

    @Override
    public ItemStack removeStackFromSlot(int var1) {
        return inventory.removeStackFromSlot(var1);
    }

    @Override
    public void setInventorySlotContents(int var1, ItemStack var2) {
        inventory.setInventorySlotContents(var1, var2);
        markDirty();
    }

    @Override
    public String getName() {
        return inventory.getName();
    }

    @Override
    public boolean hasCustomName() {
        return inventory.hasCustomName();
    }

    @Override
    public int getInventoryStackLimit() {
        return inventory.getInventoryStackLimit();
    }

    @Override
    public boolean isUsableByPlayer(EntityPlayer var1) {
        return true;
    }

    @Override
    public void openInventory(EntityPlayer player) {
    }

    @Override
    public void closeInventory(EntityPlayer player) {
    }

    @Override
    public boolean isItemValidForSlot(int var1, ItemStack var2) {
        return inventory.isItemValidForSlot(var1, var2);
    }

    public static class NpcDeathEntry {
        @Nonnull
        public ItemStack stackToSpawn;
        public String npcType;
        public String npcName;
        public String deathCause;
        public boolean resurrected;
        public boolean canRes;
        public boolean beingResurrected;
        public int[] pos;

        public NpcDeathEntry(NBTTagCompound tag) {
            readFromNBT(tag);
        }

        public NpcDeathEntry(NpcPlayerOwned npc, DamageSource source, boolean canRes) {
            this.stackToSpawn = ItemNpcSpawner.getSpawnerItemForNpc(npc);
            this.npcType = npc.getNpcFullType();
            this.npcName = npc.getCustomNameTag();
            this.deathCause = source.damageType;
            this.canRes = canRes;
            this.pos = new int[]{MathHelper.floor(npc.posX), MathHelper.floor(npc.posY), MathHelper.floor(npc.posZ)};
        }

        public final void readFromNBT(NBTTagCompound tag) {
            stackToSpawn = new ItemStack(tag.getCompoundTag("spawnerStack"));
            npcType = tag.getString("npcType");
            npcName = tag.getString("npcName");
            deathCause = tag.getString("deathCause");
            resurrected = tag.getBoolean("resurrected");
            canRes = tag.getBoolean("canRes");
            pos = tag.getIntArray("pos");
        }

        public NBTTagCompound writeToNBT(NBTTagCompound tag) {
            tag.setTag("spawnerStack", stackToSpawn.writeToNBT(new NBTTagCompound()));
            tag.setString("npcType", npcType);
            tag.setString("npcName", npcName);
            tag.setString("deathCause", deathCause);
            tag.setBoolean("resurrected", resurrected);
            tag.setBoolean("canRes", canRes);
            tag.setIntArray("pos", pos);
            return tag;
        }
    }

    @Override
    public boolean onBlockClicked(EntityPlayer player, EnumHand hand) {
        if (!player.world.isRemote) {
            if (!player.getName().equals(getOwnerName())) {
                // different player to the owner has used the town hall
                if (!ModAccessors.FTBU.areFriends(player.getName(), getOwnerName())) {
                    // players are NOT friends
                    this.isHq = false;
                    if (this.isActive) {
                        // drop the town hall
                        BlockTownHall block = (BlockTownHall) world.getBlock(pos);
                        block.dropBlock(world, pos, block);
                        return true;
                    } else {
                        // capture the town hall
                        oldOwner = getOwnerName();
                        setOwner(player);
                    }
                }
            }
            
            forceUpdate();
            
            // open GUI
            NetworkHandler.INSTANCE.openGui(player, NetworkHandler.GUI_NPC_TOWN_HALL, pos);
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
