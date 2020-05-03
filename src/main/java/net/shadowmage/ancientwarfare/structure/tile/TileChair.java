package net.shadowmage.ancientwarfare.structure.tile;

import com.google.common.collect.ImmutableSet;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.block.BlockRotationHandler;
import net.shadowmage.ancientwarfare.core.render.property.CoreProperties;
import net.shadowmage.ancientwarfare.structure.util.BlockStateProperties;

import java.util.Set;

public class TileChair extends TileMulti implements BlockRotationHandler.IRotatableTile {
	private EnumFacing facing = EnumFacing.NORTH;

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		readNBT(compound);
	}

	@Override
	protected void writeUpdateNBT(NBTTagCompound tag) {
		super.writeUpdateNBT(tag);
		writeNBT(tag);
	}

	@Override
	protected void handleUpdateNBT(NBTTagCompound tag) {
		super.handleUpdateNBT(tag);
		readNBT(tag);
	}

	private void readNBT(NBTTagCompound compound) {
		facing = EnumFacing.byName(compound.getString("facing"));
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		return writeNBT(compound);
	}

	private NBTTagCompound writeNBT(NBTTagCompound compound) {
		compound = super.writeToNBT(compound);
		compound.setString("facing", facing.getName());
		return compound;
	}

	@Override
	public Set<BlockPos> getAdditionalPositions(IBlockState state) {
		return ImmutableSet.of(pos.up());
	}

	@Override
	public EnumFacing getPrimaryFacing() {
		return facing;
	}

	@Override
	public void setPrimaryFacing(EnumFacing face) {
		facing = face;
	}

	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState) {
		return oldState.getBlock() != newState.getBlock()
				|| oldState.getValue(BlockStateProperties.VARIANT) != newState.getValue(BlockStateProperties.VARIANT)
				|| !oldState.getValue(CoreProperties.VISIBLE).equals(newState.getValue(CoreProperties.VISIBLE));
	}
}
