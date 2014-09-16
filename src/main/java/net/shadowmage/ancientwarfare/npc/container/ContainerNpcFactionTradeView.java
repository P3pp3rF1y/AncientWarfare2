package net.shadowmage.ancientwarfare.npc.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.nbt.NBTTagCompound;
import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.core.inventory.InventoryBasic;
import net.shadowmage.ancientwarfare.core.util.InventoryTools;
import net.shadowmage.ancientwarfare.npc.entity.faction.NpcFactionTrader;
import net.shadowmage.ancientwarfare.npc.trade.FactionTradeList;

public class ContainerNpcFactionTradeView extends ContainerBase
{

public final NpcFactionTrader trader;
public FactionTradeList tradeList;
public final IInventory tradeInput = new InventoryBasic(9);

public ContainerNpcFactionTradeView(EntityPlayer player, int x, int y, int z)
  {
  super(player, x, y, z);
  trader = (NpcFactionTrader) player.worldObj.getEntityByID(x);//will crash if something is fubar on entity-ids, probably not a bad thing
  this.tradeList = trader.getTradeList();
  this.trader.trader = player;
  
  int startY = 240-4-8-4*18;
  int gx=0, gy=0, sx, sy;
  for(int i = 0; i < 9; i++)
    {
    sx = gx*18 + 8 + 9*18 + 18;
    sy = gy*18 + startY + 16;
    addSlotToContainer(new Slot(tradeInput, i, sx, sy));
    gx++;
    if(gx>=3)
      {
      gx=0;
      gy++;
      }
    if(gy>=3){break;}
    }
  
  addPlayerSlots(player, 8, startY, 4);
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
  if(tag.hasKey("doTrade")){tradeList.performTrade(player, tradeInput, tag.getInteger("doTrade"));}//TODO add inv ref
  refreshGui();
  }

@Override
public void onContainerClosed(EntityPlayer player)
  {
  this.trader.trader = null;
  super.onContainerClosed(player);
  if(!player.worldObj.isRemote)
    {
    InventoryTools.dropInventoryInWorld(player.worldObj, tradeInput, player.posX, player.posY, player.posZ);
    }
  }

}
