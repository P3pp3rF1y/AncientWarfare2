package net.shadowmage.ancientwarfare.automation.block;

import java.util.List;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.automation.item.AWAutomationItemLoader;
import net.shadowmage.ancientwarfare.automation.tile.torque.TileTorqueStorageFlywheelController;
import net.shadowmage.ancientwarfare.automation.tile.torque.TileTorqueStorageFlywheelControllerLarge;
import net.shadowmage.ancientwarfare.automation.tile.torque.TileTorqueStorageFlywheelControllerMedium;
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
public boolean shouldSideBeRendered(net.minecraft.world.IBlockAccess p_149646_1_, int p_149646_2_, int p_149646_3_, int p_149646_4_, int p_149646_5_) {return false;}

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
  return new TileTorqueStorageFlywheelController();
  case 1:
  return new TileTorqueStorageFlywheelControllerMedium();
  case 2:
  return new TileTorqueStorageFlywheelControllerLarge();
  }  
  return new TileTorqueStorageFlywheelController();
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
public void registerBlockIcons(IIconRegister register)
  {
  }

@Override
public IIcon getIcon(int side, int meta)
  {
  return Blocks.iron_block.getIcon(0, 0);
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
