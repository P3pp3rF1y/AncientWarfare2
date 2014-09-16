package net.shadowmage.ancientwarfare.npc.trade;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;

public class FactionTradeList
{

private List<FactionTrade> tradeList = new ArrayList<FactionTrade>();
int ticks = 0;

public FactionTradeList()
  {
  // TODO Auto-generated constructor stub
  }

public void incrementTradePosition(int num){}// TODO Auto-generated stub
public void decrementTradePosition(int num){}// TODO Auto-generated stub
public void deleteTrade(int num){}// TODO Auto-generated stub
public void addNewTrade(){tradeList.add(new FactionTrade());}

public void tradeCompletedServer(FactionTrade trade){}// TODO Auto-generated stub
public void tradeCompletedClient(FactionTrade trade){}// TODO Auto-generated stub

/**
 * MUST be called from owning entity once per update tick.
 */
public void tick(){ticks++;}

/**
 * Should be called on server PRIOR to opening the trades GUI/container.<br>
 * Will use the internal stored tick number value for updating the trades list.<br>
 */
public void updateTradesForView()
  {
  for(int i = 0; i< tradeList.size(); i++){tradeList.get(i).updateTrade(ticks);}
  ticks = 0;
  }

/**
 * Add all available trades to the passed in list.<br>
 * Should be used on the client-side GUI to retrieve all trades.
 * @param trades
 */
public void getTrades(List<FactionTrade> trades){trades.addAll(tradeList);}

public void performTrade(EntityPlayer player, IInventory tradeInput, int tradeNum)
  {
  tradeList.get(tradeNum).performTrade(player, tradeInput);
  }

public NBTTagCompound writeToNBT(NBTTagCompound tag)
  {
  tag.setInteger("ticks", ticks);
  NBTTagList list = new NBTTagList();
  for(int i = 0; i < tradeList.size(); i++)
    {
    list.appendTag(tradeList.get(i).writeToNBT(new NBTTagCompound()));
    }
  tag.setTag("tradeList", list);
  return tag;
  }

public void readFromNBT(NBTTagCompound tag)
  {
  ticks = tag.getInteger("ticks");
  tradeList.clear();
  NBTTagList list = tag.getTagList("tradeList", Constants.NBT.TAG_COMPOUND);
  
  FactionTrade t;
  for(int i = 0; i < list.tagCount(); i++)
    {
    t = new FactionTrade();
    t.readFromNBT(list.getCompoundTagAt(i));
    tradeList.add(t);
    }
  }
}
