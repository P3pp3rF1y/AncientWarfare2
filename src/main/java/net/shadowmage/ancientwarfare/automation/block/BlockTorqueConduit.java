package net.shadowmage.ancientwarfare.automation.block;

import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.automation.item.AWAutomationItemLoader;
import net.shadowmage.ancientwarfare.automation.render.RenderTorqueConduit;
import net.shadowmage.ancientwarfare.automation.tile.torque.TileTorqueTransportConduit;
import net.shadowmage.ancientwarfare.core.block.BlockRotationHandler.RotationType;

public class BlockTorqueConduit extends BlockTorqueBase
{

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
public TileEntity createTileEntity(World world, int metadata)
  {  
  return new TileTorqueTransportConduit();
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
  TileTorqueTransportConduit tile = (TileTorqueTransportConduit) world.getTileEntity(x, y, z);
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


}
