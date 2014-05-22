package net.shadowmage.ancientwarfare.automation.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.shadowmage.ancientwarfare.automation.tile.torque.TileTorqueBase;
import net.shadowmage.ancientwarfare.core.block.BlockRotationHandler;
import net.shadowmage.ancientwarfare.core.block.BlockRotationHandler.IRotatableBlock;
import net.shadowmage.ancientwarfare.core.block.BlockRotationHandler.RelativeSide;
import net.shadowmage.ancientwarfare.core.block.IconRotationMap;
import net.shadowmage.ancientwarfare.core.interfaces.IInteractableTile;

public abstract class BlockTorqueBase extends Block implements IRotatableBlock
{

IconRotationMap iconMap = new IconRotationMap();

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

protected BlockTorqueBase(Material material)
  {
  super(material);
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

public BlockTorqueBase setIcon(RelativeSide side, String texName)
  {
  iconMap.setIcon(this, side, texName);
  return this;
  }

@Override
public void onPostBlockPlaced(World world, int x, int y, int z, int meta)
  {
  TileEntity te = world.getTileEntity(x, y, z);
  if(te instanceof TileTorqueBase)
    {
    ((TileTorqueBase) te).onBlockUpdated();
    }
  super.onPostBlockPlaced(world, x, y, z,  meta);
  }

@Override
public void onNeighborChange(IBlockAccess world, int x, int y, int z, int tileX, int tileY, int tileZ)
  {
  TileEntity te = world.getTileEntity(x, y, z);
  if(te instanceof TileTorqueBase)
    {
    ((TileTorqueBase)te).onBlockUpdated();
    }
  super.onNeighborChange(world, x, y, z, tileX, tileY, tileZ);
  }

@Override
public void onNeighborBlockChange(World world, int x, int y, int z, Block block)
  {
  TileEntity te = world.getTileEntity(x, y, z);
  if(te instanceof TileTorqueBase)
    {
    ((TileTorqueBase)te).onBlockUpdated();
    }
  super.onNeighborBlockChange(world, x, y, z, block);
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
public boolean onBlockEventReceived(World world, int x, int y, int z, int a, int b)
  {
  super.onBlockEventReceived(world, x, y, z, a, b);
  TileEntity tileentity = world.getTileEntity(x, y, z);
  return tileentity != null ? tileentity.receiveClientEvent(a, b) : false;
  }

@Override
public boolean hasTileEntity(int metadata)
  {
  return true;
  }

}
