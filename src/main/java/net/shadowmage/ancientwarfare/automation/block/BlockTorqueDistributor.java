package net.shadowmage.ancientwarfare.automation.block;

import java.util.List;

import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.automation.item.AWAutomationItemLoader;
import net.shadowmage.ancientwarfare.automation.tile.torque.TileTorqueTransportDistributor;
import net.shadowmage.ancientwarfare.automation.tile.torque.TileTorqueTransportDistributorMedium;
import net.shadowmage.ancientwarfare.automation.tile.torque.TileTorqueTransportDistributorHeavy;
import net.shadowmage.ancientwarfare.core.block.BlockRotationHandler.RotationType;

public class BlockTorqueDistributor extends BlockTorqueBase
{

protected BlockTorqueDistributor(String regName)
  {
  super(Material.rock);
  this.setCreativeTab(AWAutomationItemLoader.automationTab);
  this.setBlockName(regName);
  }

@Override
public TileEntity createTileEntity(World world, int metadata)
  {
  switch(metadata)
  {
  case 0:
  return new TileTorqueTransportDistributor();
  case 1:
  return new TileTorqueTransportDistributorMedium();
  case 2:
  return new TileTorqueTransportDistributorHeavy();
  }  
  return new TileTorqueTransportDistributor();
  }

@Override
public void getSubBlocks(Item p_149666_1_, CreativeTabs p_149666_2_, List list)
  {
  list.add(new ItemStack(Item.getItemFromBlock(this),1,0));
  list.add(new ItemStack(Item.getItemFromBlock(this),1,1));
  list.add(new ItemStack(Item.getItemFromBlock(this),1,2));
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
