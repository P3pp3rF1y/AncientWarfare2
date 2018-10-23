package net.shadowmage.ancientwarfare.structure.template.plugin.defaultplugins.blockrules;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityFlowerPot;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.shadowmage.ancientwarfare.core.util.WorldTools;
import net.shadowmage.ancientwarfare.structure.api.IStructureBuilder;
import net.shadowmage.ancientwarfare.structure.api.TemplateParsingException;

import java.util.List;

public class TemplateRuleFlowerPot extends TemplateRuleVanillaBlocks {
	public static final String PLUGIN_NAME = "vanillaFlowerPot";
	private static final String ITEM_NAME_TAG = "itemName";
	private Item item = Items.AIR;
	private int itemMeta = 0;

	public TemplateRuleFlowerPot(World world, BlockPos pos, IBlockState state, int turns) {
		super(world, pos, state, turns);
		WorldTools.getTile(world, pos, TileEntityFlowerPot.class).ifPresent(t -> {
			Item item = t.getFlowerPotItem();
			itemMeta = t.getFlowerPotData();
			if (item != null) {
				//noinspection ConstantConditions - item must be registered to be in the flower pot
				this.item = item;
			}
		});
	}

	public TemplateRuleFlowerPot(int ruleNumber, List<String> lines) throws TemplateParsingException.TemplateRuleParsingException {
		super(ruleNumber, lines);
	}

	@Override
	protected ItemStack getStack() {
		return new ItemStack(Items.FLOWER_POT);
	}

	@Override
	public void addResources(NonNullList<ItemStack> resources) {
		super.addResources(resources);
		if (item != Items.AIR) {
			resources.add(new ItemStack(item, 1, itemMeta));
		}
	}

	@Override
	public boolean shouldReuseRule(World world, IBlockState state, int turns, BlockPos pos) {
		return false;
	}

	@Override
	public void handlePlacement(World world, int turns, BlockPos pos, IStructureBuilder builder) {
		super.handlePlacement(world, turns, pos, builder);
		if (item != Items.AIR) {
			WorldTools.getTile(world, pos, TileEntityFlowerPot.class).ifPresent(t -> t.setItemStack(new ItemStack(item, 1, itemMeta)));
		}
	}

	@Override
	public void writeRuleData(NBTTagCompound tag) {
		super.writeRuleData(tag);
		if (item != Items.AIR) {
			//noinspection ConstantConditions
			tag.setString(ITEM_NAME_TAG, item.getRegistryName().toString());
		}
		tag.setInteger("itemMeta", itemMeta);
	}

	@Override
	public void parseRuleData(NBTTagCompound tag) {
		super.parseRuleData(tag);
		if (tag.hasKey(ITEM_NAME_TAG)) {
			ResourceLocation registryName = new ResourceLocation(tag.getString(ITEM_NAME_TAG));
			if (ForgeRegistries.ITEMS.containsKey(registryName)) {
				item = ForgeRegistries.ITEMS.getValue(registryName);
			}
		}
		itemMeta = tag.getInteger("itemMeta");
	}

	@Override
	protected String getPluginName() {
		return PLUGIN_NAME;
	}
}
