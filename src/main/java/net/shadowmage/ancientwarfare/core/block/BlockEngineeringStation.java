package net.shadowmage.ancientwarfare.core.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.config.AWLog;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.tile.TileEngineeringStation;

public class BlockEngineeringStation extends Block
{

protected BlockEngineeringStation(String regName)
  {
  super(Material.rock);
  this.setBlockName(regName);
  this.setCreativeTab(AWCoreBlockLoader.coreTab);
  }

@Override
public boolean hasTileEntity(int metadata)
  {
  return true;
  }

@Override
public TileEntity createTileEntity(World world, int metadata)
  {
  AWLog.logDebug("returning new tile entity....");
  return new TileEngineeringStation();
  }

@Override
public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int p_149727_6_, float p_149727_7_, float p_149727_8_, float p_149727_9_)
  {
  if(!world.isRemote)
    {
    NetworkHandler.INSTANCE.openGui(player, NetworkHandler.GUI_CRAFTING, x, y, z);    
    }
  return true;
  }

}
