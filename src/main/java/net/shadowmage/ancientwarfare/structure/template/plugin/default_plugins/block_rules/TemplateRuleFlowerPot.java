package net.shadowmage.ancientwarfare.structure.template.plugin.default_plugins.block_rules;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityFlowerPot;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.shadowmage.ancientwarfare.core.util.WorldTools;
import net.shadowmage.ancientwarfare.structure.api.IStructureBuilder;

public class TemplateRuleFlowerPot extends TemplateRuleVanillaBlocks {
	private static final String ITEM_NAME_TAG = "itemName";
	private String itemName;
	private int itemMeta;

	public TemplateRuleFlowerPot(World world, BlockPos pos, Block block, int meta, int turns) {
		super(world, pos, block, meta, turns);
		WorldTools.getTile(world, pos, TileEntityFlowerPot.class).ifPresent(t -> {
			Item item = t.getFlowerPotItem();
			itemMeta = t.getFlowerPotData();
			if (item != null) {
				itemName = item.getRegistryName().toString();
			}
		});
	}

	public TemplateRuleFlowerPot() {
	}

	@Override
	public boolean shouldReuseRule(World world, Block block, int meta, int turns, BlockPos pos) {
		return false;
	}

	@Override
	public void handlePlacement(World world, int turns, BlockPos pos, IStructureBuilder builder) {
		super.handlePlacement(world, turns, pos, builder);
		if (itemName != null) {
			Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(itemName));
			//noinspection ConstantConditions
			WorldTools.getTile(world, pos, TileEntityFlowerPot.class).ifPresent(t -> t.setItemStack(new ItemStack(item, 1, itemMeta)));
		}
	}

	@Override
	public void writeRuleData(NBTTagCompound tag) {
		super.writeRuleData(tag);
		if (itemName != null) {
			tag.setString(ITEM_NAME_TAG, itemName);
		}
		tag.setInteger("itemMeta", itemMeta);
	}

	@Override
	public void parseRuleData(NBTTagCompound tag) {
		super.parseRuleData(tag);
		if (tag.hasKey(ITEM_NAME_TAG)) {
			itemName = tag.getString(ITEM_NAME_TAG);
		}
		itemMeta = tag.getInteger("itemMeta");
	}
}
