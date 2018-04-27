package net.shadowmage.ancientwarfare.automation.tile.worksite.cropfarm;

import net.minecraft.block.BlockStem;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandler;

import java.util.ArrayList;
import java.util.List;

public class HarvestableStem implements IHarvestable {
	@Override
	public List<BlockPos> getPositionsToHarvest(World world, BlockPos origin, IBlockState state) {
		List<BlockPos> ret = new ArrayList<>();

		addPositionIfGourd(ret, world, origin.north());
		addPositionIfGourd(ret, world, origin.west());
		addPositionIfGourd(ret, world, origin.south());
		addPositionIfGourd(ret, world, origin.east());
		return ret;
	}

	@Override
	public boolean canBeFertilized(IBlockState state, World world, BlockPos pos) {
		return ((BlockStem) state.getBlock()).canGrow(world, pos, state, world.isRemote);
	}

	private void addPositionIfGourd(List<BlockPos> list, World world, BlockPos pos) {
		if (world.getBlockState(pos).getMaterial() == Material.GOURD) {
			list.add(pos);
		}
	}

	@Override
	public boolean harvest(World world, IBlockState state, BlockPos posToHarvest, EntityPlayer player, int fortune, IItemHandler inventory) {
		return false;
	}
}
