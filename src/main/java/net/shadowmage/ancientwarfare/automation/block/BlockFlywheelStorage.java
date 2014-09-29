package net.shadowmage.ancientwarfare.automation.block;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.automation.item.AWAutomationItemLoader;
import net.shadowmage.ancientwarfare.automation.tile.torque.TileFlywheelStorage;

public class BlockFlywheelStorage extends Block
{

public BlockFlywheelStorage(String regName)
  {
  super(Material.rock);
  this.setBlockName(regName);
  this.setCreativeTab(AWAutomationItemLoader.automationTab);
  }

@Override
public boolean shouldSideBeRendered(net.minecraft.world.IBlockAccess p_149646_1_, int p_149646_2_, int p_149646_3_, int p_149646_4_, int p_149646_5_) {return false;}

@Override
public boolean isOpaqueCube(){return false;}

@Override
public boolean isNormalCube(){return false;}

@Override
public IIcon getIcon(int p_149691_1_, int p_149691_2_){return Blocks.iron_block.getIcon(p_149691_1_, p_149691_2_);}

@Override
public void onPostBlockPlaced(World world, int x, int y, int z, int meta)
  {
  super.onPostBlockPlaced(world, x, y, z, meta);
  TileFlywheelStorage te = (TileFlywheelStorage) world.getTileEntity(x, y, z);
  te.blockPlaced();
  }

@Override
public void breakBlock(World world, int x, int y, int z, Block p_149749_5_, int p_149749_6_)
  {
  TileFlywheelStorage te = (TileFlywheelStorage) world.getTileEntity(x, y, z);
  te.blockBroken();
  super.breakBlock(world, x, y, z, p_149749_5_, p_149749_6_);
  }

@Override
public TileEntity createTileEntity(World world, int metadata)
  {  
  switch(metadata)
  {
  case 0:
  return new TileFlywheelStorage();
  case 1:
  return new TileFlywheelStorage();
  case 2:
  return new TileFlywheelStorage();
  }  
  return new TileFlywheelStorage();
  }

@Override
public boolean hasTileEntity(int metadata)
  {
  return true;
  }

@SuppressWarnings({ "unchecked", "rawtypes" })
@Override
public void getSubBlocks(Item p_149666_1_, CreativeTabs p_149666_2_, List list)
  {
  list.add(new ItemStack(Item.getItemFromBlock(this),1,0));
  list.add(new ItemStack(Item.getItemFromBlock(this),1,1));
  list.add(new ItemStack(Item.getItemFromBlock(this),1,2));
  }

}
