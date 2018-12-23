package net.shadowmage.ancientwarfare.automation.datafixes;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.datafix.FixTypes;
import net.minecraft.util.datafix.IDataFixer;
import net.minecraft.util.datafix.IDataWalker;
import net.minecraftforge.common.util.Constants;

import java.util.HashSet;
import java.util.Set;

public class ItemMapDataWalker implements IDataWalker {
	private final Set<ResourceLocation> tileIds = new HashSet<>();
	private final String[] tagPathElements;

	public ItemMapDataWalker(Class<?>[] tileClasses, String tagPath) {
		for (Class<?> tileClass : tileClasses) {
			//noinspection unchecked
			tileIds.add(TileEntity.getKey((Class<TileEntity>) tileClass));
		}
		this.tagPathElements = tagPath.split("/");
	}

	@Override
	public NBTTagCompound process(IDataFixer fixer, NBTTagCompound compound, int version) {
		ResourceLocation id = new ResourceLocation(compound.getString("id"));
		if (tileIds.contains(id)) {
			processItemMap(fixer, getItemMapNBT(compound), version);
		}

		return compound;
	}

	private void processItemMap(IDataFixer fixer, NBTTagList itemMapNBT, int version) {
		for (int i = 0; i < itemMapNBT.tagCount(); i++) {
			NBTTagCompound entryTag = itemMapNBT.getCompoundTagAt(i);
			entryTag.setTag("item", fixer.process(FixTypes.ITEM_INSTANCE, entryTag.getCompoundTag("item"), version));
		}
	}

	private NBTTagList getItemMapNBT(NBTTagCompound compound) {
		NBTTagCompound tag = compound;
		for (int i = 0; i < tagPathElements.length - 1; i++) {
			tag = tag.getCompoundTag(tagPathElements[i]);
		}
		return tag.getTagList(tagPathElements[tagPathElements.length - 1], Constants.NBT.TAG_COMPOUND);
	}
}
