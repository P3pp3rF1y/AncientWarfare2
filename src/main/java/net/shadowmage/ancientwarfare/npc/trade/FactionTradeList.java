package net.shadowmage.ancientwarfare.npc.trade;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;

public final class FactionTradeList
{

private List<FactionTrade> tradeList = new ArrayList<FactionTrade>();
int ticks = 0;

public FactionTradeList(){}

/**
 * decreases the index of a trade and moves it up in the trade list
 * @param num the index position to move
 */
public void decrementTradePosition(int num)
  {
  if(num<=0 || num>=tradeList.size()){return;}
  FactionTrade trade = tradeList.remove(num);
  tradeList.add(num-1, trade);
  }

/**
 * increases the index of a trade and moves it down in the trade list
 * @param num the index position to move
 */
public void incrementTradePosition(int num)
  {
  if(num>=tradeList.size()-1){return;}
  FactionTrade trade = tradeList.remove(num);
  tradeList.add(num+1, trade);
  }

/**
 * deletes a trade from the trade list
 * @param num the index of the trade to remove
 */
public void deleteTrade(int num)
  {
  if(num<0 || num>=tradeList.size()){return;}
  tradeList.remove(num);
  }

public void addNewTrade(){tradeList.add(new FactionTrade());}

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
 * removes any trades that have no input or output items.<br>
 * should be called before the changed list is sent from client->server from setup GUI.
 */
public void removeEmptyTrades()
  {
  Iterator<FactionTrade> it = tradeList.iterator();
  FactionTrade t;
  boolean hasItems = false;
  while(it.hasNext() && (t=it.next())!=null)
    {
    hasItems = false;
    for(int i = 0; i < 9; i++)
      {
      if(t.getInput()[i]!=null || t.getOutput()[i]!=null)
        {
        hasItems=true;
        break;
        }      
      }    
    if(!hasItems){it.remove();}
    }
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
