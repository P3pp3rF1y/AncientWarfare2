package net.shadowmage.ancientwarfare.automation.block;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.automation.item.AWAutomationItemLoader;
import net.shadowmage.ancientwarfare.automation.tile.warehouse2.TileWarehouseStorage;
import net.shadowmage.ancientwarfare.automation.tile.warehouse2.TileWarehouseStorageLarge;
import net.shadowmage.ancientwarfare.automation.tile.warehouse2.TileWarehouseStorageMedium;
import net.shadowmage.ancientwarfare.core.block.BlockIconMap;
import net.shadowmage.ancientwarfare.core.interfaces.IInteractableTile;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockWarehouseStorage extends Block
{

private BlockIconMap iconMap = new BlockIconMap();

public BlockWarehouseStorage(String regName)
  {
  super(Material.rock);
  this.setBlockName(regName);
  this.setCreativeTab(AWAutomationItemLoader.automationTab);
  }

public BlockWarehouseStorage setIcon(int meta, int side, String texName)
  {
  this.iconMap.setIconTexture(side, meta, texName);
  return this;
  }

@Override
@SideOnly(Side.CLIENT)
public void registerBlockIcons(IIconRegister reg)
  {
  iconMap.registerIcons(reg);
  }

@Override
@SideOnly(Side.CLIENT)
public IIcon getIcon(int side, int meta)
  {
  return iconMap.getIconFor(side, meta);
  }

@Override
public boolean hasTileEntity(int metadata)
  {
  return true;
  }

@Override
public TileEntity createTileEntity(World world, int metadata)
  {
  switch(metadata)
  {
  case 0:
  return new TileWarehouseStorage();
  case 1:
  return new TileWarehouseStorageMedium();
  case 2:
  return new TileWarehouseStorageLarge();
  default:
  return new TileWarehouseStorage();
  }  
  }

@SuppressWarnings({ "unchecked", "rawtypes" })
@Override
public void getSubBlocks(Item item, CreativeTabs p_149666_2_, List list)
  {
  list.add(new ItemStack(item,1,0));
  list.add(new ItemStack(item,1,1));
  list.add(new ItemStack(item,1,2));
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
public void breakBlock(World world, int x, int y, int z, Block block, int fortune)
  {
  if(!world.isRemote)
    {
    // TODO 
//    TileWarehouseStorageBase storage = (TileWarehouseStorageBase) world.getTileEntity(x, y, z);
//    if(storage!=null)
//      {
//      InventoryTools.dropInventoryInWorld(world, storage, x, y, z);      
//      }  
    }
  super.breakBlock(world, x, y, z, block, fortune);  
  }

@Override
public boolean onBlockEventReceived(World world, int x, int y, int z, int eventID, int eventParam)
  {
  super.onBlockEventReceived(world, x, y, z, eventID, eventParam);
  TileEntity tileentity = world.getTileEntity(x, y, z);
  return tileentity != null ? tileentity.receiveClientEvent(eventID, eventParam) : false;
  }

}
