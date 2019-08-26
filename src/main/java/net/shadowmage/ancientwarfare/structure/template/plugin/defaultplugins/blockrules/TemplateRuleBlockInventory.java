package net.shadowmage.ancientwarfare.structure.template.plugin.defaultplugins.blockrules;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.shadowmage.ancientwarfare.core.util.BlockTools;
import net.shadowmage.ancientwarfare.core.util.InventoryTools;
import net.shadowmage.ancientwarfare.core.util.WorldTools;
import net.shadowmage.ancientwarfare.structure.api.IStructureBuilder;
import net.shadowmage.ancientwarfare.structure.tile.ISpecialLootContainer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TemplateRuleBlockInventory extends TemplateRuleBlockTile {

	private static final String INVENTORY_DATA_TAG = "inventoryData";
	public static final String PLUGIN_NAME = "inventory";
	private static final String SIDED_INVENTORIES_TAG = "sidedInventories";
	private static final String LEGACY_FEATURES_TAG = "legacyFeatures";
	private boolean legacyFeatures;
	private int randomLootLevel;
	private Map<EnumFacing, NonNullList<ItemStack>> inventoryStacks = new HashMap<>();

	public TemplateRuleBlockInventory() {
		super();
	}

	public TemplateRuleBlockInventory(World world, BlockPos pos, IBlockState state, int turns) {
		this(world, pos, state, turns, new EnumFacing[] {null}, false);
	}

	public TemplateRuleBlockInventory(World world, BlockPos pos, IBlockState state, int turns, EnumFacing[] sides, boolean legacyFeatures) {
		super(world, pos, state, turns);
		this.legacyFeatures = legacyFeatures;
		WorldTools.getTile(world, pos, TileEntity.class)
				.ifPresent(te -> {
					if (te instanceof ISpecialLootContainer && ((ISpecialLootContainer) te).getLootSettings().hasLootToSpawn()) {
						return;
					}

					if (te instanceof TileEntityChest) {
						putInInventoryStacks(null, InventoryTools.getItems(((TileEntityChest) te).getSingleChestHandler()));
						setLegacyRandomLoot(legacyFeatures, null);
					} else {
						for (EnumFacing side : sides) {
							InventoryTools.getItemHandlerFrom(te, side).ifPresent(itemHandler -> {
								putInInventoryStacks(side, InventoryTools.getItems(itemHandler));
								setLegacyRandomLoot(legacyFeatures, side);
							});
						}
					}
				});
	}

	private void setLegacyRandomLoot(boolean legacyFeatures, @Nullable EnumFacing side) {
		if (legacyFeatures && side == null && inventoryStacks.get(null).size() == 1) {
			ItemStack keyStack = inventoryStacks.get(null).get(0);
			if (keyStack.getItem() == Items.GOLD_INGOT) {
				this.randomLootLevel = 1;
			} else if (keyStack.getItem() == Items.DIAMOND) {
				this.randomLootLevel = 2;
			} else if (keyStack.getItem() == Items.EMERALD) {
				this.randomLootLevel = 3;
			}
		}
	}

	@Override
	public List<ItemStack> getResources() {
		List<ItemStack> resources = new ArrayList<>(super.getResources());

		for (NonNullList<ItemStack> stacks : inventoryStacks.values()) {
			resources.addAll(stacks);
		}

		return resources;
	}

	@Override
	public void handlePlacement(World world, int turns, BlockPos pos, IStructureBuilder builder) {
		super.handlePlacement(world, turns, pos, builder);
		if (randomLootLevel > 0) {
			for (Map.Entry<EnumFacing, NonNullList<ItemStack>> inventoryEntry : inventoryStacks.entrySet()) {
				WorldTools.getItemHandlerFromTile(world, pos, inventoryEntry.getKey()).ifPresent(itemHandler -> {
					InventoryTools.emptyInventory(itemHandler);
					InventoryTools.generateLootFor(world, itemHandler, world.rand, randomLootLevel);
				});
			}
		} else if (legacyFeatures && inventoryStacks.containsKey(null) && !tag.hasKey("Items")) {
			WorldTools.getItemHandlerFromTile(world, pos, null)
					.ifPresent(itemHandler -> InventoryTools.insertItems(itemHandler, inventoryStacks.get(null), false));
		}
		BlockTools.notifyBlockUpdate(world, pos);
	}

	@Override
	public boolean shouldReuseRule(World world, IBlockState state, int turns, BlockPos pos) {
		return false;
	}

	@Override
	public void writeRuleData(NBTTagCompound tag) {
		super.writeRuleData(tag);
		tag.setInteger("lootLevel", randomLootLevel);
		tag.setBoolean(LEGACY_FEATURES_TAG, legacyFeatures);

		for (EnumFacing side : inventoryStacks.keySet()) {
			if (side == null) {
				writeInventoryForSide(tag, side);
			} else {
				if (!tag.hasKey(SIDED_INVENTORIES_TAG)) {
					tag.setTag(SIDED_INVENTORIES_TAG, new NBTTagCompound());
				}
				NBTTagCompound sidedTag = tag.getCompoundTag(SIDED_INVENTORIES_TAG);
				NBTTagCompound sideTag = new NBTTagCompound();
				writeInventoryForSide(sideTag, side);
				sidedTag.setTag(side.getName(), sideTag);
			}
		}
	}

	private void writeInventoryForSide(NBTTagCompound tag, @Nullable EnumFacing side) {
		NonNullList<ItemStack> stacks = inventoryStacks.get(side);
		NBTTagCompound invData = new NBTTagCompound();
		invData.setInteger("length", stacks.size());
		NBTTagCompound itemTag;
		NBTTagList list = new NBTTagList();
		@Nonnull ItemStack stack;
		for (int i = 0; i < stacks.size(); i++) {
			stack = stacks.get(i);
			if (stack.isEmpty()) {
				continue;
			}
			itemTag = stack.writeToNBT(new NBTTagCompound());
			itemTag.setInteger("slot", i);
			list.appendTag(itemTag);
		}
		invData.setTag("inventoryContents", list);
		tag.setTag(INVENTORY_DATA_TAG, invData);
	}

	@Override
	public void parseRule(NBTTagCompound tag) {
		super.parseRule(tag);
		if (tag.hasKey(INVENTORY_DATA_TAG)) {
			parseInventoryForSide(tag, null);
		}
		if (tag.hasKey(SIDED_INVENTORIES_TAG)) {
			NBTTagCompound sidedTag = tag.getCompoundTag(SIDED_INVENTORIES_TAG);
			for (String key : sidedTag.getKeySet()) {
				EnumFacing side = EnumFacing.byName(key);
				if (side != null) {
					parseInventoryForSide(sidedTag.getCompoundTag(key), side);
				}
			}
		}
		randomLootLevel = tag.getInteger("lootLevel");
		legacyFeatures = !tag.hasKey(LEGACY_FEATURES_TAG) || tag.getBoolean(LEGACY_FEATURES_TAG);
	}

	private void parseInventoryForSide(NBTTagCompound tag, @Nullable EnumFacing side) {
		NBTTagCompound inventoryTag = tag.getCompoundTag(INVENTORY_DATA_TAG);
		int length = inventoryTag.getInteger("length");
		NonNullList<ItemStack> stacks = NonNullList.withSize(length, ItemStack.EMPTY);
		NBTTagCompound itemTag;
		NBTTagList list = inventoryTag.getTagList("inventoryContents", Constants.NBT.TAG_COMPOUND);
		int slot;
		@Nonnull ItemStack stack;
		for (int i = 0; i < list.tagCount(); i++) {
			itemTag = list.getCompoundTagAt(i);
			stack = new ItemStack(itemTag);
			if (!stack.isEmpty()) {
				slot = itemTag.getInteger("slot");
				stacks.set(slot, stack);
			}
		}
		putInInventoryStacks(side, stacks);
	}

	@SuppressWarnings("squid:S1640") //need to use a null key as well which is not supported in EnumMap
	private void putInInventoryStacks(@Nullable EnumFacing side, NonNullList<ItemStack> stacks) {
		inventoryStacks.put(side, stacks);
	}

	@Override
	public String getPluginName() {
		return PLUGIN_NAME;
	}
}
