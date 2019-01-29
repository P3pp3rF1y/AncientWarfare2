package net.shadowmage.ancientwarfare.structure.item;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.shadowmage.ancientwarfare.core.item.ItemBlockBase;
import net.shadowmage.ancientwarfare.core.util.WorldTools;
import net.shadowmage.ancientwarfare.structure.tile.TileColored;

public class ItemBlockColored extends ItemBlockBase {
	private static final String DYE_COLOR_TAG = "dyeColor";

	public ItemBlockColored(Block block) {
		super(block);
	}

	@SuppressWarnings("ConstantConditions")
	@SideOnly(Side.CLIENT)
	public int getColor(ItemStack stack) {
		if (stack.hasTagCompound()) {
			NBTTagCompound tag = stack.getTagCompound();
			if (tag.hasKey("color")) {
				return tag.getInteger("color");
			}
			if (tag.hasKey(DYE_COLOR_TAG)) {
				return EnumDyeColor.byDyeDamage(tag.getInteger(DYE_COLOR_TAG)).getColorValue();
			}
		}
		return -1;
	}

	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
		if (this.isInCreativeTab(tab)) {
			for (int dyeDamage = 0; dyeDamage < 16; ++dyeDamage) {
				ItemStack item = new ItemStack(this, 1);
				item.setTagInfo(DYE_COLOR_TAG, new NBTTagInt(dyeDamage));
				items.add(item);
			}
		}
	}

	@SuppressWarnings("ConstantConditions")
	@Override
	public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, IBlockState newState) {
		boolean result = super.placeBlockAt(stack, player, world, pos, side, hitX, hitY, hitZ, newState);

		if (world.getBlockState(pos).getBlock() == block) {
			WorldTools.getTile(world, pos, TileColored.class).ifPresent(t -> {
				if (stack.hasTagCompound()) {
					NBTTagCompound tag = stack.getTagCompound();
					if (tag.hasKey(DYE_COLOR_TAG)) {
						t.setDyeColor(tag.getInteger(DYE_COLOR_TAG));
					}
				}
			});
		}
		return result;
	}
}
