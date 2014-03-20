package net.shadowmage.ancientwarfare.core.interfaces;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

/**
 * should be implemented by items that can receive server-side alternate key-action events
 * the key is determined client-side, so the item need only react to it -- should replace
 * any current instances of using left-click on items (as it is buggy as all hell)
 * @author Shadowmage
 *
 */
public interface IItemKeyInterface
{

/**
 * called server-side when a client presses the key that is bound to alternate item-use function
 * 
 * @param player the player using the item
 * @param stack the item stack that is in-use
 */
public void onKeyAction(EntityPlayer player, ItemStack stack);

}
