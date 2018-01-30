package net.shadowmage.ancientwarfare.core.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.tile.IBlockBreakHandler;

public abstract class BlockBase extends Block {
	public BlockBase(Material material, String modID, String regName) {
		super(material);
		setUnlocalizedName(regName);
		setRegistryName(new ResourceLocation(modID, regName));
	}

	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state) {
		if (hasTileEntity(state)) {
			TileEntity te = world.getTileEntity(pos);
			if (te instanceof IBlockBreakHandler) {
				((IBlockBreakHandler) te).onBlockBroken();
			}
		}
		super.breakBlock(world, pos, state);
	}
}
