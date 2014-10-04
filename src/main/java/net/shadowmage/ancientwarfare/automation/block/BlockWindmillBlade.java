package net.shadowmage.ancientwarfare.automation.block;

import java.util.List;

import net.minecraft.block.Block;
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
import net.shadowmage.ancientwarfare.automation.tile.torque.TileWindmillBlade;

public class BlockWindmillBlade extends Block
{

public BlockWindmillBlade(String regName)
  {
  super(Material.rock);
  this.setBlockName(regName);
  this.setCreativeTab(AWAutomationItemLoader.automationTab);
  }

@Override
public boolean onBlockEventReceived(World world, int x, int y, int z, int a, int b)
  {
  TileEntity tileentity = world.getTileEntity(x, y, z);
  return tileentity != null ? tileentity.receiveClientEvent(a, b) : false;
  }

//TODO alter slightly to render a default icon when not a valid setup -- will need to query the TE
@Override
public boolean shouldSideBeRendered(net.minecraft.world.IBlockAccess access, int x, int y, int z, int side) {return false;}

@Override
public boolean isOpaqueCube(){return false;}

@Override
public boolean isNormalCube(){return false;}

@Override
public IIcon getIcon(int side, int meta)
  {
  return Blocks.glass.getIcon(side, 0);
  }

@Override
public void registerBlockIcons(IIconRegister register)
  {
  }

@Override
public void onPostBlockPlaced(World world, int x, int y, int z, int meta)
  {
  super.onPostBlockPlaced(world, x, y, z, meta);
  TileWindmillBlade te = (TileWindmillBlade) world.getTileEntity(x, y, z);
  te.blockPlaced();
  }

@Override
public void breakBlock(World world, int x, int y, int z, Block p_149749_5_, int p_149749_6_)
  {
  TileWindmillBlade te = (TileWindmillBlade) world.getTileEntity(x, y, z);
  super.breakBlock(world, x, y, z, p_149749_5_, p_149749_6_);
  te.blockBroken();//have to call post block-break so that the tile properly sees the block/tile as gone
  }

@Override
public TileEntity createTileEntity(World world, int metadata)
  {  
  return new TileWindmillBlade();
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
  }

}
