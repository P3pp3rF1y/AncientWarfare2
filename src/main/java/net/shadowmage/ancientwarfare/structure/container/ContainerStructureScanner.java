package net.shadowmage.ancientwarfare.structure.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.shadowmage.ancientwarfare.core.config.AWLog;
import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.structure.item.AWStructuresItemLoader;
import net.shadowmage.ancientwarfare.structure.item.ItemStructureScanner;
import net.shadowmage.ancientwarfare.structure.item.ItemStructureSettings;

public class ContainerStructureScanner extends ContainerBase
{

ItemStructureSettings settings = new ItemStructureSettings();

public ContainerStructureScanner(EntityPlayer player, int x, int y, int z)
  {
  super(player, x, y, z );
  if(player.worldObj.isRemote)
    {
    return;
    }
  ItemStack builderItem = player.inventory.getCurrentItem();
  if(builderItem==null || builderItem.getItem()==null || builderItem.getItem()!=AWStructuresItemLoader.scanner)
    {
    return;
    } 
  ItemStructureSettings.getSettingsFor(builderItem, settings);
  }

@Override
public void handlePacketData(NBTTagCompound tag)
  {
  if(tag.hasKey("export"))
    {
    AWLog.logDebug("receiving export command");
    boolean include = tag.getBoolean("export");
    String name = tag.getString("name");
    NBTTagCompound validation = tag.getCompoundTag("validation");
    ItemStructureScanner.scanStructure(player.worldObj, settings.pos1(), settings.pos2(), settings.buildKey(), settings.face(), name, include, validation);
    settings.clearSettings();    
    }
  if(tag.hasKey("reset"))
    {
    settings.clearSettings();
    }
  }

@Override
public void onContainerClosed(EntityPlayer par1EntityPlayer)
  {
  super.onContainerClosed(par1EntityPlayer);
  if(par1EntityPlayer.worldObj.isRemote)
    {
    return;
    }
  ItemStack builderItem = player.inventory.getCurrentItem();  
  if(builderItem==null || builderItem.getItem()==null || builderItem.getItem()!=AWStructuresItemLoader.scanner)
    {
    return;
    }
  ItemStructureSettings.setSettingsFor(builderItem, settings); 
  }

}
