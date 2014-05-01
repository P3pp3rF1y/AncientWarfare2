package net.shadowmage.ancientwarfare.automation.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.automation.item.AWAutomationItemLoader;
import net.shadowmage.ancientwarfare.automation.tile.TileWarehouseInput;

public class BlockWarehouseInput extends Block
{

public BlockWarehouseInput(String regName)
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
  return new TileWarehouseInput();
  }


}
