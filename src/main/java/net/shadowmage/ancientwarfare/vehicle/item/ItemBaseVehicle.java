package net.shadowmage.ancientwarfare.vehicle.item;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.shadowmage.ancientwarfare.core.item.ItemBase;
import net.shadowmage.ancientwarfare.core.proxy.IClientRegister;
import net.shadowmage.ancientwarfare.core.util.ModelLoaderHelper;
import net.shadowmage.ancientwarfare.npc.AncientWarfareNPC;
import net.shadowmage.ancientwarfare.vehicle.AncientWarfareVehicles;

public abstract class ItemBaseVehicle extends ItemBase implements IClientRegister {
	public ItemBaseVehicle(String regName) {
		super(AncientWarfareVehicles.modID, regName);
		setCreativeTab(AWVehicleItemLoader.vehicleTab);

		AncientWarfareNPC.proxy.addClientRegistrar(this);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerClient() {
		ModelLoaderHelper.registerItem(this, "vehicle");
	}
}
