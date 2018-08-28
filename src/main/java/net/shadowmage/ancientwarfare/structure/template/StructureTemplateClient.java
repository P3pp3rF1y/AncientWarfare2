package net.shadowmage.ancientwarfare.structure.template;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.Vec3i;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nonnull;

public class StructureTemplateClient {
	private static final String RESOURCE_LIST_TAG = "resourceList";
	public final String name;
	public final Vec3i size;
	public final Vec3i offset;
	public final NonNullList<ItemStack> resourceList = NonNullList.create();
	public boolean survival;

	public StructureTemplateClient(StructureTemplate template) {
		this.name = template.name;
		this.size = template.size;
		this.offset = template.offset;
		this.survival = template.getValidationSettings().isSurvival();
		if (this.survival) {
			resourceList.addAll(template.getResourceList());
		}
	}

	public StructureTemplateClient(String name, Vec3i size, Vec3i offset) {
		if (name == null) {
			throw new IllegalArgumentException("cannot have null name for structure");
		}
		this.name = name;
		this.size = size;
		this.offset = offset;
	}

	public void writeToNBT(NBTTagCompound tag) {
		tag.setString("name", name);
		tag.setIntArray("size", new int[] {size.getX(), size.getY(), size.getZ()});
		tag.setIntArray("offset", new int[] {offset.getX(), offset.getY(), offset.getZ()});
		tag.setBoolean("survival", survival);
		if (survival && !resourceList.isEmpty()) {
			NBTTagList stackList = new NBTTagList();
			NBTTagCompound stackTag;
			for (ItemStack stack : this.resourceList) {
				stackTag = new NBTTagCompound();
				stack.writeToNBT(stackTag);
				stackList.appendTag(stackTag);
			}
			tag.setTag(RESOURCE_LIST_TAG, stackList);
		}
	}

	public static StructureTemplateClient readFromNBT(NBTTagCompound tag) {
		String name = tag.getString("name");
		boolean survival = tag.getBoolean("survival");
		Vec3i size = fromIntArray(tag.getIntArray("size"));
		Vec3i offset = fromIntArray(tag.getIntArray("offset"));
		StructureTemplateClient template = new StructureTemplateClient(name, size, offset);
		template.survival = survival;

		if (tag.hasKey(RESOURCE_LIST_TAG)) {
			NBTTagList stackList = tag.getTagList(RESOURCE_LIST_TAG, Constants.NBT.TAG_COMPOUND);
			NBTTagCompound stackTag;
			@Nonnull ItemStack stack;
			for (int i = 0; i < stackList.tagCount(); i++) {
				stackTag = stackList.getCompoundTagAt(i);
				stack = new ItemStack(stackTag);
				if (!stack.isEmpty()) {
					template.resourceList.add(stack);
				}
			}
		}
		return template;
	}

	private static Vec3i fromIntArray(int[] values) {
		return new Vec3i(values.length > 0 ? values[0] : 0, values.length > 1 ? values[1] : 0, values.length > 2 ? values[2] : 0);
	}

	public Vec3i getSize() {
		return size;
	}

	public Vec3i getOffset() {
		return offset;
	}
}
