package net.shadowmage.ancientwarfare.structure.block;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.shadowmage.ancientwarfare.core.util.StringTools;

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

private static final String resourcePath = "/assets/ancientwarfare/resources/";

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

private BlockDataManager(){}
private static BlockDataManager instance = new BlockDataManager(){};
public static BlockDataManager instance(){return instance;}

/**
 * must be called during pre-init to load block info for templates to use
 */
public void load()
  {
  loadBlockNamesAndIDs(getCSVLines(resourcePath+"block_name_id.csv"));
  loadItemNamesAndIDs(getCSVLines(resourcePath+"item_name_id.csv"));
  loadBlockRotations(getCSVLines(resourcePath+"block_rotations.csv"));
  loadBlockPriorities(getCSVLines(resourcePath+"block_priorities.csv"));
  loadBlockItems(getCSVLines(resourcePath+"block_items.csv"));
  }

/**
 * for 1.6, will need to rewrite to load from Block.blocksList[] by block-ID read from file
 * @param lines
 */
private void loadBlockNamesAndIDs(List<String> lines)
  {
  String[] bits;
  
  Block block;
  String name;
  int id;
  
  for(String line : lines)
    {
    bits = line.split(",",-1);
    name = bits[0];
    id = StringTools.safeParseInt(bits[1]);    
    block = Block.getBlockFromName(name);
    
    blockIDToBlock.put(id, block);
    blockIDToName.put(id, name);
    blockNameToBlock.put(name, block);
    blockNameToID.put(name, id);
    blockToID.put(block, id);
    blockToName.put(block, name);
    }
  }

private void loadBlockRotations(List<String> lines)
  {
  String[] bits;  
  Block block;
  String name;
  byte[] rotations;
  BlockInfo info;
  String rot;
  for(String line : lines)
    {
    bits = line.split(",",-1);
    name = bits[0];
    block = blockNameToBlock.get(name);
    rotations = new byte[16];
    for(int i = 0; i<16; i++)
      {
      rot = bits[i+1];
      if(rot.equals("") || rot.isEmpty())
        {
        rotations[i] = (byte)i;
        }      
      else
        {
        rotations[i] = StringTools.safeParseByte(bits[i+1]);
        }      
      }
    info = blockInfoMap.get(block);
    if(info==null)
      {
      info = new BlockInfo();   
      blockInfoMap.put(block, info);   
      }
    info.rotations = rotations;   
    
    }
  }

private void loadBlockItems(List<String> lines)
  {
  String[] bits;  
  
  BlockInfo info;

  Block block;
  Block block2;
  String blockName;
  int blockMeta; 
  
  Item item;
  String itemName;
  int itemDamage;
  int itemQuantity;
  
  for(String line : lines)
    {
    bits = line.split(",", -1);
    blockName = bits[0];
    block = blockNameToBlock.get(blockName);
    info = blockInfoMap.get(block);
    if(info==null)
      {
      info = new BlockInfo();   
      blockInfoMap.put(block, info);   
      }
    blockMeta = StringTools.safeParseInt(bits[1]);
    itemName = bits[2];
    itemDamage = StringTools.safeParseInt(bits[3]);
    itemQuantity = StringTools.safeParseInt(bits[4]);
    if(blockMeta==-1)
      {
      blockMeta=0;
      info.singleItem = true;
      }
    if(itemName.equals("null"))
      {
      info.noItem = true;
      }
    else
      {
      info.noItem = false;
      item = itemNameToItem.get(itemName);   
      if(item!=null)
        {
        info.metaStacks[blockMeta] = new ItemStack(item, itemDamage, itemQuantity);
        }
      else
        {
        block2 = blockNameToBlock.get(itemName);
        info.metaStacks[blockMeta] = new ItemStack(block2, itemDamage, itemQuantity);
        }
      }
    }
  }

private void loadBlockPriorities(List<String> lines)
  {
  String[] bits;  
  
  BlockInfo info;

  Block block;
  String blockName;
  int priority;
  
  for(String line : lines)
    {
    bits = line.split(",", -1);
    blockName = bits[0];
    priority = StringTools.safeParseInt(bits[1]);
    block = blockNameToBlock.get(blockName);
    info = blockInfoMap.get(block);
    
    if(info==null)
      {
      info = new BlockInfo();   
      blockInfoMap.put(block, info);   
      }
    info.buildPriority = (byte)priority;
    }
  }

private void loadItemNamesAndIDs(List<String> lines)
  {
  String[] bits;
  
  Item item;
  String name;
  int id;
  
  for(String line : lines)
    {
    bits = line.split(",",-1);
    name = bits[0];
    id = StringTools.safeParseInt(bits[1]);
    item = Item.getItemById(id);
    
    itemIDToItem.put(id, item);
    itemIDToName.put(id, name);
    itemNameToID.put(name, id);
    itemNameToItem.put(name, item);
    itemToID.put(item, id);
    itemToName.put(item, name);    
    }
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
    return info.getStackFor(block, meta);
    }
  return new ItemStack(block, 1, meta);
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

public ItemStack getStackFor(Block block, int meta)
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
  return new ItemStack(block, 1, meta);
  }
}

/**
 * 
 * @param path to file, incl. filename + extension, running-dir relative
 * @return
 * @throws IOException
 */
private static List<String> getCSVLines(String path)
  {
  InputStream is = BlockDataManager.class.getResourceAsStream(path);
  ArrayList<String> lines = new ArrayList<String>();
  BufferedReader reader = new BufferedReader(new InputStreamReader(is));
  String line;
  try
    {
    while((line = reader.readLine())!=null)
      {
      if(line.startsWith("#")){continue;}
      lines.add(line);
      }
    } 
  catch (IOException e)
    {
    e.printStackTrace();
    }  
  try
    {
    is.close();
    } 
  catch (IOException e)
    {
    e.printStackTrace();
    }
  return lines;
  }

}
