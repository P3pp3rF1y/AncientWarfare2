package net.shadowmage.ancientwarfare.automation.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.shadowmage.ancientwarfare.automation.item.AWAutomationItemLoader;
import net.shadowmage.ancientwarfare.automation.render.RenderTorqueConduit;
import net.shadowmage.ancientwarfare.automation.tile.TileTorqueTransportBase;
import net.shadowmage.ancientwarfare.automation.tile.TileTorqueConduit;
import net.shadowmage.ancientwarfare.automation.tile.TileTorqueDistributor;
import net.shadowmage.ancientwarfare.core.block.BlockRotationHandler;
import net.shadowmage.ancientwarfare.core.block.BlockRotationHandler.IRotatableBlock;
import net.shadowmage.ancientwarfare.core.block.BlockRotationHandler.RelativeSide;
import net.shadowmage.ancientwarfare.core.block.BlockRotationHandler.RotationType;
import net.shadowmage.ancientwarfare.core.block.IconRotationMap;

public class BlockTorqueConduit extends Block implements IRotatableBlock
{

IconRotationMap iconMap = new IconRotationMap();

@Override
public int getRenderType()
  {
  return RenderTorqueConduit.renderID;
  }

protected BlockTorqueConduit(String regName)
  {
  super(Material.rock);
  this.setCreativeTab(AWAutomationItemLoader.automationTab);
  this.setBlockName(regName);
  this.setLightOpacity(1);  
  }

@Override
public void onPostBlockPlaced(World world, int x, int y, int z, int meta)
  {
  TileEntity te = world.getTileEntity(x, y, z);
  if(te instanceof TileTorqueTransportBase)
    {
    ((TileTorqueTransportBase) te).onBlockUpdated();
    }
  super.onPostBlockPlaced(world, x, y, z,  meta);
  }

@Override
public void onNeighborChange(IBlockAccess world, int x, int y, int z, int tileX, int tileY, int tileZ)
  {
  TileEntity te = world.getTileEntity(x, y, z);
  if(te instanceof TileTorqueTransportBase)
    {
    ((TileTorqueTransportBase)te).onBlockUpdated();
    }
  super.onNeighborChange(world, x, y, z, tileX, tileY, tileZ);
  }

@Override
public void onNeighborBlockChange(World world, int x, int y, int z, Block block)
  {
  TileEntity te = world.getTileEntity(x, y, z);
  if(te instanceof TileTorqueTransportBase)
    {
    ((TileTorqueTransportBase)te).onBlockUpdated();
    }
  super.onNeighborBlockChange(world, x, y, z, block);
  }

@Override
public void registerBlockIcons(IIconRegister register)
  {
  iconMap.registerIcons(register);
  }

@Override
public IIcon getIcon(int side, int meta)
  {
  return iconMap.getIcon(this, meta, side);
  }

public BlockTorqueConduit setIcon(RelativeSide side, String texName)
  {
  iconMap.setIcon(this, side, texName);
  return this;
  }

@Override
public TileEntity createTileEntity(World world, int metadata)
  {  
  return new TileTorqueConduit();
  }

@Override
public boolean hasTileEntity(int metadata)
  {
  return true;
  }

@Override
public boolean isOpaqueCube()
  {
  return false;
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

@Override
public boolean rotateBlock(World worldObj, int x, int y, int z, ForgeDirection axis)
  {
  int meta = worldObj.getBlockMetadata(x, y, z);
  int rMeta = BlockRotationHandler.getRotatedMeta(this, meta, axis);
  if(rMeta!=meta)
    {
    worldObj.setBlockMetadataWithNotify(x, y, z, rMeta, 3);
    return true;
    }
  return false;
  }

@Override
public void setBlockBoundsForItemRender()
  {
  float min = 0.1875f, max = 0.8125f;
  setBlockBounds(min, 0, min, max, 1, max);
  }

@Override
public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z)
  {
  setBlockBoundsBasedOnState(world, x, y, z);
  return super.getCollisionBoundingBoxFromPool(world, x, y, z);
  }

@Override
public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z)
  {
  float min = 0.1875f, max = 0.8125f;
  float x1=min, y1=min, z1=min, x2=max, y2=max, z2=max;
  TileTorqueConduit tile = (TileTorqueConduit) world.getTileEntity(x, y, z);
  if(tile!=null)
    {
    boolean[] sides = tile.getConnections();
    if(sides[0]){y1=0.f;}
    if(sides[1]){y2=1.f;}
    if(sides[2]){z1=0.f;}
    if(sides[3]){z2=1.f;}
    if(sides[4]){x1=0.f;}
    if(sides[5]){x2=1.f;}
    }
  setBlockBounds(x1, y1, z1, x2, y2, z2);
  }

@Override
public boolean onBlockEventReceived(World world, int x, int y, int z, int a, int b)
  {
  super.onBlockEventReceived(world, x, y, z, a, b);
  TileEntity tileentity = world.getTileEntity(x, y, z);
  return tileentity != null ? tileentity.receiveClientEvent(a, b) : false;
  }

}
