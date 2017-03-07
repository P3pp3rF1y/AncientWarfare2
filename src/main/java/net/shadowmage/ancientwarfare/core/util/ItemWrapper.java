package net.shadowmage.ancientwarfare.core.util;

import java.lang.reflect.Array;
import java.util.ArrayList;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.item.Item;
import net.shadowmage.ancientwarfare.core.AncientWarfareCore;

public class ItemWrapper {
    public final Item item;
    public final int damage;
    
    public ItemWrapper(Item item, int damage) {
        this.item = item;
        this.damage = damage;
    }
    
    public static ArrayList<ItemWrapper> buildList(String listName, String[] input) {
        ArrayList<ItemWrapper> outputList = new ArrayList<ItemWrapper>();

        AncientWarfareCore.log.info("Building " + listName + "...");
        
        for (String itemName : input) {
            itemName = itemName.trim();
            if (!itemName.equals("")) {
                String[] itemId = itemName.split(":");
                if (Array.getLength(itemId) != 2 && Array.getLength(itemId) != 3 ) {
                    AncientWarfareCore.log.warn(" - Invalid item (bad length of " + Array.getLength(itemId) + "): " + itemId);
                    continue;
                }
                if (itemId[0] == null || itemId[1] == null) {
                    AncientWarfareCore.log.warn(" - Invalid block (parse/format error): " + itemId);
                    continue;
                }
                
                Item item = GameRegistry.findItem(itemId[0], itemId[1]);
                
                if (item == null) {
                    AncientWarfareCore.log.warn(" - Skipping missing item: " + itemName);
                    continue;
                }
                short damage = -1;
                if (Array.getLength(itemId) == 3) {
                    try {
                        damage = Short.parseShort(itemId[2]);
                    } catch (NumberFormatException e) {
                        AncientWarfareCore.log.warn(" - Damage value invalid : '" + itemId[2] + "', must be a number between 0 and " + Short.MAX_VALUE);
                        continue;
                    }
                }
                outputList.add(new ItemWrapper(item, damage));
            }
        }

        AncientWarfareCore.log.info("...added " + outputList.size() + " items to " + listName);
        
        
        return outputList;
    }
}
