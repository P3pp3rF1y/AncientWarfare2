package net.shadowmage.ancientwarfare.structure.block.altar;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.util.WorldTools;
import net.shadowmage.ancientwarfare.structure.tile.TileColored;

import javax.annotation.Nullable;

public class BlockAltarCandle extends BlockAltarTop {
	private static final AxisAlignedBB CANDLE_AABB = new AxisAlignedBB(6 / 16D, 0, 6 / 16D, 10 / 16D, 10 / 16D, 10 / 16D);
	private static final String DYE_COLOR_TAG = "dyeColor";

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
	public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> items) {
		for (int dyeDamage = 0; dyeDamage < 16; ++dyeDamage) {
			ItemStack item = new ItemStack(this, 1);
			item.setTagInfo(DYE_COLOR_TAG, new NBTTagInt(dyeDamage));
			items.add(item);
		}
	}

	@SuppressWarnings("ConstantConditions")
	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		WorldTools.getTile(world, pos, TileColored.class).ifPresent(t -> {
			if (stack.hasTagCompound()) {
				NBTTagCompound tag = stack.getTagCompound();
				if (tag.hasKey(DYE_COLOR_TAG)) {
					t.setDyeColor(tag.getInteger(DYE_COLOR_TAG));
				}
			}
		});
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
}
