package net.shadowmage.ancientwarfare.structure.tile;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.tile.IBlockBreakHandler;
import net.shadowmage.ancientwarfare.core.tile.TileUpdatable;
import net.shadowmage.ancientwarfare.core.util.WorldTools;

import java.util.Optional;
import java.util.Set;

public abstract class TileMulti extends TileUpdatable implements IBlockBreakHandler {
	private static final String MAIN_BLOCK_POS_TAG = "mainBlockPos";
	private BlockPos mainBlockPos = null;

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		if (compound.hasKey(MAIN_BLOCK_POS_TAG)) {
			mainBlockPos = BlockPos.fromLong(compound.getLong(MAIN_BLOCK_POS_TAG));
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		compound = super.writeToNBT(compound);
		if (mainBlockPos != null) {
			compound.setLong(MAIN_BLOCK_POS_TAG, mainBlockPos.toLong());
		}
		return compound;
	}

	public void setMainBlockPos(BlockPos mainBlockPos) {
		this.mainBlockPos = mainBlockPos;
	}

	public Optional<BlockPos> getMainBlockPos() {
		return Optional.ofNullable(mainBlockPos);
	}

	@Override
	public void onBlockBroken(IBlockState state) {
		Optional<BlockPos> mainPos = getMainBlockPos();
		if (mainPos.isPresent() && !mainPos.get().equals(pos)) {
			IBlockState mainState = world.getBlockState(mainPos.get());
			if (mainState.getBlock() != Blocks.AIR) {
				WorldTools.getTile(world, mainPos.get(), TileMulti.class).ifPresent(tileMulti -> tileMulti.onBlockBroken(mainState));
				world.setBlockToAir(mainPos.get());
			}
			return;
		}

		getAdditionalPositions(state).forEach(position -> world.setBlockToAir(position));
	}

	public abstract Set<BlockPos> getAdditionalPositions(IBlockState state);

	public void setPlacementDirection(World world, BlockPos pos, IBlockState state, EnumFacing horizontalFacing, float rotationYaw) {
		//noop by default
	}

	public void setMainPosOnAdditionalBlocks() {
		getAdditionalPositions(world.getBlockState(pos)).forEach(additionalPos -> WorldTools.getTile(world, additionalPos, TileMulti.class)
				.ifPresent(teAdditional -> teAdditional.setMainBlockPos(pos)));
	}
}
