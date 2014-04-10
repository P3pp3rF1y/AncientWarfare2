package net.shadowmage.ancientwarfare.structure.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.core.inventory.InventoryBasic;
import net.shadowmage.ancientwarfare.core.inventory.InventorySided.SlotItemFilter;
import net.shadowmage.ancientwarfare.core.inventory.SlotFiltered;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.network.PacketGui;
import net.shadowmage.ancientwarfare.structure.item.AWStructuresItemLoader;
import net.shadowmage.ancientwarfare.structure.tile.SpawnerSettings;

public class ContainerSpawnerAdvancedInventoryBase extends ContainerBase
{

SpawnerSettings settings;
InventoryBasic inventory;

public ContainerSpawnerAdvancedInventoryBase(EntityPlayer player, int x, int y, int z)
  {
  super(player, x, y, z);  
  }

protected void addSettingsInventorySlots()
  {
  int xPos;
  int yPos;
  int slotNum;
  
  SlotItemFilter filter = new SlotItemFilter()
    {
    @Override
    public boolean isItemValid(ItemStack stack)
      {      
      if(stack!=null && stack.getItem() instanceof ItemBlock)
        {
        ItemBlock block = (ItemBlock)stack.getItem();
        if(block.field_150939_a==AWStructuresItemLoader.spawnerBlock)
          {
          return false;
          }
        }
      return true;
      }
    };
    
  for(int y = 0; y<3; y++)
    {
    yPos = y*18 + 8;
    for(int x = 0; x<3; x++)
      {
      xPos = x*18 + 8;//TODO find offset
      slotNum = y*3 + x;
      addSlotToContainer(new SlotFiltered(inventory, slotNum, xPos, yPos, filter));
      }
    }
  }

public void sendSettingsToServer()
  {
  NBTTagCompound tag = new NBTTagCompound();
  settings.writeToNBT(tag);
  
  PacketGui pkt = new PacketGui();
  pkt.packetData.setTag("spawnerSettings", tag);
  NetworkHandler.sendToServer(pkt);
  }

}
