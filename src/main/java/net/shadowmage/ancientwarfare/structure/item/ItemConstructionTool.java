package net.shadowmage.ancientwarfare.structure.item;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MovingObjectPosition;
import net.shadowmage.ancientwarfare.core.config.AWLog;
import net.shadowmage.ancientwarfare.core.interfaces.IItemClickable;
import net.shadowmage.ancientwarfare.core.interfaces.IItemKeyInterface;
import net.shadowmage.ancientwarfare.core.util.BlockPosition;
import net.shadowmage.ancientwarfare.core.util.BlockTools;

public class ItemConstructionTool extends Item implements IItemClickable, IItemKeyInterface
{

public ItemConstructionTool(String regName)
  {
  this.setUnlocalizedName(regName);
  this.setTextureName("ancientwarfare:structure/"+regName);
  this.setCreativeTab(AWStructuresItemLoader.structureTab);
  }

/**
 * implement additional key functionality for item
 * right click = do fill / do offset fill
 * 
 * z = 
 * x =
 * c =
 * v =
 *  
 * layers based functionality
 *    right click         do fill
 *    shift right click   do fill (offset target side)
 *    key click           set block
 *    shift key click     noop
 *    
 * Bounds based
 *    right click         do fill
 *    shift right click   do fill (offset target side)
 *    key click           set position/block
 *    shift key click     toggle to next position/block
 *       
 * need ability to set pos1, pos2 and block for box type
 * 
 * need ability to set block type and replace block type for both types
 * 
 * need ability to clear bounds and change types
 * 
 * make four items:
 * lake fill
 *    
 * layer fill
 * solid fill
 * box fill
 */

@Override
public boolean onLeftClickClient(EntityPlayer player, ItemStack stack){return true;}

@Override
public void onLeftClick(EntityPlayer player, ItemStack stack)
  {
  if(player.isSneaking())
    {
    ConstructionSettings settings = getSettings(stack);
    settings.type = settings.type.next();
    writeConstructionSettings(stack, settings);
    AWLog.logDebug("set type to: "+settings.type);    
    }
  else
    {
    BlockPosition pos = BlockTools.getBlockClickedOn(player, player.worldObj, false);
    if(pos==null){return;}
    Block block = player.worldObj.getBlock(pos.x, pos.y, pos.z);
    int meta = player.worldObj.getBlockMetadata(pos.x, pos.y, pos.z);
    AWLog.logDebug("hit: "+pos+" block: "+block+" meta: "+meta);    
    }
  }

@Override
public boolean onRightClickClient(EntityPlayer player, ItemStack stack){return true;}//return true for send packet to act on server-side

@Override
public void onRightClick(EntityPlayer player, ItemStack stack)
  {
  //TODO
  }

public static ConstructionSettings getSettings(ItemStack item)
  {
  if(item.getItem()==AWStructuresItemLoader.constructionTool)
    {
    ConstructionSettings settings = new ConstructionSettings();
    if(item.hasTagCompound() && item.getTagCompound().hasKey("constructionSettings"))
      {
      settings.readFromNBT(item.getTagCompound().getCompoundTag("constructionSettings"));
      }
    return settings;
    }
  return null;
  }

public static void writeConstructionSettings(ItemStack item, ConstructionSettings settings)
  {
  if(item.getItem()==AWStructuresItemLoader.constructionTool)
    {
    item.setTagInfo("constructionSettings", settings.writeToNBT(new NBTTagCompound()));
    }
  }

public static final class ConstructionSettings
{
Block block;
int meta;
BlockPosition pos1;
BlockPosition pos2;
ConstructionType type = ConstructionType.SOLID_FILL;

protected void readFromNBT(NBTTagCompound tag)
  {
  if(tag.hasKey("pos1")){pos1=new BlockPosition(tag.getCompoundTag("pos1"));}
  if(tag.hasKey("pos2")){pos2=new BlockPosition(tag.getCompoundTag("pos2"));}
  if(tag.hasKey("block")){block = (Block) Block.blockRegistry.getObject(tag.getString("block"));}
  if(tag.hasKey("meta")){meta = tag.getInteger("meta");}
  if(tag.hasKey("type")){type = ConstructionType.values()[tag.getInteger("type")];}
  }

protected NBTTagCompound writeToNBT(NBTTagCompound tag)
  {
  if(block!=null){tag.setString("block", Block.blockRegistry.getNameForObject(block));}
  tag.setInteger("meta", meta);
  if(pos1!=null){tag.setTag("pos1", pos1.writeToNBT(new NBTTagCompound()));}
  if(pos2!=null){tag.setTag("pos2", pos2.writeToNBT(new NBTTagCompound()));}
  tag.setInteger("type", type.ordinal());
  return tag;
  }

}

public static enum ConstructionType
{

/**
 * Fills current layer and downwards with chosen block
 */
LAKE_FILL,

/**
 * fills current layer only with chosen block
 */
LAYER_FILL,

/**
 * fills entire bounding box with chosen block
 */
SOLID_FILL,

/**
 * creates a box around chosen area with chosen block
 */
BOX_FILL;

public ConstructionType next()
  {
  int ordinal = ordinal();
  ordinal++;
  if(ordinal>=values().length){ordinal=0;}
  return values()[ordinal];
  }
}

@Override
public boolean onKeyActionClient(EntityPlayer player, ItemStack stack, int keyIndex)
  {
  // TODO Auto-generated method stub
  return false;
  }


@Override
public void onKeyAction(EntityPlayer player, ItemStack stack, int keyIndex)
  {
  // TODO Auto-generated method stub
  
  }

}
