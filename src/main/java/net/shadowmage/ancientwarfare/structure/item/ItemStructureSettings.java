package net.shadowmage.ancientwarfare.structure.item;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.shadowmage.ancientwarfare.core.util.BlockTools;

public class ItemStructureSettings {
	private static final String STRUCT_DATA_TAG = "structData";
	private static final String BUILD_KEY_TAG = "buildKey";
	private BlockPos pos1 = BlockPos.ORIGIN;
	private BlockPos pos2 = BlockPos.ORIGIN;
	BlockPos key = BlockPos.ORIGIN;
	EnumFacing buildFace;
	String name = "";

	private ItemStructureSettings() {

	}

	/*
	 * @param stack to extract the info from
	 */
	public static ItemStructureSettings getSettingsFor(ItemStack stack) {
		ItemStructureSettings settings = new ItemStructureSettings();
		NBTTagCompound tag;
		//noinspection ConstantConditions
		if (stack.hasTagCompound() && stack.getTagCompound().hasKey(STRUCT_DATA_TAG)) {
			tag = stack.getTagCompound().getCompoundTag(STRUCT_DATA_TAG);
		} else {
			tag = new NBTTagCompound();
		}
		if (tag.hasKey("pos1")) {
			settings.pos1 = BlockPos.fromLong(tag.getLong("pos1"));
		}
		if (tag.hasKey("pos2")) {
			settings.pos2 = BlockPos.fromLong(tag.getLong("pos2"));
		}
		if (tag.hasKey(BUILD_KEY_TAG)) {
			settings.key = BlockPos.fromLong(tag.getCompoundTag(BUILD_KEY_TAG).getLong("key"));
			settings.buildFace = EnumFacing.VALUES[tag.getCompoundTag(BUILD_KEY_TAG).getByte("face")];
		}
		if (tag.hasKey("name")) {
			settings.name = tag.getString("name");
		}

		return settings;
	}

	public static void setSettingsFor(ItemStack item, ItemStructureSettings settings) {
		NBTTagCompound tag = new NBTTagCompound();
		if (settings.hasPos1()) {
			tag.setLong("pos1", settings.getPos1().toLong());
		}
		if (settings.hasPos2()) {
			tag.setLong("pos2", settings.getPos2().toLong());
		}
		if (settings.hasBuildKey()) {
			NBTTagCompound buildKeyTag = new NBTTagCompound();
			buildKeyTag.setByte("face", (byte) settings.buildFace.ordinal());
			buildKeyTag.setLong("key", settings.key.toLong());
			tag.setTag(BUILD_KEY_TAG, buildKeyTag);
		}
		if (settings.hasName()) {
			tag.setString("name", settings.name);
		}
		item.setTagInfo(STRUCT_DATA_TAG, tag);
	}

	public void setPos1(BlockPos pos) {
		pos1 = pos;
	}

	public void setPos2(BlockPos pos) {
		pos2 = pos;
	}

	public void setBuildKey(BlockPos pos, EnumFacing face) {
		key = pos;
		buildFace = face;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean hasPos1() {
		return pos1 != BlockPos.ORIGIN;
	}

	public boolean hasPos2() {
		return pos2 != BlockPos.ORIGIN;
	}

	public boolean hasBuildKey() {
		return key != BlockPos.ORIGIN;
	}

	public boolean hasName() {
		return !name.isEmpty();
	}

	public BlockPos buildKey() {
		return key;
	}

	public EnumFacing face() {
		return buildFace;
	}

	public String name() {
		return name;
	}

	void clearSettings() {
		pos1 = BlockPos.ORIGIN;
		pos2 = BlockPos.ORIGIN;
		key = BlockPos.ORIGIN;
		name = "";
	}

	public BlockPos getPos1() {
		return pos1;
	}

	public BlockPos getPos2() {
		return pos2;
	}

	public BlockPos getMin() {
		return BlockTools.getMin(pos1, pos2);
	}

	public BlockPos getMax() {
		return BlockTools.getMax(pos1, pos2);
	}

	public AxisAlignedBB getBoundingBox() {
		return new AxisAlignedBB(getMin(), getMax());
	}
}
