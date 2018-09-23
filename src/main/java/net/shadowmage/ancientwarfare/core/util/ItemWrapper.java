package net.shadowmage.ancientwarfare.core.util;

import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.shadowmage.ancientwarfare.core.AncientWarfareCore;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class ItemWrapper {
	public final Item item;
	public final int damage;

	public ItemWrapper(Item item, int damage) {
		this.item = item;
		this.damage = damage;
	}

	public static ArrayList<ItemWrapper> buildList(String listName, String[] input) {
		ArrayList<ItemWrapper> outputList = new ArrayList<>();

		AncientWarfareCore.LOG.info("Building " + listName + "...");

		for (String itemName : input) {
			itemName = itemName.trim();
			if (!itemName.equals("")) {
				String[] itemId = itemName.split(":");
				if (Array.getLength(itemId) != 2 && Array.getLength(itemId) != 3) {
					AncientWarfareCore.LOG.warn(" - Invalid item (bad length of " + Array.getLength(itemId) + "): " + itemId);
					continue;
				}
				if (itemId[0] == null || itemId[1] == null) {
					AncientWarfareCore.LOG.warn(" - Invalid block (parse/format error): " + itemId);
					continue;
				}

				Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(itemId[0] + ":" + itemId[1]));

				if (item == null) {
					AncientWarfareCore.LOG.warn(" - Skipping missing item: " + itemName);
					continue;
				}
				short damage = -1;
				if (Array.getLength(itemId) == 3) {
					try {
						damage = Short.parseShort(itemId[2]);
					}
					catch (NumberFormatException e) {
						AncientWarfareCore.LOG.warn(" - Damage value invalid : '" + itemId[2] + "', must be a number between 0 and " + Short.MAX_VALUE);
						continue;
					}
				}
				outputList.add(new ItemWrapper(item, damage));
			}
		}

		AncientWarfareCore.LOG.info("...added " + outputList.size() + " items to " + listName);

		return outputList;
	}
}
