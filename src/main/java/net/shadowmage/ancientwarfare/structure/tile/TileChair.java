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
	protected void readNBT(NBTTagCompound compound) {
		super.readNBT(compound);
		facing = EnumFacing.byName(compound.getString("facing"));
	}

	@Override
	protected void writeNBT(NBTTagCompound compound) {
		super.writeNBT(compound);
		compound.setString("facing", facing.getName());
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
