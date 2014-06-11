package net.shadowmage.ancientwarfare.automation.block;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.shadowmage.ancientwarfare.automation.item.AWAutomationItemLoader;
import net.shadowmage.ancientwarfare.automation.tile.warehouse2.TileWarehouseStockViewer;
import net.shadowmage.ancientwarfare.core.block.BlockRotationHandler.IRotatableBlock;
import net.shadowmage.ancientwarfare.core.block.BlockRotationHandler.RelativeSide;
import net.shadowmage.ancientwarfare.core.block.BlockRotationHandler.RotationType;
import net.shadowmage.ancientwarfare.core.interfaces.IInteractableTile;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockWarehouseStockViewer extends Block implements IRotatableBlock
{

public BlockWarehouseStockViewer(String regName)
  {
  super(Material.rock);
  this.setBlockName(regName);
  this.setCreativeTab(AWAutomationItemLoader.automationTab);
  this.setBlockTextureName("minecraft:oak_planks");
  }

@Override
public RotationType getRotationType()
  {
  return RotationType.FOUR_WAY;
  }

@Override
@SideOnly(Side.CLIENT)
public boolean shouldSideBeRendered(IBlockAccess p_149646_1_, int p_149646_2_, int p_149646_3_, int p_149646_4_, int p_149646_5_)
  {
  return true;
  }

@Override
public boolean isBlockSolid(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5)
  {
  return false;
  }

@Override
public boolean isOpaqueCube()
  {
  return false;
  }

@Override
public boolean renderAsNormalBlock()
  {
  return false;
  }

@Override
public boolean invertFacing()
  {
  return true;
  }

@Override
public void addCollisionBoxesToList(World p_149743_1_, int p_149743_2_, int p_149743_3_, int p_149743_4_, AxisAlignedBB p_149743_5_, List p_149743_6_, Entity p_149743_7_)
  {
  //noop for no collisions
  }

@Override
public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z)
  {
  int meta = world.getBlockMetadata(x, y, z);
  ForgeDirection d = ForgeDirection.getOrientation(meta).getOpposite();
  float wmin = 0.125f;
  float wmax = 0.875f;
  float hmin = 0.375f;
  float hmax = 0.875f;
  switch(d)
  {
  case EAST:
    {
    setBlockBounds(wmax, hmin, 0, 1.f, hmax, 1);
    }
    break;
  case WEST:
    {
    setBlockBounds(0, hmin, 0, wmin, hmax, 1);
    }
    break;
  case NORTH:
    {
    setBlockBounds(0, hmin, 0, 1, hmax, wmin);
    }
    break;
  case SOUTH:
    {
    setBlockBounds(0, hmin, wmax, 1, hmax, 1);
    }
    break;
  default:
    {
    setBlockBounds(0, 0, 0, 1, 1, 1);
    }
    break;
  }
  }

@Override
public void setBlockBoundsForItemRender()
  {
  float wmin = 0.125f;
  float wmax = 0.875f;
  float hmin = 0.375f;
  float hmax = 0.875f;
  setBlockBounds(wmax, hmin, 0, 1.f, hmax, 1);
  }

@Override
public BlockWarehouseStockViewer setIcon(RelativeSide side, String texName)
  {  
  return this;
  }

@Override
@SideOnly(Side.CLIENT)
public IIcon getIcon(int side, int meta)
  {
  return Blocks.planks.getIcon(0, 0);
  }

@Override
public boolean hasTileEntity(int metadata)
  {
  return true;
  }

@Override
public TileEntity createTileEntity(World world, int metadata)
  {
  return new TileWarehouseStockViewer();
  }

@Override
public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int sideHit, float hitX, float hitY, float hitZ)
  {  
  TileEntity te = world.getTileEntity(x, y, z);
  if(te instanceof IInteractableTile)
    {
    ((IInteractableTile) te).onBlockClicked(player);
    }
  return true;  
  }

@Override
public boolean onBlockEventReceived(World world, int x, int y, int z, int a, int b)
  {
  super.onBlockEventReceived(world, x, y, z, a, b);
  TileEntity tileentity = world.getTileEntity(x, y, z);
  return tileentity != null ? tileentity.receiveClientEvent(a, b) : false;
  }

}
