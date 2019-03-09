package net.shadowmage.ancientwarfare.npc.item;

import net.minecraft.item.ItemFood;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.shadowmage.ancientwarfare.core.proxy.IClientRegister;
import net.shadowmage.ancientwarfare.core.util.ModelLoaderHelper;
import net.shadowmage.ancientwarfare.npc.AncientWarfareNPC;

public class ItemFoodBundle extends ItemFood implements IClientRegister {
	public ItemFoodBundle() {
		super(15, 1, true);

		setUnlocalizedName("food_bundle");
		setRegistryName(new ResourceLocation(AncientWarfareNPC.MOD_ID, "food_bundle"));
		setCreativeTab(AncientWarfareNPC.TAB);
		AncientWarfareNPC.proxy.addClientRegister(this);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerClient() {
		ModelLoaderHelper.registerItem(this, "npc");
	}
}
