package net.shadowmage.ancientwarfare.npc.item;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.shadowmage.ancientwarfare.core.item.ItemBase;
import net.shadowmage.ancientwarfare.core.proxy.IClientRegister;
import net.shadowmage.ancientwarfare.core.util.ModelLoaderHelper;
import net.shadowmage.ancientwarfare.npc.AncientWarfareNPC;

public abstract class ItemBaseNPC extends ItemBase implements IClientRegister {
	public ItemBaseNPC(String regName) {
		super(AncientWarfareNPC.MOD_ID, regName);
		setCreativeTab(AncientWarfareNPC.TAB);

		AncientWarfareNPC.proxy.addClientRegister(this);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerClient() {
		ModelLoaderHelper.registerItem(this, "npc");
	}
}
