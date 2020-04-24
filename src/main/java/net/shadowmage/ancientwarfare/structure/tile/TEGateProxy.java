package net.shadowmage.ancientwarfare.structure.tile;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.shadowmage.ancientwarfare.core.tile.TileUpdatable;
import net.shadowmage.ancientwarfare.core.util.BlockTools;
import net.shadowmage.ancientwarfare.structure.entity.EntityGate;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class TEGateProxy extends TileUpdatable implements ITickable {
	private static final String RENDER_TAG = "render";
	@Nullable
	private EntityGate owner = null;
	private UUID entityID = null;
	private int clientEntityID = 0;
	private int noParentTicks = 0;
	private boolean render = false;

	public void setOwner(EntityGate gate) {
		this.owner = gate;
		this.entityID = owner.getPersistentID();
		BlockTools.notifyBlockUpdate(this);
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		if (tag.hasKey("msb") && tag.hasKey("lsb")) {
			long msb = tag.getLong("msb");
			long lsb = tag.getLong("lsb");
			entityID = new UUID(msb, lsb);
		}
		render = tag.getBoolean(RENDER_TAG);
		markDirty();
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		if (this.entityID != null) {
			tag.setLong("msb", entityID.getMostSignificantBits());
			tag.setLong("lsb", entityID.getLeastSignificantBits());
		}
		tag.setBoolean(RENDER_TAG, render);
		return tag;
	}

	@Override
	protected void writeUpdateNBT(NBTTagCompound tag) {
		tag.setBoolean(RENDER_TAG, render);
		tag.setInteger("owner", owner != null ? owner.getEntityId() : 0);
	}

	@Override
	protected void handleUpdateNBT(NBTTagCompound tag) {
		render = tag.getBoolean(RENDER_TAG);
		clientEntityID = tag.getInteger("owner");
	}

	@Override
	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getRenderBoundingBox() {
		return getGate().map(EntityGate::getRenderBoundingBox).orElse(super.getRenderBoundingBox());
	}

	@Override
	public void update() {
		if (!hasWorld() || (world.isRemote && (!render || clientEntityID <= 0 || owner != null))) {
			return;
		}
		if (world.isRemote) {
			Entity entity = world.getEntityByID(clientEntityID);
			owner = entity instanceof EntityGate ? (EntityGate) entity : null;
			return;
		}

		handleMissingOwner();
	}

	private void handleMissingOwner() {
		if (this.entityID == null) {
			this.noParentTicks++;
		} else if (!getOwner().isPresent()) {
			this.noParentTicks++;

			List<Entity> entities = this.world.loadedEntityList;
			for (Entity ent : entities) {
				if (ent.getPersistentID().equals(entityID) && ent instanceof EntityGate) {
					setOwner((EntityGate) ent);
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

	@SideOnly(Side.CLIENT)
	public double getMaxRenderDistanceSquared()
	{
		return 65536.0D;
	}

	public boolean isGateClosed() {
		return getOwner().map(EntityGate::isClosed).orElse(false);
	}

	public Optional<EntityGate> getOwner() {
		return Optional.ofNullable(owner);
	}

	public void setRender() {
		render = true;
		BlockTools.notifyBlockUpdate(this);
	}

	public boolean doesRender() {
		return render;
	}

	public Optional<EntityGate> getGate() {
		return Optional.ofNullable(owner);
	}

	public boolean isOpen() {
		return owner == null || !owner.isClosed();
	}
}
