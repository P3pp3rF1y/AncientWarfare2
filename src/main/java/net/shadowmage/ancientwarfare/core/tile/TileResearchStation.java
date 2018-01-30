package net.shadowmage.ancientwarfare.core.tile;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.scoreboard.Team;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.shadowmage.ancientwarfare.core.block.BlockRotationHandler.IRotatableTile;
import net.shadowmage.ancientwarfare.core.config.AWCoreStatics;
import net.shadowmage.ancientwarfare.core.interfaces.IInteractableTile;
import net.shadowmage.ancientwarfare.core.interfaces.ITorque.ITorqueTile;
import net.shadowmage.ancientwarfare.core.interfaces.IWorkSite;
import net.shadowmage.ancientwarfare.core.interfaces.IWorker;
import net.shadowmage.ancientwarfare.core.item.ItemResearchBook;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.research.ResearchGoal;
import net.shadowmage.ancientwarfare.core.research.ResearchTracker;
import net.shadowmage.ancientwarfare.core.upgrade.WorksiteUpgrade;
import net.shadowmage.ancientwarfare.core.util.BlockTools;
import net.shadowmage.ancientwarfare.core.util.InventoryTools;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.List;

public class TileResearchStation extends TileOwned implements IWorkSite, ITorqueTile, IInteractableTile, IRotatableTile, ITickable {

	private EnumFacing orientation = EnumFacing.NORTH;//default for old blocks

	public final ItemStackHandler bookInventory = new ItemStackHandler(1) {
		@Override
		protected void onContentsChanged(int slot) {
			markDirty();
		}

		@Nonnull
		@Override
		public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
			return ItemResearchBook.getResearcherName(stack) != null ? super.insertItem(slot, stack, simulate) : stack;
		}
	};
	public final ItemStackHandler resourceInventory = new ItemStackHandler(9) {
		@Override
		protected void onContentsChanged(int slot) {
			markDirty();
		}
	};

	int startCheckDelay = 0;
	int startCheckDelayMax = 40;

	public boolean useAdjacentInventory;
	public EnumFacing inventoryDirection = EnumFacing.NORTH;
	public EnumFacing inventorySide = EnumFacing.NORTH;

	double maxEnergyStored = 1600;
	double maxInput = 100;
	private double storedEnergy;

	public TileResearchStation() {
		super("owningPlayer");
	}

	@Override
	public void onBlockBroken() {
		InventoryTools.dropItemsInWorld(world, bookInventory, pos);
		InventoryTools.dropItemsInWorld(world, resourceInventory, pos);
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
	public void update() {
		if (!hasWorld() || world.isRemote) {
			return;
		}
		String name = getCrafterName();
		if (name == null) {
			return;
		}
		int goal = ResearchTracker.INSTANCE.getCurrentGoal(world, name);
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
	protected void writeUpdateNBT(NBTTagCompound tag) {
		super.writeUpdateNBT(tag);
		tag.setInteger("orientation", orientation.ordinal());
	}

	@Override
	protected void handleUpdateNBT(NBTTagCompound tag) {
		super.handleUpdateNBT(tag);
		orientation = EnumFacing.VALUES[tag.getInteger("orientation")];
		BlockTools.notifyBlockUpdate(this);
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		bookInventory.deserializeNBT(tag.getCompoundTag("bookInventory"));
		resourceInventory.deserializeNBT(tag.getCompoundTag("resourceInventory"));
		this.useAdjacentInventory = tag.getBoolean("useAdjacentInventory");
		this.storedEnergy = tag.getDouble("storedEnergy");
		if (tag.hasKey("orientation")) {
			setPrimaryFacing(EnumFacing.VALUES[tag.getInteger("orientation")]);
		}
		this.inventoryDirection = EnumFacing.VALUES[tag.getInteger("inventoryDirection")];
		this.inventorySide = EnumFacing.VALUES[tag.getInteger("inventorySide")];
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		tag.setTag("bookInventory", bookInventory.serializeNBT());
		tag.setTag("resourceInventory", resourceInventory.serializeNBT());
		tag.setBoolean("useAdjacentInventory", useAdjacentInventory);
		tag.setDouble("storedEnergy", storedEnergy);
		tag.setInteger("orientation", orientation.ordinal());
		tag.setInteger("inventoryDirection", inventoryDirection.ordinal());
		tag.setInteger("inventorySide", inventorySide.ordinal());
		return tag;

	}

	@Override
	public boolean hasWork() {
		return storedEnergy < maxEnergyStored;
	}

	private void workTick(String name, int goal, int tickCount) {
		ResearchGoal g1 = ResearchGoal.getGoal(goal);
		int progress = ResearchTracker.INSTANCE.getProgress(world, name);
		progress += tickCount;
		if (progress >= g1.getTotalResearchTime()) {
			ResearchTracker.INSTANCE.finishResearch(world, getCrafterName(), goal);
			tryStartNextResearch(name);
		} else {
			ResearchTracker.INSTANCE.setProgress(world, name, progress);
		}
		storedEnergy -= AWCoreStatics.energyPerResearchUnit;
	}

	private void tryStartNextResearch(String name) {
		List<Integer> queue = ResearchTracker.INSTANCE.getResearchQueueFor(world, name);
		if (!queue.isEmpty()) {
			int goalId = queue.get(0);
			ResearchGoal goalInstance = ResearchGoal.getGoal(goalId);
			if (goalInstance == null) {
				return;
			}
			if (goalInstance.tryStart(resourceInventory)) {
				ResearchTracker.INSTANCE.startResearch(world, name, goalId);
			} else if (useAdjacentInventory) {
				TileEntity t = world.getTileEntity(pos.offset(inventoryDirection));
				boolean started = false;
				if (t != null && t.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, inventorySide)) {
					started = goalInstance.tryStart(t.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, inventorySide));
				}
				if (started) {
					ResearchTracker.INSTANCE.startResearch(world, name, goalId);
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
		return world.getScoreboard().getPlayersTeam(getOwnerName());
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
	public boolean onBlockClicked(EntityPlayer player, EnumHand hand) {
		//TODO validate team/owner status
		if (!player.world.isRemote) {
			NetworkHandler.INSTANCE.openGui(player, NetworkHandler.GUI_RESEARCH_STATION, pos);
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

	@Override
	public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
		return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || super.hasCapability(capability, facing);
	}

	@Nullable
	@Override
	public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
		if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			return (T) resourceInventory;
		}
		return super.getCapability(capability, facing);
	}
}
