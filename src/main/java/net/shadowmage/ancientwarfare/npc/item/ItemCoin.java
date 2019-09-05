package net.shadowmage.ancientwarfare.npc.item;

import com.google.common.collect.ImmutableMap;
import net.minecraft.block.Block;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.shadowmage.ancientwarfare.npc.init.AWNPCItems;
import net.shadowmage.ancientwarfare.structure.init.AWStructureBlocks;
import net.shadowmage.ancientwarfare.structure.init.AWStructureSounds;

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
	public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		ItemStack stack = player.getHeldItem(hand);
		String metal = getMetal(stack).toString().toLowerCase();

		Map<String, Block> metalsToBlocks = ImmutableMap.of(
		"ancient", AWStructureBlocks.COIN_STACK_ANCIENT,
		"gold", AWStructureBlocks.COIN_STACK_GOLD,
		"silver", AWStructureBlocks.COIN_STACK_SILVER,
		"copper", AWStructureBlocks.COIN_STACK_COPPER
		);

		if ((stack.getCount() < 8)) {
			return EnumActionResult.FAIL;
		}
		if (worldIn.getBlockState(pos).getBlock().isReplaceable(worldIn, pos)) {
			worldIn.setBlockState(pos, (metalsToBlocks.get(metal)).getDefaultState());
		} else if (facing == EnumFacing.UP && worldIn.getBlockState(pos.up()).getBlock().isReplaceable(worldIn, pos)) {
			worldIn.setBlockState(pos.up(), (metalsToBlocks.get(metal)).getDefaultState());
		} else {
			return EnumActionResult.FAIL;
		}
			stack.shrink(8);
			worldIn.playSound(null, pos, AWStructureSounds.COIN_STACK_INTERACT, SoundCategory.PLAYERS, 0.5f, 1);
			return EnumActionResult.SUCCESS;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		if (getMetal(stack).equals("ancient"))  {
			tooltip.add(I18n.format("item.coin.ancient.tooltip", TextFormatting.DARK_AQUA.toString() + TextFormatting.ITALIC.toString()));
		}
	}
}
