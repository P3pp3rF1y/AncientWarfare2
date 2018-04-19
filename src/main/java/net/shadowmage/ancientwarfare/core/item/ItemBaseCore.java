package net.shadowmage.ancientwarfare.core.item;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.shadowmage.ancientwarfare.core.AncientWarfareCore;
import net.shadowmage.ancientwarfare.core.block.AWCoreBlockLoader;
import net.shadowmage.ancientwarfare.core.proxy.IClientRegistrar;
import net.shadowmage.ancientwarfare.core.util.ModelLoaderHelper;

public abstract class ItemBaseCore extends ItemBase implements IClientRegistrar {
	public ItemBaseCore(String regName) {
		super(AncientWarfareCore.modID, regName);
		setCreativeTab(AWCoreBlockLoader.coreTab);

		AncientWarfareCore.proxy.addClientRegistrar(this);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerClient() {
		ModelLoaderHelper.registerItem(this, "core");
	}
}
