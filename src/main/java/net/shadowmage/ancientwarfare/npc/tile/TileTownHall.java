package net.shadowmage.ancientwarfare.npc.tile;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.util.Constants;
import net.shadowmage.ancientwarfare.core.interfaces.IInteractableTile;
import net.shadowmage.ancientwarfare.core.inventory.InventoryBasic;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.tile.TileOwned;
import net.shadowmage.ancientwarfare.npc.config.AWNPCStatics;
import net.shadowmage.ancientwarfare.npc.container.ContainerTownHall;
import net.shadowmage.ancientwarfare.npc.entity.NpcPlayerOwned;
import net.shadowmage.ancientwarfare.npc.item.ItemNpcSpawner;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class TileTownHall extends TileOwned implements IInventory, IInteractableTile, ITickable {

    public boolean alarmActive = false;
    
    public String name = "Unnamed"; //TODO see if naming town halls is needed for anything
    private int broadcastRange = AWNPCStatics.townMaxRange;
    private int updateDelayTicks = 0;
    private boolean isActive = true;
    private boolean isNeglected = false;

    private String oldOwner = null;
    
    private final List<NpcDeathEntry> deathNotices = new ArrayList<>();

    private final InventoryBasic inventory = new InventoryBasic(27);

    private final List<ContainerTownHall> viewers = new ArrayList<>();
    
    private ForgeChunkManager.Ticket ticket;

    public TileTownHall(){
        super("owner");
    }

    @Override
    public void update() {
        if (world == null || world.isRemote)
            return;

        updateDelayTicks--;
        if (updateDelayTicks <= 0 && isActive) {
            broadcast();
            updateDelayTicks = AWNPCStatics.townUpdateFreq;
        }
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
        AxisAlignedBB bb = new AxisAlignedBB(pos.getX() - broadcastRange, pos.getY() - broadcastRange / 2, pos.getZ() - broadcastRange, pos.getX() + broadcastRange + 1, pos.getY() + broadcastRange / 2 + 1, pos.getZ() + broadcastRange + 1);
        List<NpcPlayerOwned> entities = world.getEntitiesWithinAABB(NpcPlayerOwned.class, bb);
        if (entities.size() > 0) {
            for (Entity entity : entities) {
                if (((NpcPlayerOwned)entity).hasCommandPermissions(getOwnerName())) {
                    ((NpcPlayerOwned)entity).handleTownHallBroadcast(this, pos);
                }
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

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        inventory.deserializeNBT(tag.getCompoundTag("inventory"));
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
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        tag.setTag("inventory", inventory.serializeNBT());
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
        return tag;
    }

    @Override
    public int getSizeInventory() {
        return inventory.getSizeInventory();
    }

    @Override
    public boolean isEmpty() {
        return inventory.isEmpty();
    }

    @Override
    public ItemStack getStackInSlot(int var1) {
        return inventory.getStackInSlot(var1);
    }

    @Override
    public ItemStack decrStackSize(int var1, int var2) {
        @Nonnull ItemStack stack = inventory.decrStackSize(var1, var2);
        if(!stack.isEmpty())
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

    @Override
    public int getField(int id) {
        return 0;
    }

    @Override
    public void setField(int id, int value) {

    }

    @Override
    public int getFieldCount() {
        return 0;
    }

    @Override
    public void clear() {
        inventory.clear();
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
    @SuppressWarnings("squid:S3516")
    public boolean onBlockClicked(EntityPlayer player, EnumHand hand) {
        if (!player.world.isRemote) {
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
}
