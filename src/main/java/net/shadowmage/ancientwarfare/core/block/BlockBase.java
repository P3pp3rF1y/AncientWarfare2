package net.shadowmage.ancientwarfare.core.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.tile.IBlockBreakHandler;
import net.shadowmage.ancientwarfare.core.util.WorldTools;

public abstract class BlockBase extends Block {
	public BlockBase(Material material, String modID, String regName) {
		super(material);
		setUnlocalizedName(regName);
		setRegistryName(new ResourceLocation(modID, regName));
		setHardness(2);
	}

	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state) {
		if (hasTileEntity(state)) {
			WorldTools.getTile(world, pos, IBlockBreakHandler.class).ifPresent(handler -> handler.onBlockBroken(state));
		}
		super.breakBlock(world, pos, state);
	}
}
