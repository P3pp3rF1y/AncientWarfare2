package net.shadowmage.ancientwarfare.automation.block;

import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.automation.item.AWAutomationItemLoader;
import net.shadowmage.ancientwarfare.core.block.BlockRotationHandler.RotationType;

public abstract class BlockTorqueGenerator extends BlockTorqueBase
{

protected BlockTorqueGenerator(String regName)
  {
  super(Material.rock);
  this.setCreativeTab(AWAutomationItemLoader.automationTab);
  this.setBlockName(regName);
  }

@Override
public abstract TileEntity createTileEntity(World world, int metadata);

@Override
public RotationType getRotationType()
  {
  return RotationType.FOUR_WAY;
  }

@Override
public boolean invertFacing()
  {
  return false;
  }

}
