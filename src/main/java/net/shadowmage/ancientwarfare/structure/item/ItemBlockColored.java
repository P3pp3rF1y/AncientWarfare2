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
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.shadowmage.ancientwarfare.core.item.ItemBlockBase;
import net.shadowmage.ancientwarfare.core.util.WorldTools;
import net.shadowmage.ancientwarfare.structure.tile.TileColored;
import org.apache.commons.lang3.StringUtils;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class ItemBlockColored extends ItemBlockBase {
	private static final String DYE_COLOR_TAG = "dyeColor";
	private static final int WHITE = 16383998;
	private static final String COLOR_TAG = "color";
	private static final String CUSTOM_DATA_TAG = "customData";
	private final Set<NBTTagCompound> customItemTags = new HashSet<>();

	public ItemBlockColored(Block block) {
		super(block);
	}

	@SuppressWarnings("ConstantConditions")
	@SideOnly(Side.CLIENT)
	public int getColor(ItemStack stack) {
		if (stack.hasTagCompound()) {
			NBTTagCompound tag = stack.getTagCompound();
			if (tag.hasKey(COLOR_TAG)) {
				return tag.getInteger(COLOR_TAG);
			}
			Optional<EnumDyeColor> dyeColor = getDyeColor(stack);
			if (dyeColor.isPresent()) {
				return dyeColor.get().getColorValue();
			}
		}
		return WHITE;
	}

	public void addCustomItemTag(NBTTagCompound tag) {
		customItemTags.add(tag);
	}

	@SuppressWarnings("ConstantConditions")
	private Optional<EnumDyeColor> getDyeColor(ItemStack stack) {
		if (stack.hasTagCompound() && stack.getTagCompound().hasKey(DYE_COLOR_TAG)) {
			return Optional.of(EnumDyeColor.byDyeDamage(stack.getTagCompound().getInteger(DYE_COLOR_TAG)));
		}

		return Optional.empty();
	}

	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
		if (isInCreativeTab(tab)) {
			for (int dyeDamage = 0; dyeDamage < 16; ++dyeDamage) {
				ItemStack item = new ItemStack(this, 1);
				item.setTagInfo(DYE_COLOR_TAG, new NBTTagInt(dyeDamage));
				items.add(item);
			}

			for (NBTTagCompound tag : customItemTags) {
				ItemStack item = new ItemStack(this, 1);
				item.setTagCompound(tag);
				items.add(item);
			}
		}
	}

	@SuppressWarnings("ConstantConditions")
	@Override
	public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, IBlockState newState) {
		boolean result = super.placeBlockAt(stack, player, world, pos, side, hitX, hitY, hitZ, newState);

		if (world.getBlockState(pos).getBlock() == block) {
			WorldTools.getTile(world, pos, TileColored.class).ifPresent(t -> t.setFromStack(stack));
		}
		return result;
	}

	@Override
	public String getItemStackDisplayName(ItemStack stack) {
		if (!stack.hasTagCompound()) {
			return getUnlocalizedNameInefficiently(stack);
		}

		NBTTagCompound tag = stack.getTagCompound();

		if (tag.hasKey(CUSTOM_DATA_TAG)) {
			return I18n.translateToLocalFormatted((getUnlocalizedNameInefficiently(stack) + "." + tag.getString("unlocalizedNamePart") + ".name").trim(),
					StringUtils.capitalize(tag.getString(CUSTOM_DATA_TAG)));
		}

		String color = I18n.translateToLocal(getDyeColor(stack).map(EnumDyeColor::getUnlocalizedName).orElse("white"));
		return I18n.translateToLocalFormatted((getUnlocalizedNameInefficiently(stack) + ".name").trim(), color);
	}

}
