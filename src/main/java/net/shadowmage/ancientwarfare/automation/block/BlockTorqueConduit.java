package net.shadowmage.ancientwarfare.automation.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.automation.item.AWAutomationItemLoader;
import net.shadowmage.ancientwarfare.automation.tile.TileTorqueConduit;
import net.shadowmage.ancientwarfare.core.block.BlockRotationHandler.IRotatableBlock;
import net.shadowmage.ancientwarfare.core.block.BlockRotationHandler.RelativeSide;
import net.shadowmage.ancientwarfare.core.block.BlockRotationHandler.RotationType;
import net.shadowmage.ancientwarfare.core.block.IconRotationMap;

public class BlockTorqueConduit extends Block implements IRotatableBlock
{

IconRotationMap iconMap = new IconRotationMap();

protected BlockTorqueConduit(String regName)
  {
  super(Material.rock);
  this.setCreativeTab(AWAutomationItemLoader.automationTab);
  this.setBlockName(regName);
  this.setLightOpacity(0);  
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

public void setIcon(RelativeSide side, String texName)
  {
  iconMap.setIcon(this, side, texName);
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
public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z)
  {
  float min = 0.1875f, max = 0.8125f;
  int meta = world.getBlockMetadata(x, y, z);
  if(meta==0||meta==1)
    {
    setBlockBounds(min, 0, min, max, 1, max);
    }
  else if(meta==2||meta==3)
    {
    setBlockBounds(min, min, 0, max, max, 1);
    }
  else if(meta==4||meta==5)
    {
    setBlockBounds(0, min, min, 1, max, max);
    }
  }

}
