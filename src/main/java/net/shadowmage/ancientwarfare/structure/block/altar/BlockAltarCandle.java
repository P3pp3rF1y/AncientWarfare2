package net.shadowmage.ancientwarfare.structure.block.altar;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.util.WorldTools;
import net.shadowmage.ancientwarfare.structure.tile.TileColored;

import javax.annotation.Nullable;

public class BlockAltarCandle extends BlockAltarTop {
	private static final AxisAlignedBB CANDLE_AABB = new AxisAlignedBB(6 / 16D, 0, 6 / 16D, 10 / 16D, 10 / 16D, 10 / 16D);

	public BlockAltarCandle() {
		super(Material.IRON, "altar_candle");
	}

	@Nullable
	@Override
	public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
		return NULL_AABB;
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		return CANDLE_AABB;
	}

	@Override
	public boolean hasTileEntity(IBlockState state) {
		return true;
	}

	@Nullable
	@Override
	public TileEntity createTileEntity(World world, IBlockState state) {
		return new TileColored();
	}

	@Override
	public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
		return WorldTools.getTile(world, pos, TileColored.class).map(TileColored::getPickBlock).orElse(ItemStack.EMPTY);
	}
}
