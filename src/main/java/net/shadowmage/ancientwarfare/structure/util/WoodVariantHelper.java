package net.shadowmage.ancientwarfare.structure.util;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.shadowmage.ancientwarfare.core.AncientWarfareCore;
import net.shadowmage.ancientwarfare.structure.block.WoodVariant;
import net.shadowmage.ancientwarfare.structure.item.WoodItemBlock;

import java.util.Locale;
import java.util.function.UnaryOperator;

public class WoodVariantHelper {
	private WoodVariantHelper() {}

	private static final String VARIANT_TAG = "variant";

	public static void getSubBlocks(Block block, NonNullList<ItemStack> items) {
		for (WoodVariant variant : WoodVariant.values()) {
			ItemStack stack = new ItemStack(block);
			setStackVariant(variant, stack);
			items.add(stack);
		}
	}

	private static void setStackVariant(WoodVariant variant, ItemStack stack) {
		stack.setTagInfo(VARIANT_TAG, new NBTTagString(variant.getName()));
	}

	public static WoodVariant getVariant(ItemStack stack) {
		//noinspection ConstantConditions
		return stack.hasTagCompound() && stack.getTagCompound().hasKey(VARIANT_TAG) ? WoodVariant.byName(stack.getTagCompound().getString(VARIANT_TAG)) : WoodVariant.OAK;
	}

	public static ItemStack getPickBlock(Block block, IBlockState state) {
		ItemStack stack = new ItemStack(block);
		setStackVariant(state.getValue(BlockStateProperties.VARIANT), stack);
		return stack;
	}

	public static void getDrops(Block block, NonNullList<ItemStack> drops, IBlockState state) {
		ItemStack drop = new ItemStack(block);
		setStackVariant(state.getValue(BlockStateProperties.VARIANT), drop);
		drops.add(drop);
	}

	@SideOnly(Side.CLIENT)
	public static void registerClient(Block block) {
		registerClient(block, propString -> propString);
	}

	@SideOnly(Side.CLIENT)
	public static void registerClient(Block block, UnaryOperator<String> updatePropertyString) {
		//noinspection ConstantConditions
		ResourceLocation baseLocation = new ResourceLocation(AncientWarfareCore.MOD_ID, "structure/" + block.getRegistryName().getResourcePath());

		ModelLoader.setCustomStateMapper(block, new StateMapperBase() {
			@Override
			@SideOnly(Side.CLIENT)
			protected ModelResourceLocation getModelResourceLocation(IBlockState state) {
				return new ModelResourceLocation(baseLocation, getPropertyString(state.getProperties()));
			}
		});

		String modelPropString = updatePropertyString.apply("variant=%s");

		ModelLoader.setCustomMeshDefinition(WoodItemBlock.getItemFromBlock(block), stack -> {
			if (!stack.hasTagCompound()) {
				return new ModelResourceLocation(baseLocation, String.format(modelPropString, WoodVariant.OAK.getName().toLowerCase(Locale.ENGLISH)));
			}
			WoodVariant variant = getVariant(stack);
			return new ModelResourceLocation(baseLocation, String.format(modelPropString, variant.getName().toLowerCase(Locale.ENGLISH)));
		});

		for (WoodVariant variant : WoodVariant.values()) {
			ModelLoader.registerItemVariants(WoodItemBlock.getItemFromBlock(block),
					new ModelResourceLocation(baseLocation, String.format(modelPropString, variant.getName().toLowerCase(Locale.ENGLISH))));
		}
	}
}
