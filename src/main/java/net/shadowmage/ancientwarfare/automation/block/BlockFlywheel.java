package net.shadowmage.ancientwarfare.automation.block;

import java.util.List;

import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.automation.item.AWAutomationItemLoader;
import net.shadowmage.ancientwarfare.automation.tile.torque.TileTorqueStorageFlywheel;
import net.shadowmage.ancientwarfare.automation.tile.torque.TileTorqueStorageFlywheelLarge;
import net.shadowmage.ancientwarfare.automation.tile.torque.TileTorqueStorageFlywheelMedium;
import net.shadowmage.ancientwarfare.core.block.BlockRotationHandler.RotationType;

public class BlockFlywheel extends BlockTorqueBase
{

public BlockFlywheel(String regName)
  {
  super(Material.rock);
  this.setCreativeTab(AWAutomationItemLoader.automationTab);
  this.setBlockName(regName);
  }

@Override
public int getRenderType()
  {
  return -1;
  }

@Override
public boolean isOpaqueCube()
  {
  return false;
  }

@Override
public boolean isNormalCube()
  {
  return false;
  }

@Override
public TileEntity createTileEntity(World world, int metadata)
  {  
  switch(metadata)
  {
  case 0:
  return new TileTorqueStorageFlywheel();
  case 1:
  return new TileTorqueStorageFlywheelMedium();
  case 2:
  return new TileTorqueStorageFlywheelLarge();
  }  
  return new TileTorqueStorageFlywheel();
  }

@SuppressWarnings({ "unchecked", "rawtypes" })
@Override
public void getSubBlocks(Item p_149666_1_, CreativeTabs p_149666_2_, List list)
  {
  list.add(new ItemStack(Item.getItemFromBlock(this),1,0));
  list.add(new ItemStack(Item.getItemFromBlock(this),1,1));
  list.add(new ItemStack(Item.getItemFromBlock(this),1,2));
  }

@Override
public boolean invertFacing()
  {
  return false;
  }

@Override
public RotationType getRotationType()
  {
  return RotationType.FOUR_WAY;
  }

}
