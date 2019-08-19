package net.shadowmage.ancientwarfare.structure.tile;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.shadowmage.ancientwarfare.core.tile.TileUpdatable;
import net.shadowmage.ancientwarfare.core.util.BlockTools;

import java.util.Optional;

public class TileStake extends TileUpdatable {
	private final EntityStatueInfo entityStatueInfo = new EntityStatueInfo();
	private boolean burns = true;

	public Optional<Entity> getRenderEntity() {
		return entityStatueInfo.getRenderEntity(world);
	}

	public boolean isEntityOnFire() {
		return entityStatueInfo.isEntityOnFire();
	}

	public boolean burns() {
		return burns;
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		readNBT(compound);
		markDirty();
	}

	private void readNBT(NBTTagCompound compound) {
		entityStatueInfo.deserializeNBT(compound);
		burns = compound.getBoolean("burns");
	}

	@Override
	protected void writeUpdateNBT(NBTTagCompound tag) {
		writeNBT(tag);
	}

	@Override
	protected void handleUpdateNBT(NBTTagCompound tag) {
		readNBT(tag);
		BlockTools.notifyBlockUpdate(this);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		compound = super.writeToNBT(compound);
		return writeNBT(compound);
	}

	private NBTTagCompound writeNBT(NBTTagCompound compound) {
		compound = entityStatueInfo.serializeNBT(compound);
		compound.setBoolean("burns", burns);
		return compound;
	}

	public void resetEntityName() {
		entityStatueInfo.resetEntityName();
		markDirty();
	}

	public ResourceLocation getEntityName() {
		return entityStatueInfo.getEntityName();
	}

	public void setEntityName(ResourceLocation entityName) {
		entityStatueInfo.setEntityName(entityName);
		markDirty();
	}

	public void setEntityOnFire(boolean entityOnFire) {
		entityStatueInfo.setEntityOnFire(entityOnFire);
		markDirty();
	}

	public void setBurns(boolean burns) {
		this.burns = burns;
		markDirty();
	}

	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		return new AxisAlignedBB(pos, pos.add(0, 3, 0));
	}
}
