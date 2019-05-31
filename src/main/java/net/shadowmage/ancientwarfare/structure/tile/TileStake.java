package net.shadowmage.ancientwarfare.structure.tile;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.shadowmage.ancientwarfare.core.tile.TileUpdatable;
import net.shadowmage.ancientwarfare.core.util.BlockTools;

import java.util.Optional;

public class TileStake extends TileUpdatable {
	private static final String ENTITY_NAME_TAG = "entityName";
	private Entity entity = null;
	private ResourceLocation entityName = null;
	private boolean entityOnFire = false;
	private boolean burns = true;

	public Optional<Entity> getRenderEntity() {
		if (entity != null) {
			return Optional.of(entity);
		}
		if (entityName != null && world.isRemote) {
			EntityEntry entityEntry = ForgeRegistries.ENTITIES.getValue(entityName);
			if (entityEntry == null) {
				entityName = null;
				return Optional.empty();
			}
			entity = entityEntry.newInstance(world);
			return Optional.of(entity);
		}
		return Optional.empty();
	}

	public boolean isEntityOnFire() {
		return entityOnFire;
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
		if (compound.hasKey(ENTITY_NAME_TAG)) {
			entityName = new ResourceLocation(compound.getString(ENTITY_NAME_TAG));
			entityOnFire = compound.getBoolean("entityOnFire");
		} else {
			entityName = null;
		}
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
		if (entityName != null) {
			compound.setString(ENTITY_NAME_TAG, entityName.toString());
			compound.setBoolean("entityOnFire", entityOnFire);
		}
		compound.setBoolean("burns", burns);
		return compound;
	}

	public void resetEntityName() {
		entityName = null;
		entity = null;
		markDirty();
	}

	public ResourceLocation getEntityName() {
		return entityName;
	}

	public void setEntityName(ResourceLocation entityName) {
		this.entityName = entityName;
		entity = null;
		markDirty();
	}

	public void setEntityOnFire(boolean entityOnFire) {
		this.entityOnFire = entityOnFire;
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
