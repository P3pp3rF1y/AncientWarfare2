package net.shadowmage.ancientwarfare.automation.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.scoreboard.Team;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.shadowmage.ancientwarfare.automation.item.AWAutomationItemLoader;
import net.shadowmage.ancientwarfare.automation.tile.worksite.TileWorksiteBase;
import net.shadowmage.ancientwarfare.core.block.BlockRotationHandler;
import net.shadowmage.ancientwarfare.core.block.BlockRotationHandler.IRotatableBlock;
import net.shadowmage.ancientwarfare.core.block.BlockRotationHandler.RelativeSide;
import net.shadowmage.ancientwarfare.core.block.BlockRotationHandler.RotationType;
import net.shadowmage.ancientwarfare.core.block.IconRotationMap;
import net.shadowmage.ancientwarfare.core.interfaces.IInteractableTile;
import net.shadowmage.ancientwarfare.core.interfaces.IWorkSite;
import net.shadowmage.ancientwarfare.core.util.InventoryTools;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public abstract class BlockWorksiteBase extends Block implements IRotatableBlock
{

IconRotationMap iconMap = new IconRotationMap();
public int maxWorkSize = 16;
public int maxWorkSizeVertical = 1;

public BlockWorksiteBase(Material p_i45394_1_, String regName)
  {
  super(p_i45394_1_);
  this.setCreativeTab(AWAutomationItemLoader.automationTab);
  this.setBlockName(regName);
  setHardness(2.f);
  }

public BlockWorksiteBase setIcon(RelativeSide relativeSide, String texName)
  {
  this.iconMap.setIcon(this, relativeSide, texName);
  return this;
  }

public BlockWorksiteBase setWorkSize(int size)
  {
  this.maxWorkSize = size;
  return this;
  }

public BlockWorksiteBase setWorkVerticalSize(int size)
  {
  this.maxWorkSizeVertical = size;
  return this;
  }

/**
 * made into an abstract method so that derived classes must write an implementation
 * --used to make anonymous classes easier to setup
 * returned tiles must implement IWorksite (for team reference) and IInteractableTile (for interaction callback) if they wish to receive onBlockActivated calls<br>
 * returned tiles must implement IBoundedTile if they want workbounds set from ItemBlockWorksite<br>
 * returned tiles must implement IOwnable if they want owner-name set from ItemBlockWorksite<br>
 */
@Override
public abstract TileEntity createTileEntity(World world, int metadata);

@Override
public boolean hasTileEntity(int metadata)
  {
  return true;
  }

@Override
@SideOnly(Side.CLIENT)
public void registerBlockIcons(IIconRegister p_149651_1_)
  {
  iconMap.registerIcons(p_149651_1_);
  }

@Override
@SideOnly(Side.CLIENT)
public IIcon getIcon(int side, int meta)
  {
  return iconMap.getIcon(this, meta, side);
  }

@Override
public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ)
  {
  TileEntity te = world.getTileEntity(x, y, z);
  if(te instanceof IWorkSite && te instanceof IInteractableTile)
    {
    Team t = player.getTeam();
    Team t1 = ((IWorkSite)te).getTeam();
    if(t==t1)
      {
      return ((IInteractableTile)te).onBlockClicked(player);
      }
    }
  return true;
  }

@Override
public RotationType getRotationType()
  {
  return RotationType.FOUR_WAY;
  }

@Override
public boolean invertFacing()
  {
  return true;
  }

@Override
public boolean rotateBlock(World worldObj, int x, int y, int z, ForgeDirection axis)
  {  
  int meta = worldObj.getBlockMetadata(x, y, z);
  int newMeta = BlockRotationHandler.getRotatedMeta(this, meta, axis);
  if(meta!=newMeta)
    {
    worldObj.setBlockMetadataWithNotify(x, y, z, newMeta, 3);
    worldObj.markBlockForUpdate(x, y, z);    
    return true;
    }
  return false;
  }

@Override
public void breakBlock(World world, int x, int y, int z, Block block, int meta)
  {
  TileEntity te = world.getTileEntity(x, y, z);
  if(te instanceof IInventory)
    {
    InventoryTools.dropInventoryInWorld(world, (IInventory) te, x, y, z);
    }
  if(te instanceof TileWorksiteBase)
    {
    ((TileWorksiteBase) te).onBlockBroken();
    }
  super.breakBlock(world, x, y, z, block, meta);
  }

}
