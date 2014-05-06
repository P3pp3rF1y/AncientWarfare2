package net.shadowmage.ancientwarfare.automation.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.automation.item.AWAutomationItemLoader;
import net.shadowmage.ancientwarfare.automation.tile.TileWarehouseStorageBase;
import net.shadowmage.ancientwarfare.automation.tile.TileWarehouseStorageSmall;
import net.shadowmage.ancientwarfare.core.interfaces.IInteractableTile;
import net.shadowmage.ancientwarfare.core.util.InventoryTools;

public class BlockWarehouseStorage extends Block
{

public BlockWarehouseStorage(String regName)
  {
  super(Material.rock);
  this.setBlockName(regName);
  this.setCreativeTab(AWAutomationItemLoader.automationTab);
  }

@Override
public boolean hasTileEntity(int metadata)
  {
  return true;
  }

@Override
public TileEntity createTileEntity(World world, int metadata)
  {
  switch(metadata)
  {
  case 0:
  return new TileWarehouseStorageSmall();  
  case 1:
  case 2:
  default:
  return new TileWarehouseStorageSmall();  
  }
  }

@Override
public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int sideHit, float hitX, float hitY, float hitZ)
  {  
  TileEntity te = world.getTileEntity(x, y, z);
  if(te instanceof IInteractableTile)
    {
    ((IInteractableTile) te).onBlockClicked(player);
    }
  return true;  
  }

@Override
public void breakBlock(World world, int x, int y, int z, Block block, int fortune)
  {
  if(!world.isRemote)
    {
    TileWarehouseStorageBase storage = (TileWarehouseStorageBase) world.getTileEntity(x, y, z);
    if(storage!=null)
      {
      InventoryTools.dropInventoryInWorld(world, storage, x, y, z);      
      }    
    }
  super.breakBlock(world, x, y, z, block, fortune);  
  }

@Override
public boolean onBlockEventReceived(World world, int x, int y, int z, int eventID, int eventParam)
  {
  super.onBlockEventReceived(world, x, y, z, eventID, eventParam);
  TileEntity tileentity = world.getTileEntity(x, y, z);
  return tileentity != null ? tileentity.receiveClientEvent(eventID, eventParam) : false;
  }

}
