package net.shadowmage.ancientwarfare.structure.block;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.shadowmage.ancientwarfare.core.config.AWCoreStatics;
import net.shadowmage.ancientwarfare.core.config.AWLog;
import net.shadowmage.ancientwarfare.core.util.StringTools;

import java.util.HashMap;
import java.util.List;

/*
 * Block data manager
 * holds information regarding meta-rotations, build-priorities, and block-item
 * mappings.<br>
 * Loads necessary information from disk from .csv files for rotation mapping,
 * item mapping, 1.7 names-to-1.6 IDs mapping, and build-priorities mapping.<br>
 * May be queried to retrieve block or item from name or ID (where an ID map exists),
 * retrieve the item necessary to spawn/place a block, the build priority for a block,
 * or the meta-rotation states for block placement.
 *
 * @author Shadowmage
 */
public class BlockDataManager {

	/*
	 * Mapping of old 1.6 names to block instances, and vice-versa.  Used to enable loading of pre 1.7 templates.
	 */
	private HashMap<Block, String> blockToName = new HashMap<>();
	private HashMap<String, Block> blockNameToBlock = new HashMap<>();
	private HashMap<String, Block> blockUnlocalizedNameToBlock = new HashMap<>();

	private HashMap<Block, BlockInfo> blockInfoMap = new HashMap<>();

	public static final BlockDataManager INSTANCE = new BlockDataManager();

	private BlockDataManager() {
	}

    /*
	 * must be called during pre-init to load block info for templates to use
     */

	public void load() {
		loadBlockNamesAndIDs(StringTools.getResourceLines(AWCoreStatics.resourcePath + "block_name_id.csv"));
		loadBlockRotations(StringTools.getResourceLines(AWCoreStatics.resourcePath + "block_rotations.csv"));
		loadBlockPriorities(StringTools.getResourceLines(AWCoreStatics.resourcePath + "block_priorities.csv"));
		loadBlockItems(StringTools.getResourceLines(AWCoreStatics.resourcePath + "block_items.csv"));

		for (Block block : Block.REGISTRY) {
			if (block == null) {
				return;
			}
			blockUnlocalizedNameToBlock.put(block.getUnlocalizedName(), block);
		}

	}

	/*
	 * loads OLD (1.6) block names from file -- used to enable loading of older templates
	 */
	private void loadBlockNamesAndIDs(List<String> lines) { //TODO remove?
		String[] bits;

		Block block;
		String name;
		int id;

		for (String line : lines) {
			bits = line.split(",", -1);
			name = bits[0];
			id = StringTools.safeParseInt(bits[1]);
			block = Block.getBlockFromName(name);
			if (block == null) {
				AWLog.logError("ERROR parsing block name from name mapping: " + name + " id: " + id + " found: " + block);
				continue;
			}
			blockNameToBlock.put(name, block);
			blockToName.put(block, name);
		}
	}

	private void loadBlockRotations(List<String> lines) {
		String[] bits;
		Block block;
		String name;
		byte[] rotations;
		BlockInfo info;
		String rot;
		for (String line : lines) {
			bits = line.split(",", -1);
			name = bits[0];
			block = blockNameToBlock.get(name);
			rotations = new byte[16];
			for (int i = 0; i < 16; i++) {
				rot = bits[i + 1];
				if (rot.equals("") || rot.isEmpty()) {
					rotations[i] = (byte) i;
				} else {
					rotations[i] = StringTools.safeParseByte(bits[i + 1]);
				}
			}
			info = blockInfoMap.get(block);
			if (info == null) {
				info = new BlockInfo();
				blockInfoMap.put(block, info);
			}
			info.setRotations(rotations);
		}
	}

	private void loadBlockItems(List<String> lines) {
		String[] bits;

		BlockInfo info;

		Block block;
		Block block2;
		String blockName;
		int blockMeta;

		Item item;
		String itemName;
		int itemDamage;
		int itemQuantity;

		for (String line : lines) {
			bits = line.split(",", -1);
			blockName = bits[0];
			block = blockNameToBlock.get(blockName);
			info = blockInfoMap.get(block);
			if (info == null) {
				info = new BlockInfo();
				blockInfoMap.put(block, info);
			}
			blockMeta = StringTools.safeParseInt(bits[1]);
			itemName = bits[2];
			itemDamage = StringTools.safeParseInt(bits[3]);
			itemQuantity = StringTools.safeParseInt(bits[4]);
			if (blockMeta == -1) {
				blockMeta = 0;
				info.singleItem = true;
			}
			if (itemName.equals("null")) {
				if (info.singleItem) {
					for (int i = 0; i < 16; i++) {
						info.noItemFlags[i] = true;
					}
				} else {
					info.noItemFlags[blockMeta] = true;
				}
			} else {
				item = Item.REGISTRY.getObject(new ResourceLocation(itemName));
				if (item != null) {
					info.metaStacks.set(blockMeta, new ItemStack(item, itemQuantity, itemDamage));
				} else {
					block2 = blockNameToBlock.get("minecraft:" + itemName);
					info.metaStacks.set(blockMeta, new ItemStack(block2, itemQuantity, itemDamage));
				}
			}
		}
	}

	private void loadBlockPriorities(List<String> lines) {
		String[] bits;

		BlockInfo info;

		Block block;
		String blockName;
		int priority;

		for (String line : lines) {
			bits = line.split(",", -1);
			blockName = bits[0];
			priority = StringTools.safeParseInt(bits[1]);
			block = blockNameToBlock.get(blockName);
			info = blockInfoMap.get(block);

			if (info == null) {
				info = new BlockInfo();
				blockInfoMap.put(block, info);
			}
			info.buildPriority = (byte) priority;
		}
	}

	/*
	 * return the new meta for the input block after rotating clockwise 90' x the input number of turns
	 */
	public int getRotatedMeta(Block block, int meta, int turns) {
		BlockInfo info = blockInfoMap.get(block);
		if (info != null) {
			int rm = meta;
			for (int i = 0; i < turns; i++) {
				rm = info.getRotatedMeta(rm);
			}
			return rm;
		}
		return meta;
	}

	/*
	 * return the build-priority for the block<br>
	 * 0==solid block, no requisites, e.g. stone<br>
	 * 1==second-pass building, e.g. torches<br>
	 * (higher build-priorities may exist as well)
	 */
	public int getPriorityForBlock(Block block) {
		BlockInfo info = blockInfoMap.get(block);
		if (info != null) {
			return info.buildPriority;
		}
		return 0;
	}

	/*
	 * get the 1.7 name for the input Block
	 */
	public String getNameForBlock(Block block) {
		String name = block.getRegistryName() == null ? null : block.getRegistryName().toString();
		if (name == null) {
			throw new RuntimeException("Could not locate block name for: " + block.getUnlocalizedName());
		}
		return name;
	}

	/*
	 * get the Block for the 1.7 name
	 */
	public Block getBlockForName(String name) {
		Block b = Block.getBlockFromName(name);
		if (b == null) {
			if (blockNameToBlock.containsKey(name)) {
				return blockNameToBlock.get(name);
			} else if (blockUnlocalizedNameToBlock.containsKey(name)) {
				return blockUnlocalizedNameToBlock.get(name);
			}
			return Blocks.AIR;
		}
		return b;
	}

	/*
	 * return a proper sized item-stack for the input block, null if no item mapping / forced null mapping<br>
	 * Returns a _new_ stack for each call of the method.<br>
	 * Use the returned stack to copy, alter stack-size, etc.<br>
	 * returned stack-size is dependent upon how many input items are needed for that block
	 * usually 1, but can be 2 for double-slabs
	 */
	public ItemStack getInventoryStackForBlock(Block block, int meta) {
		BlockInfo info = blockInfoMap.get(block);
		if (info != null) {
			return info.getStackFor(block, meta);
		}
		return new ItemStack(block, 1, meta);
	}

	private class BlockInfo {

		boolean singleItem = false;
		boolean hasRotation = false;
		/*
		 * item-stack map, by block-meta.  if singleItem==true, will use index[0] instead of whatever is passed in
		 */ NonNullList<ItemStack> metaStacks = NonNullList.withSize(16, ItemStack.EMPTY);
		boolean[] noItemFlags = new boolean[16];//flag will be true for a meta if it should return no item

		byte[] rotations = new byte[16];
		byte buildPriority = 0;

		public void setRotations(byte[] rotations) {
			this.rotations = rotations;
			this.hasRotation = true;
		}

		public int getRotatedMeta(int meta) {
			if (!hasRotation) {
				return meta;
			}
			return rotations[meta];
		}

		public ItemStack getStackFor(Block block, int meta) {
			if (singleItem && !metaStacks.get(0).isEmpty()) {
				return metaStacks.get(0).copy();
			} else if (noItemFlags[meta]) {
				return ItemStack.EMPTY;
			} else if (!metaStacks.get(meta).isEmpty()) {
				return metaStacks.get(meta).copy();
			}
			return new ItemStack(Item.getItemFromBlock(block), 1, meta);
		}
	}

}
