package net.shadowmage.ancientwarfare.structure.tile;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.shadowmage.ancientwarfare.core.config.AWCoreStatics;
import net.shadowmage.ancientwarfare.core.interfaces.IWorkSite;
import net.shadowmage.ancientwarfare.core.interfaces.IWorker;
import net.shadowmage.ancientwarfare.core.owner.IOwnable;
import net.shadowmage.ancientwarfare.core.owner.Owner;
import net.shadowmage.ancientwarfare.core.tile.TileUpdatable;
import net.shadowmage.ancientwarfare.core.upgrade.WorksiteUpgrade;
import net.shadowmage.ancientwarfare.structure.template.build.StructureBB;
import net.shadowmage.ancientwarfare.structure.template.build.StructureBuilderTicked;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.Set;

public class TileStructureBuilder extends TileUpdatable implements IWorkSite, IOwnable, ITickable {
	private static final String BUILDER_TAG = "builder";
	private static final String BB_MIN_TAG = "bbMin";
	private static final String BB_MAX_TAG = "bbMax";
	private Owner owner = Owner.EMPTY;

	private StructureBuilderTicked builder;
	private boolean shouldRemove = false;
	private boolean isStarted = false;
	private int workDelay = 20;

	private double maxEnergyStored;
	private double maxInput;
	private double storedEnergy;
	public StructureBB clientBB;

	public TileStructureBuilder() {
		maxEnergyStored = AWCoreStatics.energyPerWorkUnit * 3;
		maxInput = AWCoreStatics.energyPerWorkUnit;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getRenderBoundingBox() {
		return clientBB != null ? new AxisAlignedBB(clientBB.min, clientBB.max) : super.getRenderBoundingBox();
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
	public double addTorque(@Nullable EnumFacing from, double energy) {
		if (canInputTorque(from)) {
			if (energy + getTorqueStored(null) > getMaxTorque(null)) {
				energy = getMaxTorque(null) - getTorqueStored(null);
			}
			if (energy > getMaxTorqueInput(null)) {
				energy = getMaxTorqueInput(null);
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
	public boolean canInputTorque(@Nullable EnumFacing from) {
		return true;
	}

	@Override
	public void update() {
		if (!hasWorld() || world.isRemote) {
			return;
		}
		if (shouldRemove || builder == null || builder.invalid || builder.isFinished()) {
			shouldRemove = true;
			world.setBlockToAir(pos);
			return;
		}
		if (builder.getWorld() == null) {
			builder.setWorld(world);
		}
		if (Loader.isModLoaded("ancientwarfareautomation") || Loader.isModLoaded("ancientwarfarenpc")) {
			if (storedEnergy >= AWCoreStatics.energyPerWorkUnit) {
				storedEnergy -= AWCoreStatics.energyPerWorkUnit;
				processWork();
			}
		} else {
			if (workDelay-- <= 0) {
				processWork();
				workDelay = 20;
			}
		}
	}

	private void processWork() {
		isStarted = true;
		builder.tick();
	}

	/*
	 * should be called immediately after the tile-entity is set into the world
	 * from the ItemBlockStructureBuilder item onBlockPlaced code
	 */
	@Override
	public void setOwner(EntityPlayer player) {
		this.owner = new Owner(player);
	}

	@Override
	public void setOwner(Owner owner) {
		this.owner = owner;
	}

	@Override
	public boolean isOwner(EntityPlayer player) {
		return owner.isOwnerOrSameTeamOrFriend(player);
	}

	@Override
	public Owner getOwner() {
		return owner;
	}

	/*
	 * should be called immediately after the tile-entity is set into the world
	 * from the ItemBlockStructureBuilder item onBlockPlaced code<br>
	 * the passed in builder must be valid (have a valid structure), and must not
	 * be null
	 */
	public void setBuilder(StructureBuilderTicked builder) {
		this.builder = builder;
	}

	public void onBlockBroken(IBlockState state) {
		//noop
	}

	public void onBlockClicked(EntityPlayer player) {
		if (builder.hasClearedArea()) {
			int pass = builder.getPass() + 1;
			int max = builder.getMaxPasses();
			float percent = builder.getPercentDoneWithPass() * 100.f;
			String perc = String.format("%.2f", percent) + "%";
			player.sendMessage(new TextComponentTranslation("guistrings.structure.builder.state", perc, pass, max));
		} else {
			float percent = builder.getPercentDoneClearing() * 100.f;
			String perc = String.format("%.2f", percent) + "%";
			player.sendMessage(new TextComponentTranslation("guistrings.structure.builder.clear_state", perc));
		}
	}

	@Override
	protected void writeUpdateNBT(NBTTagCompound tag) {
		super.writeUpdateNBT(tag);
		if (builder == null) {
			return;
		}
		StructureBB bb = builder.getBoundingBox();
		tag.setLong(BB_MIN_TAG, bb.min.toLong());
		tag.setLong(BB_MAX_TAG, bb.max.toLong());
	}

	@Override
	protected void handleUpdateNBT(NBTTagCompound tag) {
		super.handleUpdateNBT(tag);
		if (tag.hasKey(BB_MIN_TAG) && tag.hasKey(BB_MAX_TAG)) {
			clientBB = new StructureBB(BlockPos.fromLong(tag.getLong(BB_MIN_TAG)), BlockPos.fromLong(tag.getLong(BB_MAX_TAG)));
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		if (tag.hasKey(BUILDER_TAG)) {
			builder = new StructureBuilderTicked();
			builder.readFromNBT(tag.getCompoundTag(BUILDER_TAG));
		} else {
			this.shouldRemove = true;
		}
		this.isStarted = tag.getBoolean("started");
		this.storedEnergy = tag.getDouble("storedEnergy");
		owner = Owner.deserializeFromNBT(tag);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		if (builder != null) {
			NBTTagCompound builderTag = new NBTTagCompound();
			builder.writeToNBT(builderTag);
			tag.setTag(BUILDER_TAG, builderTag);
		}
		tag.setBoolean("started", isStarted);
		tag.setDouble("storedEnergy", storedEnergy);
		owner.serializeToNBT(tag);
		return tag;
	}

	//******************************************WORKSITE************************************************//
	@Override
	public boolean hasWork() {
		return storedEnergy < maxEnergyStored;
	}

	@Override
	public WorkType getWorkType() {
		return WorkType.CRAFTING;
	}

	@Override
	public final Team getTeam() {
		return world.getScoreboard().getPlayersTeam(owner.getName());
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
	public double getMaxTorqueOutput(EnumFacing from) {
		return 0;
	}

	@Override
	public boolean canOutputTorque(EnumFacing towards) {
		return false;
	}

	@Override
	public double drainTorque(EnumFacing from, double energy) {
		return 0;
	}

	public StructureBuilderTicked getBuilder() {
		return builder;
	}
}
