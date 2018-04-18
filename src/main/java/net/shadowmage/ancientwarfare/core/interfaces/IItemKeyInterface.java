package net.shadowmage.ancientwarfare.core.interfaces;

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

	public static enum ItemKey {
		KEY_0,
		KEY_1,
		KEY_2,
		KEY_3,
		KEY_4;
	}

	/*
	 * called client side before sending packet to server
	 * return true to send packet
	 *
	 * @param key the number of alt-use key that is being pressed
	 * @return true to send activation packet to client
	 */
	public boolean onKeyActionClient(EntityPlayer player, ItemStack stack, ItemKey key);

	/*
	 * called server-side when a client presses the key that is bound to alternate item-use function
	 *
	 * @param player the player using the item
	 * @param stack  the item stack that is in-use
	 * @param key    the number of alt-use key that is being pressed
	 */
	public void onKeyAction(EntityPlayer player, ItemStack stack, ItemKey key);

}
