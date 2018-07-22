package net.shadowmage.ancientwarfare.structure.template.plugin.default_plugins.block_rules;

import net.minecraft.block.Block;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.shadowmage.ancientwarfare.core.util.BlockTools;
import net.shadowmage.ancientwarfare.core.util.InventoryTools;
import net.shadowmage.ancientwarfare.structure.api.IStructureBuilder;
import net.shadowmage.ancientwarfare.structure.block.BlockDataManager;

import javax.annotation.Nonnull;

public class TemplateRuleBlockInventory extends TemplateRuleVanillaBlocks {

	public static final String INVENTORY_DATA_TAG = "inventoryData";
	public int randomLootLevel = 0;
	public NBTTagCompound tag = new NBTTagCompound();
	private NonNullList<ItemStack> inventoryStacks;

	public TemplateRuleBlockInventory(World world, BlockPos pos, Block block, int meta, int turns) {
		super(world, pos, block, meta, turns);
		TileEntity te = world.getTileEntity(pos);
		if (te instanceof IInventory) {
			IInventory inventory = (IInventory) te;
			if (inventory.getSizeInventory() <= 0) {
				return;
			}
			@Nonnull ItemStack keyStack = inventory.getStackInSlot(0);
			boolean useKey = !keyStack.isEmpty() && (keyStack.getItem() == Items.GOLD_INGOT || keyStack.getItem() == Items.DIAMOND || keyStack.getItem() == Items.EMERALD);
			if (useKey) {
				for (int i = 1; i < inventory.getSizeInventory(); i++) {
					if (!inventory.getStackInSlot(i).isEmpty()) {
						useKey = false;
						break;
					}
				}
			}
			this.randomLootLevel = useKey ? keyStack.getItem() == Items.GOLD_INGOT ? 1 : keyStack.getItem() == Items.DIAMOND ? 2 : 3 : 0;
			inventoryStacks = NonNullList.withSize(inventory.getSizeInventory(), ItemStack.EMPTY);
			@Nonnull ItemStack stack;
			for (int i = 0; i < inventory.getSizeInventory(); i++) {
				stack = inventory.getStackInSlot(i);
				inventory.setInventorySlotContents(i, ItemStack.EMPTY);
				inventoryStacks.set(i, stack.isEmpty() ? ItemStack.EMPTY : stack.copy());
			}
			te.writeToNBT(tag);
			for (int i = 0; i < inventory.getSizeInventory(); i++) {
				inventory.setInventorySlotContents(i, inventoryStacks.get(i));
			}
			//actual items were already removed from tag in previous for loop blocks prior to tile writing to nbt
			tag.removeTag("Items");//remove vanilla inventory tag from tile-entities (need to custom handle AW inventoried blocks still)
		}
	}

	public TemplateRuleBlockInventory() {

	}

	@Override
	public void handlePlacement(World world, int turns, BlockPos pos, IStructureBuilder builder) {
		super.handlePlacement(world, turns, pos, builder);
		int localMeta = BlockDataManager.INSTANCE.getRotatedMeta(block, this.meta, turns);
		world.setBlockState(pos, block.getStateFromMeta(localMeta), 3);
		TileEntity te = world.getTileEntity(pos);
		if (!(te instanceof IInventory)) {
			return;
		}
		IInventory inventory = (IInventory) te;
		//TODO look into changing this so that the whole TE doesn't need reloading from custom NBT
		tag.setString("id", block.getRegistryName().toString());
		tag.setInteger("x", pos.getX());
		tag.setInteger("y", pos.getY());
		tag.setInteger("z", pos.getZ());
		te.readFromNBT(tag);
		if (randomLootLevel > 0) {
			inventory.clear(); //clear the inventory in prep for random loot stuff
			InventoryTools.generateLootFor(world, inventory, world.rand, randomLootLevel);
		} else if (inventoryStacks != null) {
			@Nonnull ItemStack stack;
			for (int i = 0; i < inventory.getSizeInventory(); i++) {
				stack = i < inventoryStacks.size() ? inventoryStacks.get(i) : ItemStack.EMPTY;
				inventory.setInventorySlotContents(i, stack.isEmpty() ? ItemStack.EMPTY : stack.copy());
			}
		}
		BlockTools.notifyBlockUpdate(world, pos);
	}

	@Override
	public boolean shouldReuseRule(World world, Block block, int meta, int turns, BlockPos pos) {
		return false;
	}

	@Override
	public void writeRuleData(NBTTagCompound tag) {
		super.writeRuleData(tag);
		tag.setInteger("lootLevel", randomLootLevel);
		tag.setTag("teData", this.tag);

		NBTTagCompound invData = new NBTTagCompound();
		invData.setInteger("length", inventoryStacks.size());
		NBTTagCompound itemTag;
		NBTTagList list = new NBTTagList();
		@Nonnull ItemStack stack;
		for (int i = 0; i < inventoryStacks.size(); i++) {
			stack = inventoryStacks.get(i);
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
	public void parseRuleData(NBTTagCompound tag) {
		super.parseRuleData(tag);
		if (tag.hasKey(INVENTORY_DATA_TAG)) {
			NBTTagCompound inventoryTag = tag.getCompoundTag(INVENTORY_DATA_TAG);
			int length = inventoryTag.getInteger("length");
			inventoryStacks = NonNullList.withSize(length, ItemStack.EMPTY);
			NBTTagCompound itemTag;
			NBTTagList list = inventoryTag.getTagList("inventoryContents", Constants.NBT.TAG_COMPOUND);
			int slot;
			@Nonnull ItemStack stack;
			for (int i = 0; i < list.tagCount(); i++) {
				itemTag = list.getCompoundTagAt(i);
				stack = new ItemStack(itemTag);
				if (!stack.isEmpty()) {
					slot = itemTag.getInteger("slot");
					inventoryStacks.set(slot, stack);
				}
			}
		}
		randomLootLevel = tag.getInteger("lootLevel");
		this.tag = tag.getCompoundTag("teData");
	}
}
