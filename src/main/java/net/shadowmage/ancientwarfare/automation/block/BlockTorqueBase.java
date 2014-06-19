package net.shadowmage.ancientwarfare.automation.block;

import java.util.HashMap;

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
import net.shadowmage.ancientwarfare.core.interfaces.ITorque.ITorqueTile;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public abstract class BlockTorqueBase extends Block implements IRotatableBlock
{

HashMap<Integer, IconRotationMap> iconMaps = new HashMap<Integer, IconRotationMap>();

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
  for(IconRotationMap map : this.iconMaps.values())
    {
    map.registerIcons(register);
    }
  }

@Override
@SideOnly(Side.CLIENT)
public IIcon getIcon(IBlockAccess block, int x, int y, int z, int side)
  {
  int meta = block.getBlockMetadata(x, y, z);
  TileEntity t = block.getTileEntity(x, y, z);
  ITorqueTile tt = (ITorqueTile)t;
  return iconMaps.get(meta).getIcon(this, tt.getOrientation().ordinal(), side);
  }

@Override
public IIcon getIcon(int side, int meta)
  {
  return iconMaps.get(meta).getIcon(this, 2, side);
  }

@Override
public BlockTorqueBase setIcon(RelativeSide side, String texName)
  {
  throw new UnsupportedOperationException("Cannot set side icons directly on torque block, need to use meta-sensitive version");
  }

public BlockTorqueBase setIcon(int meta, RelativeSide side, String texName)
  {
  if(!this.iconMaps.containsKey(meta)){this.iconMaps.put(meta, new IconRotationMap());}
  iconMaps.get(meta).setIcon(this, side, texName);
  return this;
  }

public IIcon getIcon(int meta, RelativeSide side)
  {
  return iconMaps.get(meta).getIcon(side);
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
  if(worldObj.isRemote)
    {
    return false;
    }
  TileEntity t = worldObj.getTileEntity(x, y, z);
  TileTorqueBase tt = (TileTorqueBase)t;
  int meta = tt.getOrientation().ordinal();  
  int rMeta = BlockRotationHandler.getRotatedMeta(this, meta, axis);
  if(rMeta!=meta)
    {
    tt.setOrientation(ForgeDirection.getOrientation(rMeta));
    worldObj.markBlockForUpdate(x, y, z);
    return true;
    }
  return false;
  }

@Override
public boolean onBlockEventReceived(World world, int x, int y, int z, int a, int b)
  {
  TileEntity tileentity = world.getTileEntity(x, y, z);
  return tileentity != null ? tileentity.receiveClientEvent(a, b) : false;
  }

@Override
public boolean hasTileEntity(int metadata)
  {
  return true;
  }

}
