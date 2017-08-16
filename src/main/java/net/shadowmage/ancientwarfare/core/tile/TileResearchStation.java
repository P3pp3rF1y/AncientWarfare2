package net.shadowmage.ancientwarfare.core.tile;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInvBasic;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.scoreboard.Team;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.shadowmage.ancientwarfare.core.block.BlockRotationHandler.IRotatableTile;
import net.shadowmage.ancientwarfare.core.config.AWCoreStatics;
import net.shadowmage.ancientwarfare.core.interfaces.IInteractableTile;
import net.shadowmage.ancientwarfare.core.interfaces.ITorque.ITorqueTile;
import net.shadowmage.ancientwarfare.core.interfaces.IWorkSite;
import net.shadowmage.ancientwarfare.core.interfaces.IWorker;
import net.shadowmage.ancientwarfare.core.inventory.InventoryBasic;
import net.shadowmage.ancientwarfare.core.item.ItemResearchBook;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.research.ResearchGoal;
import net.shadowmage.ancientwarfare.core.research.ResearchTracker;
import net.shadowmage.ancientwarfare.core.upgrade.WorksiteUpgrade;

import java.util.EnumSet;
import java.util.List;

public class TileResearchStation extends TileOwned implements IWorkSite, IInventory, IInvBasic, ITorqueTile, IInteractableTile, IRotatableTile {

    EnumFacing orientation = EnumFacing.NORTH;//default for old blocks

    private final InventoryBasic bookInventory = new InventoryBasic(1, this);
    private final InventoryBasic resourceInventory = new InventoryBasic(9, this);

    int startCheckDelay = 0;
    int startCheckDelayMax = 40;

    public boolean useAdjacentInventory;
    public EnumFacing inventoryDirection = EnumFacing.NORTH;
    public EnumFacing inventorySide = EnumFacing.NORTH;

    double maxEnergyStored = 1600;
    double maxInput = 100;
    private double storedEnergy;

    public TileResearchStation(){
        super("owningPlayer");
    }

    @Override
    public void onBlockBroken() {
        // TODO
    }

    @Override
    public EnumSet<WorksiteUpgrade> getUpgrades() {
        return EnumSet.noneOf(WorksiteUpgrade.class);
    }//NOOP

    @Override
    public EnumSet<WorksiteUpgrade> getValidUpgrades() {
        return EnumSet.noneOf(WorksiteUpgrade.class);
    }//NOOP

    @Override
    public void addUpgrade(WorksiteUpgrade upgrade) {
    }//NOOP

    @Override
    public void removeUpgrade(WorksiteUpgrade upgrade) {
    }//NOOP

    @Override
    public float getClientOutputRotation(EnumFacing from, float delta) {
        return 0;
    }

    @Override
    public boolean useOutputRotation(EnumFacing from) {
        return false;
    }

    @Override
    public double getMaxTorqueOutput(EnumFacing from) {
        return 0;
    }

    @Override
    public boolean canOutputTorque(EnumFacing towards) {
        return false;
    }

    @Override
    public double addTorque(EnumFacing from, double energy) {
        if (canInputTorque(from)) {
            if (energy + getTorqueStored(from) > getMaxTorque(from)) {
                energy = getMaxTorque(from) - getTorqueStored(from);
            }
            if (energy > getMaxTorqueInput(from)) {
                energy = getMaxTorqueInput(from);
            }
            storedEnergy += energy;
            return energy;
        }
        return 0;
    }

    @Override
    public double getMaxTorque(EnumFacing from) {
        return maxEnergyStored;
    }

    @Override
    public double getTorqueStored(EnumFacing from) {
        return storedEnergy;
    }

    @Override
    public double getMaxTorqueInput(EnumFacing from) {
        return maxInput;
    }

    @Override
    public boolean canInputTorque(EnumFacing from) {
        return true;
    }

    public String getCrafterName() {
        return ItemResearchBook.getResearcherName(bookInventory.getStackInSlot(0));
    }

    @Override
    public void updateEntity() {
        if (!hasWorldObj() || worldObj.isRemote) {
            return;
        }
        String name = getCrafterName();
        if (name == null) {
            return;
        }
        int goal = ResearchTracker.INSTANCE.getCurrentGoal(worldObj, name);
        boolean started = goal >= 0;
        if (started && storedEnergy >= AWCoreStatics.energyPerResearchUnit) {
            workTick(name, goal, 1);
        } else if (!started) {
            startCheckDelay--;
            if (startCheckDelay <= 0) {
                tryStartNextResearch(name);
            }
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        bookInventory.readFromNBT(tag.getCompoundTag("bookInventory"));
        resourceInventory.readFromNBT(tag.getCompoundTag("resourceInventory"));
        this.useAdjacentInventory = tag.getBoolean("useAdjacentInventory");
        this.storedEnergy = tag.getDouble("storedEnergy");
        if (tag.hasKey("orientation")) {
            setPrimaryFacing(EnumFacing.values()[tag.getInteger("orientation")]);
        }
        this.inventoryDirection = EnumFacing.getOrientation(tag.getInteger("inventoryDirection"));
        this.inventorySide = EnumFacing.getOrientation(tag.getInteger("inventorySide"));
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        tag.setTag("bookInventory", bookInventory.writeToNBT(new NBTTagCompound()));
        tag.setTag("resourceInventory", resourceInventory.writeToNBT(new NBTTagCompound()));
        tag.setBoolean("useAdjacentInventory", useAdjacentInventory);
        tag.setDouble("storedEnergy", storedEnergy);
        tag.setInteger("orientation", orientation.ordinal());
        tag.setInteger("inventoryDirection", inventoryDirection.ordinal());
        tag.setInteger("inventorySide", inventorySide.ordinal());
    }

    @Override
    public boolean hasWork() {
        return storedEnergy < maxEnergyStored;
    }

    private void workTick(String name, int goal, int tickCount) {
        ResearchGoal g1 = ResearchGoal.getGoal(goal);
        int progress = ResearchTracker.INSTANCE.getProgress(worldObj, name);
        progress += tickCount;
        if (progress >= g1.getTotalResearchTime()) {
            ResearchTracker.INSTANCE.finishResearch(worldObj, getCrafterName(), goal);
            tryStartNextResearch(name);
        } else {
            ResearchTracker.INSTANCE.setProgress(worldObj, name, progress);
        }
        storedEnergy -= AWCoreStatics.energyPerResearchUnit;
    }

    private void tryStartNextResearch(String name) {
        List<Integer> queue = ResearchTracker.INSTANCE.getResearchQueueFor(worldObj, name);
        if (!queue.isEmpty()) {
            int goalId = queue.get(0);
            ResearchGoal goalInstance = ResearchGoal.getGoal(goalId);
            if (goalInstance == null) {
                return;
            }
            if (goalInstance.tryStart(resourceInventory, -1)) {
                ResearchTracker.INSTANCE.startResearch(worldObj, name, goalId);
            } else if (useAdjacentInventory) {
                TileEntity t;
                boolean started = false;
                int x = xCoord + inventoryDirection.offsetX;
                int y = yCoord + inventoryDirection.offsetY;
                int z = zCoord + inventoryDirection.offsetZ;

                if ((t = worldObj.getTileEntity(x, y, z)) instanceof IInventory) {
                    int side = inventorySide.ordinal();
                    started = goalInstance.tryStart((IInventory) t, side);
                }
                if (started) {
                    ResearchTracker.INSTANCE.startResearch(worldObj, name, goalId);
                }
            }
        }
        startCheckDelay = startCheckDelayMax;
    }

    @Override
    public WorkType getWorkType() {
        return WorkType.RESEARCH;
    }

    @Override
    public final Team getTeam() {
        return worldObj.getScoreboard().getPlayersTeam(getOwnerName());
    }

    @Override
    public int getSizeInventory() {
        return bookInventory.getSizeInventory() + resourceInventory.getSizeInventory();
    }

    @Override
    public ItemStack getStackInSlot(int var1) {
        if(var1 < bookInventory.getSizeInventory())
            return bookInventory.getStackInSlot(var1);
        return resourceInventory.getStackInSlot(var1 - bookInventory.getSizeInventory());
    }

    @Override
    public ItemStack decrStackSize(int var1, int var2) {
        if(var1 < bookInventory.getSizeInventory()) {
            return bookInventory.decrStackSize(var1, var2);
        }
        return resourceInventory.decrStackSize(var1 - bookInventory.getSizeInventory(), var2);
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int var1) {
        if(var1 < bookInventory.getSizeInventory())
            return bookInventory.getStackInSlotOnClosing(var1);
        return resourceInventory.getStackInSlotOnClosing(var1 - bookInventory.getSizeInventory());
    }

    @Override
    public void setInventorySlotContents(int var1, ItemStack var2) {
        if(var1 < bookInventory.getSizeInventory()) {
            bookInventory.setInventorySlotContents(var1, var2);
            return;
        }
        resourceInventory.setInventorySlotContents(var1 - bookInventory.getSizeInventory(), var2);
    }

    @Override
    public void onInventoryChanged(net.minecraft.inventory.InventoryBasic internal){
        markDirty();
    }

    @Override
    public String getInventoryName() {
        return "research.station";
    }

    @Override
    public boolean hasCustomInventoryName() {
        return false;
    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
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
        return var1 >= bookInventory.getSizeInventory() || ItemResearchBook.getResearcherName(var2) != null;
    }

    @Override
    public void addEnergyFromWorker(IWorker worker) {
        storedEnergy += AWCoreStatics.energyPerWorkUnit * worker.getWorkEffectiveness(getWorkType());
        if (storedEnergy > getMaxTorque(null)) {
            storedEnergy = getMaxTorque(null);
        }
    }

    @Override
    public void addEnergyFromPlayer(EntityPlayer player) {
        storedEnergy += AWCoreStatics.energyPerWorkUnit;
        if (storedEnergy > getMaxTorque(null)) {
            storedEnergy = getMaxTorque(null);
        }
    }

    @Override
    public boolean onBlockClicked(EntityPlayer player) {
        //TODO validate team/owner status
        if (!player.worldObj.isRemote) {
            NetworkHandler.INSTANCE.openGui(player, NetworkHandler.GUI_RESEARCH_STATION, xCoord, yCoord, zCoord);
        }
        return true;
    }

    @Override
    public EnumFacing getPrimaryFacing() {
        return orientation;
    }

    @Override
    public void setPrimaryFacing(EnumFacing face) {
        this.orientation = face;
    }

    @Override
    public double drainTorque(EnumFacing from, double energy) {
        return 0;
    }

}
