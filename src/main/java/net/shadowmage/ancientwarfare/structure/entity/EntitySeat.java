package net.shadowmage.ancientwarfare.structure.entity;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

@SuppressWarnings("squid:S2160") //no need to override equals as entityId comparison works for this as well
public class EntitySeat extends Entity {
	private BlockPos seatPos = BlockPos.ORIGIN;

	public EntitySeat(World world) {
		super(world);
		setSize(0.01f, 0.01f);
	}

	public EntitySeat(World world, Vec3d position) {
		super(world);
		setPosition(position.x, position.y, position.z);
		setSize(0.01f, 0.01f);
	}

	@Override
	protected void entityInit() {
		//noop
	}

	@Override
	public void onEntityUpdate() {
		if (!world.isRemote && (!isBeingRidden() || world.isAirBlock(seatPos))) {
			setDead();
			world.updateComparatorOutputLevel(getPosition(), world.getBlockState(getPosition()).getBlock());
		}
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound compound) {
		seatPos = BlockPos.fromLong(compound.getLong("seatPos"));
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound compound) {
		compound.setLong("seatPos", seatPos.toLong());
	}

	public BlockPos getSeatPos() {
		return seatPos;
	}
}
