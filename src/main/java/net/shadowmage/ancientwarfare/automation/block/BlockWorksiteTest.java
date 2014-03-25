package net.shadowmage.ancientwarfare.automation.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.automation.item.AWAutomationItemLoader;
import net.shadowmage.ancientwarfare.automation.tile.WorkSiteQuarry;

public class BlockWorksiteTest extends Block
{

public BlockWorksiteTest(String regName)
  {
  super(Material.rock);
  this.setCreativeTab(AWAutomationItemLoader.automationTab);
  this.setBlockName(regName);
  this.setBlockTextureName("ancientwarfare:civic/civicMineQuarrySides");
  }

@Override
public TileEntity createTileEntity(World world, int metadata)
  {  
  return new WorkSiteQuarry();
  }

@Override
public boolean hasTileEntity(int metadata)
  {
  return true;
  }

}
