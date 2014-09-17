package net.shadowmage.ancientwarfare.npc.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.npc.entity.faction.NpcFactionTrader;
import net.shadowmage.ancientwarfare.npc.trade.FactionTradeList;

public class ContainerNpcFactionTradeSetup extends ContainerBase
{

public final NpcFactionTrader trader;
public FactionTradeList tradeList;
public boolean tradesChanged = false;
public ContainerNpcFactionTradeSetup(EntityPlayer player, int x, int y, int z)
  {
  super(player, x, y, z);
  trader = (NpcFactionTrader) player.worldObj.getEntityByID(x);//will crash if something is fubar on entity-ids, probably not a bad thing
  this.tradeList = trader.getTradeList();
  this.trader.trader = player;
  
  addPlayerSlots(player, 8, 240-4-8-4*18, 4);
  }

@Override
public void sendInitData()
  {    
  tradeList.updateTradesForView();
  NBTTagCompound tag = new NBTTagCompound();
  tradeList.writeToNBT(tag);
  
  NBTTagCompound packetTag = new NBTTagCompound();
  packetTag.setTag("tradeData", tag);
  sendDataToClient(packetTag);
  }

@Override
public void handlePacketData(NBTTagCompound tag)
  {
  if(tag.hasKey("tradeData")){tradeList.readFromNBT(tag.getCompoundTag("tradeData"));}
  refreshGui();
  }

@Override
public void onContainerClosed(EntityPlayer p_75134_1_)
  {
  this.trader.trader = null;
  super.onContainerClosed(p_75134_1_);
  }

public void onGuiClosed()
  {
  if(player.worldObj.isRemote && tradesChanged)
    {
    tradeList.removeEmptyTrades();
    NBTTagCompound tag = new NBTTagCompound();
    tradeList.writeToNBT(tag);
    NBTTagCompound packetTag = new NBTTagCompound();
    packetTag.setTag("tradeData", tag);
    sendDataToServer(packetTag);
    }  
  }

}
