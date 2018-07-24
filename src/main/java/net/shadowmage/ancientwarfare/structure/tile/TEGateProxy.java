package net.shadowmage.ancientwarfare.structure.tile;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.shadowmage.ancientwarfare.structure.entity.EntityGate;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class TEGateProxy extends TileEntity implements ITickable {
	@Nullable
	private EntityGate owner = null;
	private UUID entityID = null;
	private int noParentTicks = 0;

	public void setOwner(EntityGate gate) {
		this.owner = gate;
		this.entityID = owner.getPersistentID();
		markDirty();
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		if (tag.hasKey("msb") && tag.hasKey("lsb")) {
			long msb = tag.getLong("msb");
			long lsb = tag.getLong("lsb");
			entityID = new UUID(msb, lsb);
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		if (this.entityID != null) {
			tag.setLong("msb", entityID.getMostSignificantBits());
			tag.setLong("lsb", entityID.getLeastSignificantBits());
		}
		return tag;
	}

	@Override
	public void update() {
		if (!hasWorld() || this.world.isRemote) {
			return;
		}
		if (this.entityID == null) {
			this.noParentTicks++;
		} else if (!getOwner().isPresent()) {
			this.noParentTicks++;

			List<Entity> entities = this.world.loadedEntityList;
			for (Entity ent : entities) {
				if (ent instanceof EntityGate && ent.getPersistentID().equals(entityID)) {
					this.owner = (EntityGate) ent;
					this.noParentTicks = 0;
					break;
				}
			}
		}
		if (this.noParentTicks >= 100 || getOwner().map(o -> o.isDead).orElse(false)) {
			owner = null;
			this.world.setBlockToAir(pos);
		}
	}

	public boolean isGateClosed() {
		return getOwner().map(EntityGate::isClosed).orElse(false);
	}

	public Optional<EntityGate> getOwner() {
		return Optional.ofNullable(owner);
	}
}
