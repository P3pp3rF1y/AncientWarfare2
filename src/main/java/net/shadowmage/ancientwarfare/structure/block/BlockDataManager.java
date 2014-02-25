package net.shadowmage.ancientwarfare.structure.block;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

/**
 * Block data manager
 * holds information regarding meta-rotations, build-priorities, and block-item
 * mappings.<br>
 * Loads necessary information from disk from .csv files for rotation mapping,
 * item mapping, 1.7 names-to-1.6 IDs mapping, and build-priorities mapping.<br>
 * May be queried to retrieve block or item from name or ID (where an ID map exists),
 * retrieve the item necessary to spawn/place a block, the build priority for a block,
 * or the meta-rotation states for block placement.
 * @author Shadowmage
 *
 */
public class BlockDataManager
{

public BlockDataManager()
  {

  }

public void load(){}//TODO

/**
 * return the new meta for the input block after rotating clockwise 90' x the input number of turns
 * @param block
 * @param meta
 * @param turns
 * @return
 */
public int getRotatedMeta(Block block, int meta, int turns){return 0;}//TODO

/**
 * return the build-priority for the block<br>
 * 0==solid block, no requisites, e.g. stone<br>
 * 1==second-pass building, e.g. torches<br>
 * (higher build-priorities may exist as well)
 * @param block
 * @return
 */
public int getPriorityForBlock(Block block){return 0;}//TODO

/**
 * get the Block for the input 1.6 block-id
 * @param id
 * @return
 */
public Block getBlockForID(int id){return null;}//TODO

/**
 * get the 1.6 ID for the input Block
 * @param item
 * @return
 */
public int getIDForBlock(Block block){return 0;}//TODO

/**
 * get the 1.7 name for the input Block
 * @param item
 * @return
 */
public String getNameForBlock(Block block){return null;}//TODO

/**
 * get the Block for the 1.7 name
 * @param name
 * @return
 */
public Block getBlockForName(String name){return null;}//TODO

/**
 * get the 1.7 name for the 1.6 id
 * @param id
 * @return
 */
public String getNameBlockForID(int id){return null;}//TODO

/**
 * get the 1.6 ID for the 1.7 name
 * @param name
 * @return
 */
public int getIDForBlockName(String name){return 0;}//TODO

/**
 * get the Item for the input 1.6 item-id
 * @param id
 * @return
 */
public Item getItemForID(int id){return null;}//TODO

/**
 * get the 1.6 ID for the input Item
 * @param item
 * @return
 */
public int getIDForItem(Item item){return 0;}//TODO

/**
 * get the 1.7 name for the input Item
 * @param item
 * @return
 */
public String getNameForItem(Item item){return null;}//TODO

/**
 * get the Item for the 1.7 name
 * @param name
 * @return
 */
public Item getItemForName(String name){return null;}//TODO

/**
 * get the 1.7 name for the 1.6 id
 * @param id
 * @return
 */
public String getNameItemForID(int id){return null;}//TODO

/**
 * get the 1.6 ID for the 1.7 name
 * @param name
 * @return
 */
public int getIDForItemName(String name){return 0;}//TODO

/**
 * return a size-1 item-stack for the input block, null if no item mapping / forced null mapping<br>
 * Use the returned stack to copy, alter stack-size, etc.<br>
 * Returns a _new_ stack for each call of the method, no cached values
 * @param block
 * @param size
 * @return
 */
public ItemStack getInventoryStackForBlock(Block block, int size){return null;}//TODO

}
