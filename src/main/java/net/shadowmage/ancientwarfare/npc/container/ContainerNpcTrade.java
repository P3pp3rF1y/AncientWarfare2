package net.shadowmage.ancientwarfare.npc.container;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.shadowmage.ancientwarfare.core.inventory.InventoryBasic;
import net.shadowmage.ancientwarfare.core.util.InventoryTools;
import net.shadowmage.ancientwarfare.npc.config.AWNPCStatics;
import net.shadowmage.ancientwarfare.npc.entity.faction.NpcFaction;
import net.shadowmage.ancientwarfare.npc.faction.FactionTracker;
import net.shadowmage.ancientwarfare.npc.trade.NpcTrade;
import net.shadowmage.ancientwarfare.npc.trade.NpcTradeManager;

public class ContainerNpcTrade extends ContainerNpcBase
{

List<NpcTrade> tradeList = new ArrayList<NpcTrade>();
int tradeIndex = 0;
public NpcTrade currentTrade;

InventoryBasic inventory = new InventoryBasic(9);
InventoryBasic result = new InventoryBasic(1);

public ContainerNpcTrade(EntityPlayer player, int x, int y, int z)
  {
  super(player, x, y, z);  
  int npcLevel = npc.getLevelingStats().getBaseLevel();
  if(npc instanceof NpcFaction)
    {
    int standing = FactionTracker.INSTANCE.getStandingFor(player.worldObj, player.getCommandSenderName(), ((NpcFaction)npc).getFaction());
    if(standing<=0){npcLevel=0;}
    }
  NpcTradeManager.INSTANCE.getTradesFor(npc.getNpcType(), tradeList, npcLevel, npc.getUniqueID().getLeastSignificantBits());
  if(!tradeList.isEmpty())
    {
    currentTrade = tradeList.get(0);
    }
  for(int i = 0; i <9; i++)
    {
    addSlotToContainer(new Slot(inventory, i, (i%3)*18+8+18, (i/3)*18 +8+8+3*18+12+8+2)
      {
      @Override
      public void onSlotChanged()
        {
        updateTrade();
        }
      });
    }
  
  addSlotToContainer(new Slot(result, 0, 8+7*18, 8+8+2*18+12+8+18+18+2)
    {
    @Override
    public void onPickupFromSlot(EntityPlayer par1EntityPlayer, ItemStack par2ItemStack)
      {
      super.onPickupFromSlot(par1EntityPlayer, par2ItemStack);
      currentTrade.removeItems(ContainerNpcTrade.this.inventory);
      npc.addExperience(AWNPCStatics.npcXpFromTrade);
      updateTrade();
      }
    @Override
    public boolean isItemValid(ItemStack par1ItemStack)
      {
      return false;
      }
    });
  
  addPlayerSlots(player, 8, 8+8+2*18+12+8+18+18+18+8+18+2, 4);
  updateTrade();
  }

public void nextTrade()
  {
  tradeIndex++;
  if(tradeIndex>=tradeList.size()){tradeIndex=0;}
  currentTrade = tradeList.isEmpty() ? null : tradeList.get(tradeIndex);
  updateTrade();
  NBTTagCompound tag = new NBTTagCompound();
  tag.setInteger("tradeIndex", tradeIndex);
  sendDataToServer(tag);
  }

public void prevTrade()
  {
  tradeIndex--;
  if(tradeIndex<0){tradeIndex=tradeList.size()-1;}
  if(tradeIndex<0){tradeIndex=0;}
  currentTrade = tradeList.isEmpty() ? null : tradeList.get(tradeIndex);
  updateTrade();
  NBTTagCompound tag = new NBTTagCompound();
  tag.setInteger("tradeIndex", tradeIndex);
  sendDataToServer(tag);
  }

/**
 * should be called to update the output whenever the trade changes or input inventory changes or item taken from output slot
 */
public void updateTrade()
  {
  if(currentTrade!=null)
    {
    if(currentTrade.hasItems(inventory))
      {
      result.setInventorySlotContents(0, currentTrade.getResult().copy());
      }
    else
      {
      result.setInventorySlotContents(0, null);
      }
    }
  else
    {
    result.setInventorySlotContents(0, null);
    }
  }

@Override
public void handlePacketData(NBTTagCompound tag)
  {
  if(tag.hasKey("tradeIndex"))
    {
    this.tradeIndex = tag.getInteger("tradeIndex");
    if(this.tradeIndex>=this.tradeList.size()){this.tradeIndex=0;}
    if(this.tradeIndex<0){this.tradeIndex=0;}
    currentTrade = tradeList.isEmpty() ? null : tradeList.get(tradeIndex);
    updateTrade();
    }
  }

@Override
public void onContainerClosed(EntityPlayer player)
  {  
  super.onContainerClosed(player);
  if(!player.worldObj.isRemote)
    {
    InventoryTools.dropInventoryInWorld(player.worldObj, inventory, player.posX, player.posY, player.posZ);    
    }
  }

}
