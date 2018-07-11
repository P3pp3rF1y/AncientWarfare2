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
	private boolean[] setKeys = new boolean[4];
	private BlockPos pos1;
	private BlockPos pos2;
	BlockPos key;
	EnumFacing buildFace;
	String name;

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
		for (int i = 0; i < settings.setKeys.length; i++) {
			settings.setKeys[i] = false;
		}
		if (tag.hasKey("pos1")) {
			settings.pos1 = BlockPos.fromLong(tag.getLong("pos1"));
			settings.setKeys[0] = true;
		}
		if (tag.hasKey("pos2")) {
			settings.pos2 = BlockPos.fromLong(tag.getLong("pos2"));
			settings.setKeys[1] = true;
		}
		if (tag.hasKey(BUILD_KEY_TAG)) {
			settings.key = BlockPos.fromLong(tag.getCompoundTag(BUILD_KEY_TAG).getLong("key"));
			settings.setKeys[2] = true;
			settings.buildFace = EnumFacing.VALUES[tag.getCompoundTag(BUILD_KEY_TAG).getByte("face")];
		}
		if (tag.hasKey("name")) {
			settings.name = tag.getString("name");
			settings.setKeys[3] = true;
		}
		return settings;
	}

	public static void setSettingsFor(ItemStack item, ItemStructureSettings settings) {
		NBTTagCompound tag = new NBTTagCompound();
		if (settings.setKeys[0]) {
			tag.setLong("pos1", settings.getPos1().toLong());
		}
		if (settings.setKeys[1]) {
			tag.setLong("pos2", settings.getPos2().toLong());
		}
		if (settings.setKeys[2]) {
			NBTTagCompound tag1 = new NBTTagCompound();
			tag1.setByte("face", (byte) settings.buildFace.ordinal());
			tag1.setLong("key", settings.key.toLong());
			tag.setTag(BUILD_KEY_TAG, tag1);
		}
		if (settings.setKeys[3]) {
			tag.setString("name", settings.name);
		}
		item.setTagInfo(STRUCT_DATA_TAG, tag);
	}

	public void setPos1(BlockPos pos) {
		pos1 = pos;
		setKeys[0] = true;
	}

	public void setPos2(BlockPos pos) {
		pos2 = pos;
		setKeys[1] = true;
	}

	public void setBuildKey(BlockPos pos, EnumFacing face) {
		key = pos;
		buildFace = face;
		setKeys[2] = true;
	}

	public void setName(String name) {
		this.name = name;
		setKeys[3] = true;
	}

	public boolean hasPos1() {
		return setKeys[0];
	}

	public boolean hasPos2() {
		return setKeys[1];
	}

	public boolean hasBuildKey() {
		return setKeys[2];
	}

	public boolean hasName() {
		return setKeys[3];
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
		for (int i = 0; i < 3; i++) {
			this.setKeys[i] = false;
		}
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
