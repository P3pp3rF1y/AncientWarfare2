package net.shadowmage.ancientwarfare.core.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.item.ItemStack;

public class ItemTools {
	private ItemTools() {}

	public static JsonElement serializeToJson(ItemStack stack) {
		JsonObject ret = new JsonObject();
		//noinspection ConstantConditions
		ret.addProperty("name", stack.getItem().getRegistryName().toString());
		if (stack.getItem().getHasSubtypes() || stack.getItemDamage() != 0) {
			ret.addProperty("data", stack.getItemDamage());
		}
		if (stack.getCount() > 1) {
			ret.addProperty("count", stack.getCount());
		}

		if (stack.hasTagCompound()) {

			//noinspection ConstantConditions
			ret.addProperty("nbt", stack.getTagCompound().toString());
		}

		return ret;
	}

}
