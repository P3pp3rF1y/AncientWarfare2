package net.shadowmage.ancientwarfare.automation.item;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.shadowmage.ancientwarfare.core.item.ItemBlockBase;

public class ItemBlockWarehouseStorage extends ItemBlockBase {

	public ItemBlockWarehouseStorage(Block block) {
		super(block);
		this.setHasSubtypes(true);
	}

	@Override
	public String getUnlocalizedName(ItemStack par1ItemStack) {
		return super.getUnlocalizedName() + "." + par1ItemStack.getItemDamage();
	}

	@Override
	public int getMetadata(int par1) {
		return par1;
	}

}
