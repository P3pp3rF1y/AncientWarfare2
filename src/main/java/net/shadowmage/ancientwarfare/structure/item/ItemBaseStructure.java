package net.shadowmage.ancientwarfare.structure.item;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.shadowmage.ancientwarfare.core.item.ItemBase;
import net.shadowmage.ancientwarfare.core.proxy.IClientRegister;
import net.shadowmage.ancientwarfare.core.util.ModelLoaderHelper;
import net.shadowmage.ancientwarfare.structure.AncientWarfareStructure;

public class ItemBaseStructure extends ItemBase implements IClientRegister {
	public ItemBaseStructure(String regName) {
		super(AncientWarfareStructure.MOD_ID, regName);
		setCreativeTab(AncientWarfareStructure.TAB);

		AncientWarfareStructure.proxy.addClientRegister(this);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerClient() {
		ModelLoaderHelper.registerItem(this, "structure");
	}
}
