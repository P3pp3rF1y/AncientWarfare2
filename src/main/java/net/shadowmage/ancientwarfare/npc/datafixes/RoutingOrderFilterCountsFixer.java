package net.shadowmage.ancientwarfare.npc.datafixes;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.datafix.IFixableData;
import net.minecraftforge.common.util.Constants;
import net.shadowmage.ancientwarfare.npc.init.AWNPCItems;

import java.util.Arrays;

public class RoutingOrderFilterCountsFixer implements IFixableData {
	@Override
	public int getFixVersion() {
		return 6;
	}

	@Override
	public NBTTagCompound fixTagCompound(NBTTagCompound compound) {
		String id = compound.getString("id");

		//noinspection ConstantConditions
		if (id.equals(AWNPCItems.ROUTING_ORDER.getRegistryName().toString())) {
			NBTTagCompound tag = compound.getCompoundTag("tag");
			NBTTagList entryList = tag.getCompoundTag("orders").getTagList("entryList", Constants.NBT.TAG_COMPOUND);
			for (int i = 0; i < entryList.tagCount(); i++) {
				NBTTagCompound routePointNBT = entryList.getCompoundTagAt(i);
				NBTTagList filterList = routePointNBT.getTagList("filterList", Constants.NBT.TAG_COMPOUND);
				int[] filterCounts = new int[12];
				Arrays.fill(filterCounts, 0);
				for (int j = 0; j < filterList.tagCount(); j++) {
					NBTTagCompound itemTag = filterList.getCompoundTagAt(j);
					int slot = itemTag.getInteger("slot");

					ItemStack filterStack = new ItemStack(itemTag);
					filterCounts[slot] = filterStack.getCount();
				}
				routePointNBT.setIntArray("filterCounts", filterCounts);
			}
		}
		return compound;
	}
}
