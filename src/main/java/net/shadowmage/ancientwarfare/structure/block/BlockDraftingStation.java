package net.shadowmage.ancientwarfare.structure.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.structure.tile.TileDraftingStation;

public class BlockDraftingStation extends Block
{

public BlockDraftingStation(String regName)
  {
  super(Material.rock);
  this.setBlockName(regName);
  }

@Override
public boolean hasTileEntity(int metadata)
  {
  return true;
  }

@Override
public TileEntity createTileEntity(World world, int metadata)
  {
  return new TileDraftingStation();
  }

@Override
public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ)
  {
  if(world.isRemote){return false;}
  
  return true;
  }
}
