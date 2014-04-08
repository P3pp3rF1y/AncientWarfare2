package net.shadowmage.ancientwarfare.structure.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.automation.item.AWAutomationItemLoader;
import net.shadowmage.ancientwarfare.structure.item.AWStructuresItemLoader;
import net.shadowmage.ancientwarfare.structure.tile.TileAdvancedSpawner;

public class BlockAdvancedSpawner extends Block
{

public BlockAdvancedSpawner(String regName)
  {
  super(Material.rock);
  this.setCreativeTab(AWStructuresItemLoader.structureTab);
  this.setBlockName(regName);
  this.setBlockTextureName("ancientwarfare:civic/civicMineQuarrySides");
  }

@Override
public boolean hasTileEntity(int metadata)
  {
  return true;
  }

@Override
public TileEntity createTileEntity(World world, int metadata)
  {
  return new TileAdvancedSpawner();
  }
}
