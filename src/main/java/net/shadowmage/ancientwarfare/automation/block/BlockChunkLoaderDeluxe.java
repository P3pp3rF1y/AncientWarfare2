package net.shadowmage.ancientwarfare.automation.block;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.automation.item.AWAutomationItemLoader;

public class BlockChunkLoaderDeluxe extends BlockChunkLoaderSimple
{

protected BlockChunkLoaderDeluxe(String regName)
  {
  super(regName);
  this.setCreativeTab(AWAutomationItemLoader.automationTab);
  }

@Override
public TileEntity createTileEntity(World world, int metadata)
  {
  return null;//TODO
//  return new TileChunkLoaderSimple();
  }

}
