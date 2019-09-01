package net.shadowmage.ancientwarfare.npc.item;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.shadowmage.ancientwarfare.npc.init.AWNPCItems;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemCoin extends ItemBaseNPC {
	public ItemCoin() {
		super("coin");
	}

	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
		if (!isInCreativeTab(tab)) {
			return;
		}
		Arrays.stream(CoinMetal.values()).forEach(metal -> {
			ItemStack subItem = getCoinStack(metal);
					items.add(subItem);
				}
		);
	}

	private static ItemStack getCoinStack(CoinMetal metal) {
		return getCoinStack(metal, 1);
	}

	public static ItemStack getCoinStack(CoinMetal metal, int stackSize) {
		ItemStack coinStack = new ItemStack(AWNPCItems.COIN);
		coinStack.setTagInfo("metal", new NBTTagString(metal.getName()));
		coinStack.setCount(stackSize);
		return coinStack;
	}

	public static boolean isSpecificCoin(ItemStack stack, CoinMetal coinMetal) {
		return stack.getItem() == AWNPCItems.COIN && getMetal(stack) == coinMetal;
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {
		return super.getUnlocalizedName(stack) + "." + getMetalName(stack);
	}

	public static CoinMetal getMetal(ItemStack stack) {
		if (!stack.hasTagCompound()) {
			return CoinMetal.COPPER;
		}
		return CoinMetal.byName(getMetalName(stack));
	}

	private static String getMetalName(ItemStack stack) {
		//noinspection ConstantConditions
		return stack.hasTagCompound() ? stack.getTagCompound().getString("metal") : "";
	}

	public enum CoinMetal {
		ANCIENT("ancient", 0x445948),
		GOLD("gold", 0xFFD700),
		SILVER("silver", 0xC0C0C0),
		COPPER("copper", 0xB87333);

		private String name;
		private int color;

		CoinMetal(String name, int color) {
			this.name = name;
			this.color = color;
		}

		public String getName() {
			return name;
		}

		private static Map<String, CoinMetal> values = new HashMap<>();

		static {
			Arrays.stream(values()).forEach(m -> values.put(m.getName(), m));
		}

		public static CoinMetal byName(String name) {
			return values.get(name);
		}

		public int getColor() {
			return color;
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		if (getMetal(stack).equals("ancient"))  {
			tooltip.add(I18n.format("item.coin.ancient.tooltip", TextFormatting.DARK_AQUA.toString() + TextFormatting.ITALIC.toString()));
		}
	}
}
