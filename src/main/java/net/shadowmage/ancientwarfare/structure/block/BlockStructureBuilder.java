package net.shadowmage.ancientwarfare.structure.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.structure.item.AWStructuresItemLoader;
import net.shadowmage.ancientwarfare.structure.tile.TileStructureBuilder;

public class BlockStructureBuilder extends Block
{

public BlockStructureBuilder(String regName)
  {
  super(Material.rock);
  this.setBlockName(regName);
  this.setCreativeTab(AWStructuresItemLoader.structureTab);
  this.setBlockTextureName("ancientwarfare:spawner/advanced_spawner");
  }

@Override
public boolean hasTileEntity(int metadata)
  {
  return true;
  }

@Override
public TileEntity createTileEntity(World world, int metadata)
  {
  return new TileStructureBuilder();
  }

@Override
public void breakBlock(World world, int x, int y, int z, Block block, int meta)
  {
  if(!world.isRemote)
    {
    TileEntity te = world.getTileEntity(x, y, z);
    if(te instanceof TileStructureBuilder)
      {
      ((TileStructureBuilder) te).onBlockBroken();
      }
    }
  }

}
