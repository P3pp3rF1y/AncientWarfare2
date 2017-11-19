package net.shadowmage.ancientwarfare.structure.item;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.shadowmage.ancientwarfare.core.item.ItemBase;
import net.shadowmage.ancientwarfare.core.proxy.IClientRegistrar;
import net.shadowmage.ancientwarfare.core.util.ModelLoaderHelper;
import net.shadowmage.ancientwarfare.structure.AncientWarfareStructures;

public class ItemBaseStructure extends ItemBase implements IClientRegistrar {
	public ItemBaseStructure(String regName) {
		super(AncientWarfareStructures.modID, regName);
		setCreativeTab(AWStructuresItemLoader.structureTab);

		AncientWarfareStructures.proxy.addClientRegistrar(this);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerClient() {
		ModelLoaderHelper.registerItem(this, "structure");
	}
}
