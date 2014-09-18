package net.shadowmage.ancientwarfare.npc.trade;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;
import net.shadowmage.ancientwarfare.core.util.BlockPosition;
import net.shadowmage.ancientwarfare.core.util.InventoryTools;

public class POTradeRestockData
{
private BlockPosition withdrawPoint;
private int withdrawSide;
private List<POTradeWithdrawEntry> withdrawList = new ArrayList<POTradeWithdrawEntry>();
private BlockPosition depositPoint;
private int depositSide;
private List<POTradeDepositEntry> depositList = new ArrayList<POTradeDepositEntry>();

public BlockPosition getDepositPoint(){return depositPoint;}

public BlockPosition getWithdrawPoint(){return withdrawPoint;}

public int getDepositSide(){return depositSide;}

public int getWithdrawSide(){return withdrawSide;}

public void deleteDepositPoint(){depositPoint=null;}

public void deleteWithdrawPoint(){withdrawPoint=null;}

public List<POTradeWithdrawEntry> getWithdrawList(){return withdrawList;}

public List<POTradeDepositEntry> getDepositList(){return depositList;}

public void addDepositEntry(){depositList.add(new POTradeDepositEntry());}

public void addWithdrawEntry(){withdrawList.add(new POTradeWithdrawEntry());}

public void removeDepositEntry(int index){depositList.remove(index);}

public void removeWithdrawEntry(int index){withdrawList.remove(index);}

public void setDepositPoint(BlockPosition pos, int side)
  {
  depositPoint = pos;
  depositSide = side;
  }

public void setWithdrawPoint(BlockPosition pos, int side)
  {
  withdrawPoint = pos;
  withdrawSide = side;
  }

public void readFromNBT(NBTTagCompound tag)
  {
  if(tag.hasKey("withdrawPoint"))
    {
    withdrawPoint = new BlockPosition(tag.getCompoundTag("withdrawPoint"));
    withdrawSide = tag.getInteger("withdrawSide");    
    }
  if(tag.hasKey("depositPoint"))
    {
    depositPoint = new BlockPosition(tag.getCompoundTag("depositPoint"));
    depositSide = tag.getInteger("depositSide");
    }
  
  NBTTagList deposit = tag.getTagList("depositList", Constants.NBT.TAG_COMPOUND);
  POTradeDepositEntry de;
  for(int i = 0; i < deposit.tagCount(); i++)
    {
    de = new POTradeDepositEntry();
    de.readFromNBT(deposit.getCompoundTagAt(i));
    this.depositList.add(de);
    }
  
  NBTTagList withdraw = tag.getTagList("withdrawList", Constants.NBT.TAG_COMPOUND);
  POTradeWithdrawEntry we;
  for(int i = 0; i < withdraw.tagCount(); i++)
    {
    we = new POTradeWithdrawEntry();
    we.readFromNBT(deposit.getCompoundTagAt(i));
    this.withdrawList.add(we);
    }
  }

public NBTTagCompound writeToNBT(NBTTagCompound tag)
  {
  if(withdrawPoint!=null)
    {
    tag.setTag("withdrawPoint", withdrawPoint.writeToNBT(new NBTTagCompound()));
    tag.setInteger("withdrawSide", withdrawSide);
    }
  if(depositPoint!=null)
    {
    tag.setTag("depositPoint", depositPoint.writeToNBT(new NBTTagCompound()));
    tag.setInteger("depositSide", depositSide);
    }
  
  NBTTagList depositTagList = new NBTTagList();
  for(int i = 0; i < this.depositList.size(); i++)
    {
    depositTagList.appendTag(this.depositList.get(i).writeToNBT(new NBTTagCompound()));
    }
  tag.setTag("depositList", depositTagList);
  
  NBTTagList withdrawTagList = new NBTTagList();
  for(int i = 0; i < this.withdrawList.size(); i++)
    {
    withdrawTagList.appendTag(this.withdrawList.get(i).writeToNBT(new NBTTagCompound()));
    }
  tag.setTag("withdrawList", withdrawTagList);
  return tag;
  }

public static enum POTradeWithdrawType
{
ALL_OF,
QUANTITY,
FILL_TO
}

public static enum POTradeDepositType
{
ALL_OF,
QUANTITY,
DEPOSIT_EXCESS
}

public static final class POTradeWithdrawEntry
{
private POTradeWithdrawType type = POTradeWithdrawType.ALL_OF;
private ItemStack filter;
public ItemStack getFilter(){return filter;}
public void setFilter(ItemStack stack){filter=stack;}
public void setType(POTradeWithdrawType type){this.type=type==null? POTradeWithdrawType.ALL_OF : type;}
public POTradeWithdrawType getType(){return type;}
public void toggleType()
  {
  int o = type.ordinal();
  o++;
  if(o>=POTradeWithdrawType.values().length){o=0;}
  this.type = POTradeWithdrawType.values()[o];
  }
public void readFromNBT(NBTTagCompound tag)
  {
  if(tag.hasKey("item")){filter = InventoryTools.readItemStack(tag.getCompoundTag("item"));}
  type = POTradeWithdrawType.values()[tag.getInteger("type")];
  }
public NBTTagCompound writeToNBT(NBTTagCompound tag)
  {
  if(filter!=null){tag.setTag("item", InventoryTools.writeItemStack(filter, new NBTTagCompound()));}
  tag.setInteger("type", type.ordinal());
  return tag;
  }
}

public static final class POTradeDepositEntry
{
private POTradeDepositType type = POTradeDepositType.ALL_OF;
private ItemStack filter;
public ItemStack getFilter(){return filter;}
public void setFilter(ItemStack stack){filter=stack;}
public void setType(POTradeDepositType type){this.type=type==null? POTradeDepositType.ALL_OF : type;}
public POTradeDepositType getType(){return type;}
public void toggleType()
  {
  int o = type.ordinal();
  o++;
  if(o>=POTradeDepositType.values().length){o=0;}
  this.type = POTradeDepositType.values()[o];
  }
public void readFromNBT(NBTTagCompound tag)
  {
  if(tag.hasKey("item")){filter = InventoryTools.readItemStack(tag.getCompoundTag("item"));}
  type = POTradeDepositType.values()[tag.getInteger("type")];
  }
public NBTTagCompound writeToNBT(NBTTagCompound tag)
  {
  if(filter!=null){tag.setTag("item", InventoryTools.writeItemStack(filter, new NBTTagCompound()));}
  tag.setInteger("type", type.ordinal());
  return tag;
  }
}

}
