package net.shadowmage.ancientwarfare.automation.tile.worksite;

import cofh.redstoneflux.api.IEnergyProvider;
import cofh.redstoneflux.api.IEnergyReceiver;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.fml.common.Optional.Interface;
import net.minecraftforge.fml.common.Optional.Method;
import net.shadowmage.ancientwarfare.automation.config.AWAutomationStatics;
import net.shadowmage.ancientwarfare.automation.item.ItemWorksiteUpgrade;
import net.shadowmage.ancientwarfare.core.block.BlockRotationHandler.IRotatableTile;
import net.shadowmage.ancientwarfare.core.config.AWCoreStatics;
import net.shadowmage.ancientwarfare.core.interfaces.IInteractableTile;
import net.shadowmage.ancientwarfare.core.interfaces.ITorque.TorqueCell;
import net.shadowmage.ancientwarfare.core.interfaces.IWorkSite;
import net.shadowmage.ancientwarfare.core.interfaces.IWorker;
import net.shadowmage.ancientwarfare.core.owner.IOwnable;
import net.shadowmage.ancientwarfare.core.owner.Owner;
import net.shadowmage.ancientwarfare.core.tile.TileUpdatable;
import net.shadowmage.ancientwarfare.core.upgrade.WorksiteUpgrade;
import net.shadowmage.ancientwarfare.core.util.BlockTools;
import net.shadowmage.ancientwarfare.core.util.InventoryTools;
import org.apache.commons.lang3.math.NumberUtils;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.Optional;
import java.util.Set;

@Interface(iface = "cofh.redstoneflux.api.IEnergyProvider", modid = "redstoneflux", striprefs = true)
@Interface(iface = "cofh.redstoneflux.api.IEnergyReceiver", modid = "redstoneflux", striprefs = true)
public abstract class TileWorksiteBase extends TileUpdatable
		implements ITickable, IWorkSite, IInteractableTile, IOwnable, IRotatableTile, IEnergyProvider, IEnergyReceiver {
	private static final String UPGRADES_TAG = "upgrades";
	private static final String ORIENTATION_TAG = "orientation";
	private static final String ACTIVE_TAG = "active";

	private Owner owner = Owner.EMPTY;

	private double efficiencyBonusFactor = 0.f;

	private EnumSet<WorksiteUpgrade> upgrades = EnumSet.noneOf(WorksiteUpgrade.class);

	private EnumFacing orientation = EnumFacing.NORTH;

	private final TorqueCell torqueCell;

	private int workRetryDelay = 20;

	private boolean active = false;
	private int timeSinceLastActiveCheck = 0;

	public TileWorksiteBase() {
		torqueCell = new TorqueCell(32, 0, AWCoreStatics.energyPerWorkUnit * 3, 1);
	}

	//************************************** COFH RF METHODS ***************************************//
	@Method(modid = "redstoneflux")
	@Override
	public final int getEnergyStored(EnumFacing from) {
		return (int) (getTorqueStored(from) * AWAutomationStatics.torqueToRf);
	}

	@Method(modid = "redstoneflux")
	@Override
	public final int getMaxEnergyStored(EnumFacing from) {
		return (int) (getMaxTorque(from) * AWAutomationStatics.torqueToRf);
	}

	@Method(modid = "redstoneflux")
	@Override
	public final boolean canConnectEnergy(EnumFacing from) {
		return canOutputTorque(from) || canInputTorque(from);
	}

	@Method(modid = "redstoneflux")
	@Override
	public final int extractEnergy(EnumFacing from, int maxExtract, boolean simulate) {
		return 0;
	}

	@Method(modid = "redstoneflux")
	@Override
	public final int receiveEnergy(EnumFacing from, int maxReceive, boolean simulate) {
		if (!canInputTorque(from)) {
			return 0;
		}
		if (simulate) {
			return Math.min(maxReceive, (int) (AWAutomationStatics.torqueToRf * getMaxTorqueInput(from)));
		}
		return (int) (AWAutomationStatics.torqueToRf * addTorque(from, (double) maxReceive * AWAutomationStatics.rfToTorque));
	}
	//************************************** UPGRADE HANDLING METHODS ***************************************//

	public boolean isActive() {
		return active;
	}

	@Override
	public final Set<WorksiteUpgrade> getUpgrades() {
		return upgrades;
	}

	@Override
	public Set<WorksiteUpgrade> getValidUpgrades() {
		return EnumSet.of(WorksiteUpgrade.ENCHANTED_TOOLS_1, WorksiteUpgrade.ENCHANTED_TOOLS_2, WorksiteUpgrade.TOOL_QUALITY_1, WorksiteUpgrade.TOOL_QUALITY_2,
				WorksiteUpgrade.TOOL_QUALITY_3);
	}

	@Override
	public void onBlockBroken(IBlockState state) {
		for (WorksiteUpgrade ug : this.upgrades) {
			InventoryTools.dropItemInWorld(world, ItemWorksiteUpgrade.getStack(ug), pos);
		}
		efficiencyBonusFactor = 0;
		upgrades.clear();
	}

	@Override
	public void addUpgrade(WorksiteUpgrade upgrade) {
		upgrades.add(upgrade);
		updateEfficiency();
		BlockTools.notifyBlockUpdate(this);
		markDirty();
	}

	@Override
	public void removeUpgrade(WorksiteUpgrade upgrade) {
		upgrades.remove(upgrade);
		updateEfficiency();
		BlockTools.notifyBlockUpdate(this);
		markDirty();
	}

	public int getFortune() {
		if (getUpgrades().contains(WorksiteUpgrade.ENCHANTED_TOOLS_2)) {
			return 2;
		}
		return getUpgrades().contains(WorksiteUpgrade.ENCHANTED_TOOLS_1) ? 1 : 0;
	}

	//************************************** TILE UPDATE METHODS ***************************************//

	protected abstract Optional<IWorksiteAction> getNextAction();

	protected abstract boolean processAction(IWorksiteAction action);

	protected abstract void updateWorksite();

	@Override
	public final void update() {
		if (!hasWorld() || world.isRemote || world.getStrongPower(pos) != 0) {
			return;
		}
		if (workRetryDelay > 0) {
			workRetryDelay--;
		} else {
			world.profiler.startSection("Check For Work");
			Optional<IWorksiteAction> nextAction = getNextAction();
			boolean hasWork = nextAction.isPresent() && nextAction.get().getEnergyConsumed(efficiencyBonusFactor) <= getTorqueStored(null);
			if (timeSinceLastActiveCheck < 0) {
				if (active != checkIfActive()) {
					active = checkIfActive();
					BlockTools.notifyBlockUpdate(this);
				}
				timeSinceLastActiveCheck = 60;
			} else {
				timeSinceLastActiveCheck--;
			}
			if (hasWork) {
				world.profiler.endStartSection("Process Work");
				IWorksiteAction action = nextAction.get();
				if (processAction(action)) {
					torqueCell.setEnergy(torqueCell.getEnergy() - action.getEnergyConsumed(efficiencyBonusFactor));
					markDirty();
				} else {
					workRetryDelay = 20;
				}
			}
			world.profiler.endSection();
		}
		world.profiler.startSection("WorksiteBaseUpdate");
		updateWorksite();
		world.profiler.endSection();
	}

	private boolean checkIfActive() {
		return getTorqueStored(null) > 0;
	}

	private void updateEfficiency() {
		efficiencyBonusFactor = IWorkSite.WorksiteImplementation.getEfficiencyFactor(upgrades);
	}

	//************************************** TILE INTERACTION METHODS ***************************************//

	@Override
	public final Team getTeam() {
		return world.getScoreboard().getPlayersTeam(owner.getName());
	}

	@Override
	public Owner getOwner() {
		return owner;
	}

	@Override
	public final boolean isOwner(EntityPlayer player) {
		return owner.isOwnerOrSameTeamOrFriend(player);
	}

	@Override
	public final void setOwner(EntityPlayer player) {
		owner = new Owner(player);
	}

	@Override
	public final void setOwner(Owner owner) {
		this.owner = owner;
	}

	//************************************** TORQUE INTERACTION METHODS ***************************************//

	@Override
	public final float getClientOutputRotation(EnumFacing from, float delta) {
		return 0;
	}

	@Override
	public final boolean useOutputRotation(@Nullable EnumFacing from) {
		return false;
	}

	@Override
	public final double getMaxTorqueOutput(EnumFacing from) {
		return 0;
	}

	@Override
	public final boolean canOutputTorque(EnumFacing towards) {
		return false;
	}

	@Override
	public final double drainTorque(EnumFacing from, double energy) {
		return 0;
	}

	@Override
	public final void addEnergyFromWorker(IWorker worker) {
		addTorque(null, AWCoreStatics.energyPerWorkUnit * worker.getWorkEffectiveness(getWorkType()) * AWAutomationStatics.hand_cranked_generator_output);
	}

	@Override
	public final void addEnergyFromPlayer(EntityPlayer player) {
		addTorque(null, AWCoreStatics.energyPerWorkUnit * AWAutomationStatics.hand_cranked_generator_output);
	}

	@Override
	public final double addTorque(@Nullable EnumFacing from, double energy) {
		return torqueCell.addEnergy(energy);
	}

	@Override
	public final double getMaxTorque(@Nullable EnumFacing from) {
		return torqueCell.getMaxEnergy();
	}

	@Override
	public final double getTorqueStored(@Nullable EnumFacing from) {
		return torqueCell.getEnergy();
	}

	@Override
	public final double getMaxTorqueInput(@Nullable EnumFacing from) {
		return torqueCell.getMaxTickInput();
	}

	@Override
	public final boolean canInputTorque(EnumFacing from) {
		return true;
	}

	//************************************** MISC METHODS ***************************************//
	@Override
	public boolean shouldRenderInPass(int pass) {
		return pass == 1;
	}

	@Override
	public String toString() {
		return "Worksite Base[" + torqueCell.getEnergy() + "]";
	}

	@Override
	public boolean hasWork() {
		return torqueCell.getEnergy() < torqueCell.getMaxEnergy() && world.getStrongPower(pos) == 0;
	}

	@Override
	public final EnumFacing getPrimaryFacing() {
		return orientation;
	}

	@Override
	public final void setPrimaryFacing(EnumFacing face) {
		orientation = face;
		BlockTools.notifyBlockUpdate(this);
		markDirty();//notify neighbors of tile change
	}

	//************************************** NBT AND PACKET DATA METHODS ***************************************//

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		tag.setDouble("storedEnergy", torqueCell.getEnergy());
		owner.serializeToNBT(tag);
		if (!getUpgrades().isEmpty()) {
			int[] ug = new int[getUpgrades().size()];
			int i = 0;
			for (WorksiteUpgrade u : getUpgrades()) {
				ug[i] = u.ordinal();
				i++;
			}
			tag.setIntArray(UPGRADES_TAG, ug);
		}
		tag.setInteger(ORIENTATION_TAG, orientation.ordinal());

		return tag;
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		torqueCell.setEnergy(tag.getDouble("storedEnergy"));
		owner = Owner.deserializeFromNBT(tag);
		if (tag.hasKey(UPGRADES_TAG)) {
			NBTBase upgradeTag = tag.getTag(UPGRADES_TAG);
			if (upgradeTag instanceof NBTTagIntArray) {
				int[] ug = tag.getIntArray(UPGRADES_TAG);
				for (int anUg : ug) {
					upgrades.add(WorksiteUpgrade.values()[anUg]);
				}
			} else if (upgradeTag instanceof NBTTagList)//template parser reads int-arrays as a tag list for some reason
			{
				NBTTagList list = (NBTTagList) upgradeTag;
				for (int i = 0; i < list.tagCount(); i++) {
					String st = list.getStringTagAt(i);
					int ug = NumberUtils.toInt(st, -1);
					if (ug > -1) {
						upgrades.add(WorksiteUpgrade.values()[ug]);
					}
				}
			}
		}

		if (tag.hasKey(ORIENTATION_TAG)) {
			orientation = EnumFacing.values()[tag.getInteger(ORIENTATION_TAG)];
		}
		updateEfficiency();
	}

	@Override
	protected void writeUpdateNBT(NBTTagCompound tag) {
		super.writeUpdateNBT(tag);
		int[] ugs = new int[upgrades.size()];
		int i = 0;
		for (WorksiteUpgrade ug : upgrades) {
			ugs[i] = ug.ordinal();
			i++;
		}
		tag.setIntArray(UPGRADES_TAG, ugs);
		tag.setInteger(ORIENTATION_TAG, orientation.ordinal());
		tag.setBoolean(ACTIVE_TAG, active);
		owner.serializeToNBT(tag);
	}

	@Override
	protected void handleUpdateNBT(NBTTagCompound tag) {
		super.handleUpdateNBT(tag);
		upgrades.clear();
		if (tag.hasKey(UPGRADES_TAG)) {
			int[] ugs = tag.getIntArray(UPGRADES_TAG);
			for (int ug : ugs) {
				upgrades.add(WorksiteUpgrade.values()[ug]);
			}
		}
		updateEfficiency();
		orientation = EnumFacing.values()[tag.getInteger(ORIENTATION_TAG)];
		active = tag.getBoolean(ACTIVE_TAG);
		owner = Owner.deserializeFromNBT(tag);
		BlockTools.notifyBlockUpdate(this);
	}
}
