package net.shadowmage.ancientwarfare.npc.trade;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.shadowmage.ancientwarfare.core.util.BlockPosition;

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
  //TODO add deposit/withdraw entry info
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
  //TODO add deposit/withdraw entry info
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

public static class POTradeWithdrawEntry
{
private POTradeWithdrawType type = POTradeWithdrawType.ALL_OF;
private ItemStack filter;
}

public static class POTradeDepositEntry
{
private POTradeDepositType type = POTradeDepositType.ALL_OF;
private ItemStack filter;
}
}
