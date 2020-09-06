package net.shadowmage.ancientwarfare.core.input;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

/*
 * should be implemented by items that can receive server-side alternate key-action events
 * the key is determined client-side, so the item need only react to it -- should replace
 * any current instances of using left-click on items (as it is buggy as all hell)
 *
 * @author Shadowmage
 */
public interface IItemKeyInterface {

	enum ItemAltFunction {
		ALT_FUNCTION_1,
		ALT_FUNCTION_2,
		ALT_FUNCTION_3,
		ALT_FUNCTION_4,
		ALT_FUNCTION_5
	}

	/*
	 * called client side before sending packet to server
	 * return true to send packet
	 *
	 * @param key the number of alt-use key that is being pressed
	 * @return true to send activation packet to client
	 */
	boolean onKeyActionClient(EntityPlayer player, ItemStack stack, ItemAltFunction altFunction);

	/*
	 * called server-side when a client presses the key that is bound to alternate item-use function
	 *
	 * @param player the player using the item
	 * @param stack  the item stack that is in-use
	 * @param key    the number of alt-use key that is being pressed
	 */
	void onKeyAction(EntityPlayer player, ItemStack stack, ItemAltFunction altFunction);

}
