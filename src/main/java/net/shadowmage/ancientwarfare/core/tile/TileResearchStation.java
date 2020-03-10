package net.shadowmage.ancientwarfare.core.tile;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.shadowmage.ancientwarfare.core.block.BlockResearchStation;
import net.shadowmage.ancientwarfare.core.block.BlockRotationHandler.IRotatableTile;
import net.shadowmage.ancientwarfare.core.config.AWCoreStatics;
import net.shadowmage.ancientwarfare.core.interfaces.IInteractableTile;
import net.shadowmage.ancientwarfare.core.interfaces.ITorque.ITorqueTile;
import net.shadowmage.ancientwarfare.core.interfaces.IWorkSite;
import net.shadowmage.ancientwarfare.core.interfaces.IWorker;
import net.shadowmage.ancientwarfare.core.item.ItemResearchBook;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.owner.Owner;
import net.shadowmage.ancientwarfare.core.registry.ResearchRegistry;
import net.shadowmage.ancientwarfare.core.research.ResearchGoal;
import net.shadowmage.ancientwarfare.core.research.ResearchTracker;
import net.shadowmage.ancientwarfare.core.upgrade.WorksiteUpgrade;
import net.shadowmage.ancientwarfare.core.util.BlockTools;
import net.shadowmage.ancientwarfare.core.util.InventoryTools;
import net.shadowmage.ancientwarfare.core.util.WorldTools;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class TileResearchStation extends TileOwned implements IWorkSite, ITorqueTile, IInteractableTile, IRotatableTile, ITickable {

	public static final String ORIENTATION_TAG = "orientation";
	private EnumFacing orientation = EnumFacing.NORTH;//default for old blocks

	public final ItemStackHandler bookInventory = new ItemStackHandler(1) {
		@Override
		protected void onContentsChanged(int slot) {
			IBlockState iblockstate = world.getBlockState(pos);
			world.setBlockState(pos, iblockstate.withProperty(BlockResearchStation.HAS_BOOK, hasBook()));
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

	private int startCheckDelay = 0;
	private int startCheckDelayMax = 40;

	public boolean useAdjacentInventory;
	public EnumFacing inventoryDirection = EnumFacing.NORTH;
	public EnumFacing inventorySide = EnumFacing.NORTH;

	private double maxEnergyStored = 1600;
	private double maxInput = 100;
	private double storedEnergy;

	public TileResearchStation() {
		super();
	}

	@Override
	public void onBlockBroken(IBlockState state) {
		InventoryTools.dropItemsInWorld(world, bookInventory, pos);
		InventoryTools.dropItemsInWorld(world, resourceInventory, pos);
	}

	public boolean hasBook() {
		return (!bookInventory.getStackInSlot(0).isEmpty());
	}

	@Override
	public Set<WorksiteUpgrade> getUpgrades() {
		return EnumSet.noneOf(WorksiteUpgrade.class);
	}

	@Override
	public Set<WorksiteUpgrade> getValidUpgrades() {
		return EnumSet.noneOf(WorksiteUpgrade.class);
	}//NOOP

	@Override
	public void addUpgrade(WorksiteUpgrade upgrade) {
		//NOOP
	}

	@Override
	public void removeUpgrade(WorksiteUpgrade upgrade) {
		//NOOP
	}

	@Override
	public float getClientOutputRotation(EnumFacing from, float delta) {
		return 0;
	}

	@Override
	public boolean useOutputRotation(@Nullable EnumFacing from) {
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
	public double addTorque(@Nullable EnumFacing from, double energy) {
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
	public double getMaxTorque(@Nullable EnumFacing from) {
		return maxEnergyStored;
	}

	@Override
	public double getTorqueStored(@Nullable EnumFacing from) {
		return storedEnergy;
	}

	@Override
	public double getMaxTorqueInput(@Nullable EnumFacing from) {
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
		Optional<String> goal = ResearchTracker.INSTANCE.getCurrentGoal(world, name);
		boolean started = goal.isPresent();
		if (started && storedEnergy >= AWCoreStatics.energyPerResearchUnit) {
			workTick(name, goal.get(), 1);
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
		tag.setInteger(ORIENTATION_TAG, orientation.ordinal());
	}

	@Override
	protected void handleUpdateNBT(NBTTagCompound tag) {
		super.handleUpdateNBT(tag);
		orientation = EnumFacing.VALUES[tag.getInteger(ORIENTATION_TAG)];
		BlockTools.notifyBlockUpdate(this);
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		bookInventory.deserializeNBT(tag.getCompoundTag("bookInventory"));
		resourceInventory.deserializeNBT(tag.getCompoundTag("resourceInventory"));
		this.useAdjacentInventory = tag.getBoolean("useAdjacentInventory");
		this.storedEnergy = tag.getDouble("storedEnergy");
		if (tag.hasKey(ORIENTATION_TAG)) {
			setPrimaryFacing(EnumFacing.VALUES[tag.getInteger(ORIENTATION_TAG)]);
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
		tag.setInteger(ORIENTATION_TAG, orientation.ordinal());
		tag.setInteger("inventoryDirection", inventoryDirection.ordinal());
		tag.setInteger("inventorySide", inventorySide.ordinal());
		return tag;

	}

	@Override
	public boolean hasWork() {
		return storedEnergy < maxEnergyStored;
	}

	private void workTick(String name, String goal, int tickCount) {
		ResearchGoal g1 = ResearchRegistry.getResearch(goal);
		int progress = ResearchTracker.INSTANCE.getProgress(world, name);
		progress += tickCount;
		//noinspection ConstantConditions
		if (progress >= g1.getTotalResearchTime()) {
			ResearchTracker.INSTANCE.finishResearch(world, getCrafterName(), goal);
			tryStartNextResearch(name);
		} else {
			ResearchTracker.INSTANCE.setProgress(world, name, progress);
		}
		storedEnergy -= AWCoreStatics.energyPerResearchUnit;
	}

	private void tryStartNextResearch(String name) {
		List<String> queue = ResearchTracker.INSTANCE.getResearchQueueFor(world, name);
		if (!queue.isEmpty()) {
			String goalName = queue.get(0);
			ResearchGoal goalInstance = ResearchRegistry.getResearch(goalName);
			if (goalInstance == null) {
				return;
			}
			if (goalInstance.tryStart(resourceInventory)) {
				ResearchTracker.INSTANCE.startResearch(world, name, goalName);
			} else if (useAdjacentInventory) {
				//noinspection ConstantConditions
				boolean started = WorldTools.getItemHandlerFromTile(world, pos.offset(inventoryDirection), inventorySide).map(goalInstance::tryStart)
						.orElse(false);
				if (started) {
					ResearchTracker.INSTANCE.startResearch(world, name, goalName);
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
		return world.getScoreboard().getPlayersTeam(getOwner().getName());
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

	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate) {
		super.shouldRefresh(world, pos, oldState, newSate);
		return !(newSate.getBlock() instanceof BlockResearchStation);
	}
}
