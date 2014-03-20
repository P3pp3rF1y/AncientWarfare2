package net.shadowmage.ancientwarfare.automation.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.automation.item.AWAutomationItemLoader;
import net.shadowmage.ancientwarfare.automation.tile.TileWorkerTest;
import net.shadowmage.ancientwarfare.core.config.AWLog;

public class BlockWorkerTest extends Block
{

public BlockWorkerTest(String regName)
  {
  super(Material.rock);
  this.setCreativeTab(AWAutomationItemLoader.automationTab);
  this.setBlockName(regName);
  }

@Override
public TileEntity createTileEntity(World world, int metadata)
  {  
  AWLog.logDebug("returning new worker test tile entity");
  return new TileWorkerTest();
  }

@Override
public boolean hasTileEntity(int metadata)
  {
  return true;
  }

}
