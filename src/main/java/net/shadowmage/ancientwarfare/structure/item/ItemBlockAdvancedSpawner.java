package net.shadowmage.ancientwarfare.structure.item;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.config.AWLog;
import net.shadowmage.ancientwarfare.core.interfaces.IItemKeyInterface;
import net.shadowmage.ancientwarfare.structure.tile.SpawnerSettings;
import net.shadowmage.ancientwarfare.structure.tile.SpawnerSettings.EntitySpawnGroup;
import net.shadowmage.ancientwarfare.structure.tile.SpawnerSettings.EntitySpawnSettings;
import net.shadowmage.ancientwarfare.structure.tile.TileAdvancedSpawner;

public class ItemBlockAdvancedSpawner extends ItemBlock implements IItemKeyInterface
{

public ItemBlockAdvancedSpawner(Block p_i45328_1_)
  {
  super(p_i45328_1_);
  }

@Override
public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ, int metadata)
  {
  if(player.isSneaking())
    {
    if(world.isRemote){return false;}
    //TODO open GUI
    return false;
    }
//  if(!stack.hasTagCompound())
//    {
//    AWLog.logDebug("no tag exists for spawner item...");
//    return false;
//    }
  boolean val = super.placeBlockAt(stack, player, world, x, y, z, side, hitX, hitY, hitZ, metadata);
  if(!world.isRemote && val)
    {
    TileEntity te = world.getTileEntity(x, y, z);
    if(te instanceof TileAdvancedSpawner)
      {
      TileAdvancedSpawner tile = (TileAdvancedSpawner)te;

      SpawnerSettings settings = new SpawnerSettings();
      EntitySpawnGroup grp = new EntitySpawnGroup();
      grp.setWeight(1);
      EntitySpawnSettings set = new EntitySpawnSettings();
      set.setEntityToSpawn("Pig");
      set.setSpawnCountMin(1);
      set.setSpawnCountMax(4);
      set.setSpawnLimitTotal(-1);      
      grp.addSpawnSetting(set);
      settings.addSpawnGroup(grp);      
//      settings.readFromNBT(stack.getTagCompound());//TODO un-comment for set from item-data      
      tile.setSettings(settings);
      }    
    world.markBlockForUpdate(x, y, z);
    }
  return val;  
  }

@Override
public void onKeyAction(EntityPlayer player, ItemStack stack)
  {
  
  }

}
