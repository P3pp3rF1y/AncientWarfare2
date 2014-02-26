package net.shadowmage.ancientwarfare.structure.block;

import java.util.HashMap;

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

private HashMap<Integer, String> blockIDToName = new HashMap<Integer, String>();
private HashMap<Integer, Block> blockIDToBlock = new HashMap<Integer, Block>();
private HashMap<String, Block> blockNameToBlock = new HashMap<String, Block>();
private HashMap<String, Integer> blockNameToID = new HashMap<String, Integer>();
private HashMap<Block, Integer> blockToID = new HashMap<Block, Integer>();
private HashMap<Block, String> blockToName = new HashMap<Block, String>();
private HashMap<Integer, String> itemIDToName = new HashMap<Integer, String>();
private HashMap<Integer, Item> itemIDToItem = new HashMap<Integer, Item>();
private HashMap<String, Item> itemNameToItem = new HashMap<String, Item>();
private HashMap<String, Integer> itemNameToID = new HashMap<String, Integer>();
private HashMap<Item, Integer> itemToID = new HashMap<Item, Integer>();
private HashMap<Item, String> itemToName = new HashMap<Item, String>();

private HashMap<Block, BlockInfo> blockInfoMap = new HashMap<Block, BlockInfo>();

public void load()
  {
  /**
   * TODO
   * load item and block name/id and id/name maps from disk
   * load item and block instance to name/id and id/name to instance maps 
   * load block rotation data
   * load block priority data
   * load block item mapping data
   */  
  }

/**
 * return the new meta for the input block after rotating clockwise 90' x the input number of turns
 * @param block
 * @param meta
 * @param turns
 * @return
 */
public int getRotatedMeta(Block block, int meta, int turns)
  {
  BlockInfo info = blockInfoMap.get(block);    
  if(info!=null)
    {
    int rm = meta;
    for(int i =0; i <turns; i++)
      {
      rm = info.getRotatedMeta(rm);
      }    
    return rm;
    }
  return meta;
  }

/**
 * return the build-priority for the block<br>
 * 0==solid block, no requisites, e.g. stone<br>
 * 1==second-pass building, e.g. torches<br>
 * (higher build-priorities may exist as well)
 * @param block
 * @return
 */
public int getPriorityForBlock(Block block)
  {
  BlockInfo info = blockInfoMap.get(block);
  if(info!=null)
    {
    return info.buildPriority;
    }
  return 0;
  }

/**
 * get the Block for the input 1.6 block-id
 * @param id
 * @return
 */
public Block getBlockForID(int id)
  {
  if(blockIDToBlock.containsKey(id))
    {
    return blockIDToBlock.get(id);
    }
  return null;
  }

/**
 * get the 1.6 ID for the input Block
 * @param item
 * @return
 */
public int getIDForBlock(Block block)
  {
  if(blockToID.containsKey(block))
    {
    return blockToID.get(block);
    }
  return 0;
  }

/**
 * get the 1.7 name for the input Block
 * @param item
 * @return
 */
public String getNameForBlock(Block block)
  {
  if(blockToName.containsKey(block))
    {
    return blockToName.get(block);
    }
  return null;
  }

/**
 * get the Block for the 1.7 name
 * @param name
 * @return
 */
public Block getBlockForName(String name)
  {
  if(blockNameToBlock.containsKey(name))
    {
    return blockNameToBlock.get(name);
    }
  return null;
  }

/**
 * get the 1.7 name for the 1.6 id
 * @param id
 * @return
 */
public String getNameBlockForID(int id)
  {
  if(blockIDToName.containsKey(id))
    {
    return blockIDToName.get(id);
    }
  return null;
  }

/**
 * get the 1.6 ID for the 1.7 name
 * @param name
 * @return
 */
public int getIDForBlockName(String name)
  {
  if(blockNameToID.containsKey(name))
    {
    return blockNameToID.get(name);
    }
  return 0;
  }

/**
 * get the Item for the input 1.6 item-id
 * @param id
 * @return
 */
public Item getItemForID(int id)
  {
  if(itemIDToItem.containsKey(id))
    {
    return itemIDToItem.get(id);
    }
  return null;
  }

/**
 * get the 1.6 ID for the input Item
 * @param item
 * @return
 */
public int getIDForItem(Item item)
  {
  if(itemToID.containsKey(item))
    {
    return itemToID.get(item);        
    }
  return 0;
  }

/**
 * get the 1.7 name for the input Item
 * @param item
 * @return
 */
public String getNameForItem(Item item)
  {
  if(itemToName.containsKey(item))
    {
    return itemToName.get(item);
    }
  return null;
  }

/**
 * get the Item for the 1.7 name
 * @param name
 * @return
 */
public Item getItemForName(String name)
  {
  if(itemNameToItem.containsKey(name))
    {
    return itemNameToItem.get(name);
    }
  return null;
  }

/**
 * get the 1.7 name for the 1.6 id
 * @param id
 * @return
 */
public String getNameItemForID(int id)
  {
  if(itemIDToName.containsKey(id))
    {
    return itemIDToName.get(id);
    }
  return null;
  }

/**
 * get the 1.6 ID for the 1.7 name
 * @param name
 * @return
 */
public int getIDForItemName(String name)
  {
  if(itemNameToID.containsKey(name))
    {
    return itemNameToID.get(name);
    }
  return 0;  
  }

/**
 * return a proper sized item-stack for the input block, null if no item mapping / forced null mapping<br>
 * Returns a _new_ stack for each call of the method.<br>
 * Use the returned stack to copy, alter stack-size, etc.<br>
 * returned stack-size is dependent upon how many input items are needed for that block
 * usually 1, but can be 2 for double-slabs
 * @param block
 * @param size
 * @return
 */
public ItemStack getInventoryStackForBlock(Block block, int meta)
  {
  BlockInfo info = blockInfoMap.get(block);
  if(info!=null)
    {
    return info.getStackFor(meta);
    }
  return null;
  }

private class BlockInfo
{
boolean singleItem = false;
boolean noItem = false;
/**
 * item-stack map, by block-meta.  if singleItem==true, will use index[0] instead of whatever is passed in
 */
ItemStack[] metaStacks = new ItemStack[16];

byte[] rotations;
byte buildPriority = 0;

public int getRotatedMeta(int meta)
  {
  return rotations[meta];
  }

public ItemStack getStackFor(int meta)
  {
  if(noItem)
    {
    return null;
    }
  else if(singleItem && metaStacks[0]!=null)
    {
    return metaStacks[0].copy();
    }
  else if(metaStacks[meta]!=null)
    {
    return metaStacks[meta].copy();
    }    
  return null;
  }
}

}
