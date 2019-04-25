package net.shadowmage.ancientwarfare.structure.entity;

import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.shadowmage.ancientwarfare.structure.block.BlockSeat;
import net.shadowmage.ancientwarfare.structure.util.RotationLimit;

import java.util.Optional;

@SuppressWarnings("squid:S2160") //no need to override equals as entityId comparison works for this as well
public class EntitySeat extends Entity implements IEntityAdditionalSpawnData {
	private BlockPos seatPos = BlockPos.ORIGIN;

	public EntitySeat(World world) {
		super(world);
		setSize(0.01f, 0.01f);
	}

	public EntitySeat(World world, Vec3d position, BlockPos seatPos) {
		super(world);
		this.seatPos = seatPos;
		setPosition(position.x, position.y, position.z);
		setSize(0.01f, 0.01f);
	}

	@Override
	protected void entityInit() {
		//noop
	}

	@Override
	protected void addPassenger(Entity passenger) {
		super.addPassenger(passenger);
		getRotationLimit().ifPresent(rotationLimit -> {
			if (!rotationLimit.isWithinLimit(passenger.rotationYaw)) {
				passenger.rotationYaw = rotationLimit.getMidPoint();
				passenger.prevRotationYaw = rotationLimit.getMidPoint();
			}
		});
	}

	@Override
	public void onEntityUpdate() {
		if (!world.isRemote && (!isBeingRidden() || world.isAirBlock(seatPos))) {
			setDead();
			world.updateComparatorOutputLevel(getPosition(), world.getBlockState(getPosition()).getBlock());
			return;
		}

		if (!world.isRemote || !isBeingRidden()) {
			return;
		}

		restrictPlayerRotation();
	}

	private void restrictPlayerRotation() {
		getRotationLimit().ifPresent(rotationLimit -> {
					if (!(getPassengers().get(0) instanceof EntityPlayer)) {
						return;
					}
					EntityPlayer passenger = (EntityPlayer) getPassengers().get(0);

					if (!rotationLimit.isWithinLimit(passenger.rotationYaw)) {
						float rotation = rotationLimit.restrictToLimit(passenger.rotationYaw);
						passenger.prevRotationYaw = rotation;
						passenger.rotationYaw = rotation;
					}
				}
		);

	}

	private Optional<RotationLimit> getRotationLimit() {
		IBlockState state = world.getBlockState(seatPos);
		Block block = state.getBlock();
		if (!(block instanceof BlockSeat)) {
			return Optional.empty();
		}
		BlockSeat seat = (BlockSeat) block;
		RotationLimit rotationLimit = seat.getRotationLimit(world, seatPos, state);
		return Optional.of(rotationLimit);
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

	@Override
	public void writeSpawnData(ByteBuf buffer) {
		buffer.writeLong(seatPos.toLong());
	}

	@Override
	public void readSpawnData(ByteBuf additionalData) {
		seatPos = BlockPos.fromLong(additionalData.readLong());
	}
}
