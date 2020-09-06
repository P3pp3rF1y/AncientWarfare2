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
import java.util.function.Function;
import java.util.function.Supplier;

public abstract class TileMulti extends TileUpdatable implements IBlockBreakHandler {
	private static final String MAIN_BLOCK_POS_TAG = "mainBlockPos";
	private BlockPos mainBlockPos = null;

	@Override
	public final void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		readNBT(compound);
	}

	protected void readNBT(NBTTagCompound compound) {
		if (compound.hasKey(MAIN_BLOCK_POS_TAG)) {
			mainBlockPos = BlockPos.fromLong(compound.getLong(MAIN_BLOCK_POS_TAG));
		}
	}

	@Override
	public final NBTTagCompound writeToNBT(NBTTagCompound compound) {
		compound = super.writeToNBT(compound);
		writeNBT(compound);
		return compound;
	}

	protected void writeNBT(NBTTagCompound compound) {
		if (mainBlockPos != null) {
			compound.setLong(MAIN_BLOCK_POS_TAG, mainBlockPos.toLong());
		}
	}

	@Override
	protected final void writeUpdateNBT(NBTTagCompound tag) {
		super.writeUpdateNBT(tag);
		writeNBT(tag);
	}

	@Override
	protected final void handleUpdateNBT(NBTTagCompound tag) {
		super.handleUpdateNBT(tag);
		readNBT(tag);
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

	protected <T, U extends TileMulti> T getValueFromMain(Class<U> teClass, Function<U, T> getValue, T value, Supplier<T> getDefaultValue) {
		Optional<BlockPos> mainPos = getMainBlockPos();
		if (!mainPos.isPresent() || mainPos.get().equals(pos)) {
			return value;
		}
		return WorldTools.getTile(world, mainPos.get(), teClass).map(getValue).orElse(getDefaultValue.get());
	}

	public void setPlacementDirection(World world, BlockPos pos, IBlockState state, EnumFacing horizontalFacing, float rotationYaw) {
		//noop by default
	}

	public void setMainPosOnAdditionalBlocks() {
		getAdditionalPositions(world.getBlockState(pos)).forEach(additionalPos -> WorldTools.getTile(world, additionalPos, TileMulti.class)
				.ifPresent(teAdditional -> teAdditional.setMainBlockPos(pos)));
	}
}
