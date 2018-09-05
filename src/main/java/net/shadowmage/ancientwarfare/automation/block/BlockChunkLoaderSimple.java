package net.shadowmage.ancientwarfare.automation.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.automation.tile.TileChunkLoaderSimple;
import net.shadowmage.ancientwarfare.core.util.WorldTools;

public class BlockChunkLoaderSimple extends BlockBaseAutomation {
	public BlockChunkLoaderSimple(String regName) {
		super(Material.ROCK, regName);
		setHardness(2.f);
	}

	@Override
	public boolean hasTileEntity(IBlockState state) {
		return true;
	}

	@Override
	public TileEntity createTileEntity(World world, IBlockState state) {
		return new TileChunkLoaderSimple();
	}

	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state) {
		WorldTools.getTile(world, pos, TileChunkLoaderSimple.class).ifPresent(TileChunkLoaderSimple::releaseTicket);
		super.breakBlock(world, pos, state);
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		return WorldTools.clickInteractableTileWithHand(world, pos, player, hand);
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		super.onBlockPlacedBy(world, pos, state, placer, stack);
		if (!world.isRemote) {
			WorldTools.getTile(world, pos, TileChunkLoaderSimple.class).ifPresent(TileChunkLoaderSimple::setupInitialTicket);
		}
	}
}
