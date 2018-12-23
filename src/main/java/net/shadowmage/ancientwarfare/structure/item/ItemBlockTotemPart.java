package net.shadowmage.ancientwarfare.structure.item;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.shadowmage.ancientwarfare.core.item.ItemBlockBase;
import net.shadowmage.ancientwarfare.structure.block.BlockTotemPart;

public class ItemBlockTotemPart extends ItemBlockBase {
	public ItemBlockTotemPart(Block block) {
		super(block);
	}

	public static BlockTotemPart.Variant getVariant(ItemStack stack) {
		if (stack.hasTagCompound()) {
			//noinspection ConstantConditions
			return BlockTotemPart.Variant.fromId(stack.getTagCompound().getByte("variant"));
		}
		return BlockTotemPart.Variant.BASE;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean canPlaceBlockOnSide(World world, BlockPos pos, EnumFacing side, EntityPlayer player, ItemStack stack) {
		return getVariant(stack).canPlace(world, pos.offset(side), player);
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {
		if (!stack.hasTagCompound()) {
			return super.getUnlocalizedName(stack);
		}

		//noinspection ConstantConditions
		return String.format("%s.%s", super.getUnlocalizedName(stack),
				BlockTotemPart.Variant.fromId(stack.getTagCompound().getByte("variant")).name().toLowerCase());
	}
}
