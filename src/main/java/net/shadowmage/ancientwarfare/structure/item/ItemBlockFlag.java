package net.shadowmage.ancientwarfare.structure.item;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.translation.I18n;
import net.shadowmage.ancientwarfare.core.item.ItemBlockBase;
import org.apache.commons.lang3.StringUtils;

public class ItemBlockFlag extends ItemBlockBase {
	public ItemBlockFlag(Block block) {
		super(block);
	}

	@Override
	public String getItemStackDisplayName(ItemStack stack) {
		String name = "";
		if (stack.hasTagCompound()) {
			//noinspection ConstantConditions
			name = StringUtils.capitalize(stack.getTagCompound().getString("name"));
		}

		return I18n.translateToLocalFormatted(getUnlocalizedNameInefficiently(stack) + ".name", name).trim();
	}
}
