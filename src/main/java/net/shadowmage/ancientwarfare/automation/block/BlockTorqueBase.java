package net.shadowmage.ancientwarfare.automation.block;

import java.util.HashMap;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.shadowmage.ancientwarfare.automation.tile.torque.TileTorqueBase;
import net.shadowmage.ancientwarfare.core.block.BlockRotationHandler;
import net.shadowmage.ancientwarfare.core.block.BlockRotationHandler.IRotatableBlock;
import net.shadowmage.ancientwarfare.core.block.BlockRotationHandler.IRotatableTile;
import net.shadowmage.ancientwarfare.core.block.BlockRotationHandler.RelativeSide;
import net.shadowmage.ancientwarfare.core.block.IconRotationMap;
import net.shadowmage.ancientwarfare.core.interfaces.IInteractableTile;
import net.shadowmage.ancientwarfare.core.util.InventoryTools;
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
    return ((IInteractableTile) te).onBlockClicked(player);
    }
  return false;  
  }

protected BlockTorqueBase(Material material)
  {
  super(material);
  setHardness(2.f);
  }

@Override
public boolean isNormalCube(IBlockAccess world, int x, int y, int z){return false;}

@Override
public boolean isSideSolid(IBlockAccess world, int x, int y, int z, ForgeDirection side){return false;}

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
  TileEntity t = block.getTileEntity(x, y, z);
  IRotatableTile tt = (IRotatableTile)t;
  int meta = 2;
  if(tt!=null)
    {
    ForgeDirection d = tt.getPrimaryFacing();
    if(d!=null)
      {
      meta = d.ordinal();
      }
    }
  return iconMaps.get(meta).getIcon(this, meta, side);
  }

@Override
public IIcon getIcon(int side, int meta)
  {
//  AWLog.logDebug("fetching texture for block: "+getUnlocalizedName());
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
public void onNeighborChange(IBlockAccess world, int x, int y, int z, int tileX, int tileY, int tileZ)
  {
  TileEntity te = world.getTileEntity(x, y, z);
  if(te instanceof TileTorqueBase)
    {
    ((TileTorqueBase)te).onNeighborTileChanged();
    }
  super.onNeighborChange(world, x, y, z, tileX, tileY, tileZ);
  }

@Override
public void onNeighborBlockChange(World world, int x, int y, int z, Block block)
  {
  TileEntity te = world.getTileEntity(x, y, z);
  if(te instanceof TileTorqueBase)
    {
    ((TileTorqueBase)te).onNeighborTileChanged();
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
  int meta = tt.getPrimaryFacing().ordinal();  
  int rMeta = BlockRotationHandler.getRotatedMeta(this, meta, axis);
  if(rMeta!=meta)
    {
    tt.setPrimaryFacing(ForgeDirection.getOrientation(rMeta));
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

@Override
public void breakBlock(World world, int x, int y, int z, Block block, int meta)
  {
  TileEntity te = world.getTileEntity(x, y, z);
  if(te instanceof IInventory)
    {
    InventoryTools.dropInventoryInWorld(world, (IInventory) te, x, y, z);
    }
  super.breakBlock(world, x, y, z, block, meta);
  }

@Override
public int damageDropped(int meta)
  {
  return meta;
  }

}
