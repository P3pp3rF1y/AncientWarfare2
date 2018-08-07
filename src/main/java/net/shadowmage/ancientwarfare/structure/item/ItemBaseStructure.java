package net.shadowmage.ancientwarfare.structure.item;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.shadowmage.ancientwarfare.core.item.ItemBase;
import net.shadowmage.ancientwarfare.core.proxy.IClientRegister;
import net.shadowmage.ancientwarfare.core.util.ModelLoaderHelper;
import net.shadowmage.ancientwarfare.structure.AncientWarfareStructures;

public class ItemBaseStructure extends ItemBase implements IClientRegister {
	public ItemBaseStructure(String regName) {
		super(AncientWarfareStructures.MOD_ID, regName);
		setCreativeTab(AWStructuresItemLoader.structureTab);

		AncientWarfareStructures.proxy.addClientRegistrer(this);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerClient() {
		ModelLoaderHelper.registerItem(this, "structure");
	}
}
