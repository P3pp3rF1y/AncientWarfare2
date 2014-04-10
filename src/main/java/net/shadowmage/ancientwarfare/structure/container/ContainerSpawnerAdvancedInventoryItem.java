package net.shadowmage.ancientwarfare.structure.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.shadowmage.ancientwarfare.structure.tile.SpawnerSettings;

public class ContainerSpawnerAdvancedInventoryItem extends ContainerSpawnerAdvancedInventoryBase
{

public ContainerSpawnerAdvancedInventoryItem(EntityPlayer player, int x, int y, int z)
  {
  super(player, x, y, z);
  
  settings = new SpawnerSettings();
  ItemStack item = player.inventory.getCurrentItem();
  if(item==null || !item.hasTagCompound() || !item.getTagCompound().hasKey("spawnerSettings")){throw new IllegalArgumentException("stack cannot be null, and must have tag compounds!!");}
  settings.readFromNBT(item.getTagCompound().getCompoundTag("spawnerSettings"));
  inventory = settings.getInventory();
  
  this.addSettingsInventorySlots();
  this.addPlayerSlots(player, 8, 70, 8);
  }

@Override
public void handlePacketData(NBTTagCompound tag)
  {
  if(tag.hasKey("spawnerSettings"))
    {
    ItemStack item = player.inventory.getCurrentItem();
    item.setTagInfo("spawnerSettings", tag.getCompoundTag("spawnerSettings"));
    }
  }

}
