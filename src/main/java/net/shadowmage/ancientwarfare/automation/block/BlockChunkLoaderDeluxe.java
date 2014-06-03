package net.shadowmage.ancientwarfare.automation.block;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.automation.tile.TileChunkLoaderDeluxe;

public class BlockChunkLoaderDeluxe extends BlockChunkLoaderSimple
{

protected BlockChunkLoaderDeluxe(String regName)
  {
  super(regName);
  }

@Override
public TileEntity createTileEntity(World world, int metadata)
  {
  return new TileChunkLoaderDeluxe();
  }

}
