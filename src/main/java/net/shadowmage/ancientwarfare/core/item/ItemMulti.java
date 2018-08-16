package net.shadowmage.ancientwarfare.core.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;
import net.shadowmage.ancientwarfare.core.proxy.CommonProxyBase;
import net.shadowmage.ancientwarfare.core.proxy.IClientRegister;
import net.shadowmage.ancientwarfare.core.util.ModelLoaderHelper;

import java.util.HashMap;
import java.util.Map;

/*
 * Handle subtypes through ItemStack damage values
 */
public class ItemMulti extends ItemBase implements IClientRegister {

	private final HashMap<Integer, String> subItems = new HashMap<>();

	public ItemMulti(String modID, String regName) {
		super(modID, regName);
		this.setHasSubtypes(true);
	}

	@Override
	public boolean getShareTag() {
		return false;
	}

	@Override
	public String getUnlocalizedName(ItemStack par1ItemStack) {
		return super.getUnlocalizedName(par1ItemStack) + "." + par1ItemStack.getItemDamage();
	}

	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
		if (!isInCreativeTab(tab)) {
			return;
		}

		for (Integer num : subItems.keySet()) {
			items.add(new ItemStack(this, 1, num));
		}
	}

	public void addSubItem(int num, String modelName) {
		if (!subItems.containsKey(num))
			subItems.put(num, modelName);
	}

	public void addSubItem(int num, String modelName, String ore) {
		addSubItem(num, modelName);
		OreDictionary.registerOre(ore, new ItemStack(this, 1, num));
	}

	public ItemStack getSubItem(int num) {
		return new ItemStack(this, 1, num);
	}

	public ItemMulti listenToProxy(CommonProxyBase proxy) {
		proxy.addClientRegister(this);

		return this;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerClient() {
		for (Map.Entry<Integer, String> entry : subItems.entrySet()) {
			ModelLoaderHelper.registerItem(this, entry.getKey(), entry.getValue());
		}
	}
}
