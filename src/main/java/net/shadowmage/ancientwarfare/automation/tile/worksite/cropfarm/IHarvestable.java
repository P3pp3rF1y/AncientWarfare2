package net.shadowmage.ancientwarfare.automation.tile.worksite.cropfarm;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandler;

import java.util.List;

public interface IHarvestable {
	List<BlockPos> getPositionsToHarvest(World world, BlockPos pos, IBlockState state);

	boolean canBeFertilized(IBlockState state, World world, BlockPos pos);

	boolean harvest(World world, IBlockState state, BlockPos pos, EntityPlayer player, int fortune, IItemHandler inventory);

	boolean matches(IBlockState state);
}
