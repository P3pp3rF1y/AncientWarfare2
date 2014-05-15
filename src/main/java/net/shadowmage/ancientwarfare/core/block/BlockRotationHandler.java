package net.shadowmage.ancientwarfare.core.block;

import java.util.EnumSet;
import java.util.HashMap;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraftforge.common.util.ForgeDirection;
import net.shadowmage.ancientwarfare.core.config.AWLog;
import net.shadowmage.ancientwarfare.core.inventory.IInventorySaveable;
import net.shadowmage.ancientwarfare.core.inventory.ItemSlotFilter;
import net.shadowmage.ancientwarfare.core.util.BlockTools;
import net.shadowmage.ancientwarfare.core.util.InventoryTools;

public class BlockRotationHandler
{

public static int getRotatedMeta(IRotatableBlock block, int meta, ForgeDirection axis)
  {
  RotationType t = block.getRotationType();
  if(t==RotationType.NONE){return meta;}
  ForgeDirection rotator = t==RotationType.FOUR_WAY ? ForgeDirection.DOWN : axis;
  ForgeDirection face = ForgeDirection.getOrientation(meta);
  face = face.getRotation(rotator);
  return face.ordinal();
  }

public static int getMetaForPlacement(EntityLivingBase entity, IRotatableBlock block)
  {
  if(block.getRotationType()==RotationType.NONE){return 0;}
  int f = BlockTools.getPlayerFacingFromYaw(entity.rotationYaw);
  ForgeDirection face = BlockTools.getForgeDirectionFromFacing(f);
  if(block.getRotationType()==RotationType.SIX_WAY)
    {
//    if(sideHit==0 || sideHit==1)
//      {
//      face = ForgeDirection.getOrientation(sideHit);
//      }
    //TODO figure this crap out
    }  
  AWLog.logDebug("returning facing for block: "+face);
  return face.ordinal();
  }

public interface IRotatableBlock
{
public RotationType getRotationType();
}

public static enum RotationType
{
/**
 * Can have 6 textures / inventories.<br>
 * Top, Bottom, Front, Rear, Left, Right<br>
 * Can only face in one of four-directions - N/S/E/W
 */
FOUR_WAY(EnumSet.of(RelativeSide.TOP, RelativeSide.BOTTOM, RelativeSide.LEFT, RelativeSide.RIGHT, RelativeSide.FRONT, RelativeSide.REAR)),
/**
 * Can have 3 textures / inventories<br>
 * Top, Bottom, Sides<br>
 * Can face in any orientation - U/D/N/S/E/W
 */
SIX_WAY(EnumSet.of(RelativeSide.TOP, RelativeSide.BOTTOM, RelativeSide.ANY_SIDE)),
/**
 * No rotation, can still have relative sides, but FRONT always == NORTH
 */
NONE(EnumSet.of(RelativeSide.TOP, RelativeSide.BOTTOM, RelativeSide.LEFT, RelativeSide.RIGHT, RelativeSide.FRONT, RelativeSide.REAR));
RotationType(EnumSet<RelativeSide> sides){validSides=sides;}
EnumSet<RelativeSide> validSides;
public EnumSet<RelativeSide> getValidSides()
  {
  return validSides;
  }
}

public static enum InventorySide
{
BOTTOM,TOP,FRONT,REAR,LEFT,RIGHT,NONE;
}

public static enum RelativeSide
{

BOTTOM,TOP,FRONT,REAR,LEFT,RIGHT,ANY_SIDE;

private static final int DOWN = 0;
private static final int UP = 1;
private static final int NORTH = 2;
private static final int SOUTH = 3;
private static final int WEST = 4;
private static final int EAST = 5;
public static final RelativeSide[][] sixWayMap = new RelativeSide[6][6];
public static final RelativeSide[][] fourWayMap = new RelativeSide[6][6];
static
{
//D,U,N,S,W,E
//[side-viewed][block-facing]=relative side viewed
//fourWayMap[X][0-1] SHOULD BE NEVER REFERENCED AS BLOCK CAN NEVER POINT U/D
sixWayMap[DOWN][0]=TOP;
sixWayMap[DOWN][1]=BOTTOM;
sixWayMap[DOWN][2]=ANY_SIDE;
sixWayMap[DOWN][3]=ANY_SIDE;
sixWayMap[DOWN][4]=ANY_SIDE;
sixWayMap[DOWN][5]=ANY_SIDE;

sixWayMap[UP][0]=BOTTOM;
sixWayMap[UP][1]=TOP;
sixWayMap[UP][2]=ANY_SIDE;
sixWayMap[UP][3]=ANY_SIDE;
sixWayMap[UP][4]=ANY_SIDE;
sixWayMap[UP][5]=ANY_SIDE;

sixWayMap[NORTH][0]=ANY_SIDE;
sixWayMap[NORTH][1]=ANY_SIDE;
sixWayMap[NORTH][2]=TOP;
sixWayMap[NORTH][3]=BOTTOM;
sixWayMap[NORTH][4]=ANY_SIDE;
sixWayMap[NORTH][5]=ANY_SIDE;

sixWayMap[SOUTH][0]=ANY_SIDE;
sixWayMap[SOUTH][1]=ANY_SIDE;
sixWayMap[SOUTH][2]=BOTTOM;
sixWayMap[SOUTH][3]=TOP;
sixWayMap[SOUTH][4]=ANY_SIDE;
sixWayMap[SOUTH][5]=ANY_SIDE;

sixWayMap[WEST][0]=ANY_SIDE;
sixWayMap[WEST][1]=ANY_SIDE;
sixWayMap[WEST][2]=ANY_SIDE;
sixWayMap[WEST][3]=ANY_SIDE;
sixWayMap[WEST][4]=TOP;
sixWayMap[WEST][5]=BOTTOM;

sixWayMap[EAST][0]=ANY_SIDE;
sixWayMap[EAST][1]=ANY_SIDE;
sixWayMap[EAST][2]=ANY_SIDE;
sixWayMap[EAST][3]=ANY_SIDE;
sixWayMap[EAST][4]=BOTTOM;
sixWayMap[EAST][5]=TOP;

fourWayMap[DOWN][0] = ANY_SIDE;
fourWayMap[DOWN][1] = ANY_SIDE;
fourWayMap[DOWN][2] = BOTTOM;
fourWayMap[DOWN][3] = BOTTOM;
fourWayMap[DOWN][WEST] = BOTTOM;
fourWayMap[DOWN][EAST] = BOTTOM;

fourWayMap[UP][0] = ANY_SIDE;
fourWayMap[UP][1] = ANY_SIDE;
fourWayMap[UP][2] = TOP;
fourWayMap[UP][3] = TOP;
fourWayMap[UP][WEST] = TOP;
fourWayMap[UP][EAST] = TOP;

fourWayMap[NORTH][0] = ANY_SIDE;
fourWayMap[NORTH][1] = ANY_SIDE;
fourWayMap[NORTH][NORTH] = FRONT;
fourWayMap[NORTH][SOUTH] = REAR;
fourWayMap[NORTH][WEST] = RIGHT;
fourWayMap[NORTH][EAST] = LEFT;

fourWayMap[SOUTH][0] = ANY_SIDE;
fourWayMap[SOUTH][1] = ANY_SIDE;
fourWayMap[SOUTH][NORTH] = REAR;
fourWayMap[SOUTH][SOUTH] = FRONT;
fourWayMap[SOUTH][WEST] = LEFT;
fourWayMap[SOUTH][EAST] = RIGHT;

fourWayMap[WEST][0] = ANY_SIDE;
fourWayMap[WEST][1] = ANY_SIDE;
fourWayMap[WEST][NORTH] = LEFT;
fourWayMap[WEST][SOUTH] = RIGHT;
fourWayMap[WEST][WEST] = FRONT;
fourWayMap[WEST][EAST] = REAR;

fourWayMap[EAST][0] = ANY_SIDE;
fourWayMap[EAST][1] = ANY_SIDE;
fourWayMap[EAST][NORTH] = RIGHT;
fourWayMap[EAST][SOUTH] = LEFT;
fourWayMap[EAST][WEST] = REAR;
fourWayMap[EAST][EAST] = FRONT;
}

public static RelativeSide getSideViewed(IRotatableBlock block, int meta, int side)
  {
  RotationType t = block.getRotationType();
  if(t==RotationType.FOUR_WAY)
    {
    return fourWayMap[side][meta];
    }
  else if(t==RotationType.SIX_WAY)
    {
    return sixWayMap[side][meta];
    }
  return ANY_SIDE;
  }
}

public static final class IconRotationMap
{
private HashMap<RelativeSide, String> texNames = new HashMap<RelativeSide, String>();
private HashMap<RelativeSide, IIcon> icons = new HashMap<RelativeSide, IIcon>(); 

public void setIcon(IRotatableBlock block, RelativeSide side, String texName)
  {
  RotationType t = block.getRotationType();
  if(t==RotationType.NONE)
    {
    //TODO throw error message about improper block-rotatation type, perhaps just register the string as ALL_SIDES
    }
  else if(t==RotationType.SIX_WAY)
    {
    if(side!=RelativeSide.TOP && side!=RelativeSide.BOTTOM && side!=RelativeSide.ANY_SIDE)
      {
      //TODO throw error message about improper block-rotation / cannot map specific sides on a six-way
      }
    }
  texNames.put(side, texName);
  }

public void registerIcons(IIconRegister register)
  {
  String name;
  for(RelativeSide key : texNames.keySet())
    {
    name = texNames.get(key);
    icons.put(key, register.registerIcon(name));
    }
  }

public IIcon getIcon(IRotatableBlock block, int meta, int side)
  {
  RelativeSide rSide = RelativeSide.getSideViewed(block, meta, side);
  return icons.get(rSide);
  }

}

public static final class InventorySided implements IInventory, ISidedInventory, IInventorySaveable
{
/**
 * what inventory side is accessible from what block-side
 */
HashMap<RelativeSide, InventorySide> accessMap = new HashMap<RelativeSide, InventorySide>();
HashMap<RelativeSide, InventorySide> accessMapDefault = new HashMap<RelativeSide, InventorySide>();//used to reset access map by user to default settings
HashMap<InventorySide, int[]> slotsByInventorySide = new HashMap<InventorySide, int[]>();
HashMap<InventorySide, boolean[]> extractInsertFlags = new HashMap<InventorySide, boolean[]>();//inventoryside x boolean[2]; [0]=extract, [1]=insert
//InventorySide[] inventoryOrder //for use by GUI for ordering slots on screen and ordering of item-merging from container
//HashMap<InventorySide, InventorySideLayout> sideLayout //denotes the x,y,w of where the sides slots should be in container/gui
TileEntity te;
IRotatableBlock block;
ItemStack[] inventorySlots;
ItemSlotFilter[] filtersByInventorySlot;

public InventorySided(TileEntity te, IRotatableBlock block, int inventorySize)
  {
  this.te = te;
  this.block = block;
  //TODO throw error if either is null
  inventorySlots = new ItemStack[inventorySize];
  filtersByInventorySlot = new ItemSlotFilter[inventorySize];
  for(RelativeSide rSide : block.getRotationType().getValidSides())
    {
    setAccessibleSideDefault(rSide, InventorySide.NONE);
    }
  }

/**
 * Should be called to configure the default access directly after construction of the inventory
 * @param rSide
 * @param iSide
 */
public void setAccessibleSideDefault(RelativeSide rSide, InventorySide iSide)
  {
  if(rSide==null || iSide==null){}
  //TODO throw error if either is null, or NONE
  accessMapDefault.put(rSide, iSide);
  accessMap.put(rSide, iSide);
  }

/**
 * may be called by user/dynamically to reconfigure the inventory access in real-time.  Only has any effect on server.
 * @param rSide
 * @param iSide
 */
public void setAccessibleSide(RelativeSide rSide, InventorySide iSide)
  {
  if(rSide==null || iSide==null){}
  //TODO throw error if either is null, or NONE
  accessMap.put(rSide, iSide);
  }

public void setInventoryIndices(InventorySide side, int[] indices)
  {
  //TODO throw error if either is null, or NONE
  slotsByInventorySide.put(side, indices);  
  }

public void setFilterForSlots(ItemSlotFilter filter, int[] indices)
  {
  for(int i : indices)
    {
    filtersByInventorySlot[i]=filter;
    }
  }

public void setExtractInsertFlags(InventorySide side, boolean[] flags)
  {
  //TODO throw error if either is null, or NONE
  extractInsertFlags.put(side, flags);
  }

public InventorySide getInventorySide(int mcSide)
  {
  int meta = te.getBlockMetadata();
  RelativeSide rSide = RelativeSide.getSideViewed(block, meta, mcSide);
  return accessMap.get(rSide);
  }

@Override
public int[] getAccessibleSlotsFromSide(int var1)
  {
  InventorySide iSide = getInventorySide(var1);
  return iSide==null ? null : slotsByInventorySide.get(iSide);
  }

@Override
public boolean canInsertItem(int slot, ItemStack var2, int mcSide)
  {
  InventorySide iSide = getInventorySide(mcSide);
  if(iSide==null){return false;}
  boolean[] flags = extractInsertFlags.get(iSide);
  if(flags!=null && !flags[1]){return false;}
  return isItemValidForSlot(slot, var2);
  }

@Override
public boolean canExtractItem(int slot, ItemStack var2, int mcSide)
  {
  InventorySide iSide = getInventorySide(mcSide);
  if(iSide==null){return false;}
  boolean[] flags = extractInsertFlags.get(iSide);
  return flags!=null ? flags[0] : true;
  }

@Override
public int getSizeInventory()
  {
  return inventorySlots.length;
  }

@Override
public ItemStack getStackInSlot(int var1)
  {
  return inventorySlots[var1];
  }

@Override
public ItemStack decrStackSize(int var1, int var2)
  {
  ItemStack stack = inventorySlots[var1];
  if(stack!=null)
    {
    int qty = var2 > stack.stackSize? stack.stackSize : var2;
    stack.stackSize -= qty;    
    ItemStack returnStack = stack.copy();
    returnStack.stackSize = qty;
    if(stack.stackSize<=0)      
      {
      inventorySlots[var1]=null;
      }
    if(returnStack.stackSize<=0){returnStack=null;}
    return returnStack;
    }
  return null;
  }

@Override
public ItemStack getStackInSlotOnClosing(int var1)
  {
  ItemStack stack = inventorySlots[var1];
  inventorySlots[var1]=null;
  return stack;
  }

@Override
public void setInventorySlotContents(int var1, ItemStack var2)
  {
  inventorySlots[var1]=var2;
  }

@Override
public String getInventoryName()
  {
  return "aw_inventory_sided";
  }

@Override
public boolean hasCustomInventoryName()
  {  
  return false;
  }

@Override
public int getInventoryStackLimit()
  {
  return 64;
  }

@Override
public void markDirty()
  {
  te.markDirty();
  }

@Override
public boolean isUseableByPlayer(EntityPlayer var1)
  {
  return true;
  }

@Override
public void openInventory()
  {
  }

@Override
public void closeInventory()
  {
  }

@Override
public boolean isItemValidForSlot(int var1, ItemStack var2)
  {
  ItemSlotFilter filter = filtersByInventorySlot[var1];
  if(filter!=null){return filter.isItemValid(var2);}
  return true;
  }

@Override
public void readFromNBT(NBTTagCompound tag)
  {
  InventoryTools.readInventoryFromNBT(this, tag);
  NBTTagCompound accessTag = tag.getCompoundTag("accessTag");
  int[] rMap = accessTag.getIntArray("rMap");
  int[] iMap = accessTag.getIntArray("iMap");
  RelativeSide rSide;
  InventorySide iSide;
  for(int i = 0; i <rMap.length && i<iMap.length; i++)
    {
    rSide = RelativeSide.values()[rMap[i]];
    iSide = InventorySide.values()[iMap[i]];
    accessMap.put(rSide, iSide);
    }
  }

@Override
public NBTTagCompound writeToNBT(NBTTagCompound tag)
  {
  InventoryTools.writeInventoryToNBT(this, tag);
  int l = accessMap.size();
  int rMap[] = new int[l];
  int iMap[] = new int[l];  
  int index = 0;
  for(RelativeSide rSide : accessMap.keySet())
    {
    rMap[index]=rSide.ordinal();
    iMap[index]=accessMap.get(rSide).ordinal();
    }
  NBTTagCompound accessTag = new NBTTagCompound();
  accessTag.setIntArray("rMap", rMap);
  accessTag.setIntArray("iMap", iMap);
  tag.setTag("accessTag", accessTag);  
  return tag;
  }

}

}
