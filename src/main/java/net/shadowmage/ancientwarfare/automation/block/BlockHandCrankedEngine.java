package net.shadowmage.ancientwarfare.automation.block;

import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.automation.item.AWAutomationItemLoader;
import net.shadowmage.ancientwarfare.automation.tile.torque.TileHandCrankedEngine;
import net.shadowmage.ancientwarfare.core.block.BlockRotationHandler.RotationType;

public class BlockHandCrankedEngine extends BlockTorqueBase
{

protected BlockHandCrankedEngine(String regName)
  {
  super(Material.rock);
  this.setCreativeTab(AWAutomationItemLoader.automationTab);
  this.setBlockName(regName);
  }

@Override
public TileEntity createTileEntity(World world, int metadata)
  {
  return new TileHandCrankedEngine();
  }

@Override
public RotationType getRotationType()
  {
  return RotationType.SIX_WAY;
  }

@Override
public boolean invertFacing()
  {
  return false;
  }

}
