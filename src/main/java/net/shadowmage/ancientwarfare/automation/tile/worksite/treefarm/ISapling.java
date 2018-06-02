package net.shadowmage.ancientwarfare.automation.tile.worksite.treefarm;

import net.minecraft.item.ItemStack;

public interface ISapling {
	boolean matches(ItemStack stack);

	boolean isRightClick();
}
