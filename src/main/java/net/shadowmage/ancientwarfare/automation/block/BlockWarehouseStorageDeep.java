package net.shadowmage.ancientwarfare.automation.block;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.automation.item.AWAutomationItemLoader;
import net.shadowmage.ancientwarfare.automation.tile.IWarehouseStorageTile;
import net.shadowmage.ancientwarfare.automation.tile.TileWarehouseStorageBase;
import net.shadowmage.ancientwarfare.automation.tile.TileWarehouseStorageDeep;
import net.shadowmage.ancientwarfare.automation.tile.TileWarehouseStorageSmall;
import net.shadowmage.ancientwarfare.core.block.BlockIconMap;
import net.shadowmage.ancientwarfare.core.config.AWLog;
import net.shadowmage.ancientwarfare.core.interfaces.IInteractableTile;
import net.shadowmage.ancientwarfare.core.util.InventoryTools;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockWarehouseStorageDeep extends Block
{

BlockIconMap iconMap = new BlockIconMap();

public BlockWarehouseStorageDeep(String regName)
  {
  super(Material.rock);
  this.setBlockName(regName);
  this.setCreativeTab(AWAutomationItemLoader.automationTab);
  }

public BlockWarehouseStorageDeep setIcon(int meta, int side, String texName)
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
  return new TileWarehouseStorageDeep();
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
public void getSubBlocks(Item item, CreativeTabs p_149666_2_, List list)
  {
  list.add(new ItemStack(item,1,0));
  list.add(new ItemStack(item,1,3));
  }

@Override
public void breakBlock(World world, int x, int y, int z, Block block, int fortune)
  {
  if(!world.isRemote)
    {
    IWarehouseStorageTile storage = (IWarehouseStorageTile) world.getTileEntity(x, y, z);
    if(storage!=null)
      {
      storage.dropInventoryInWorld();      
      }    
    }
  super.breakBlock(world, x, y, z, block, fortune);  
  }

@Override
  public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase player, ItemStack itemStack)
    {
    AWLog.logDebug("block placed by.."+itemStack);
    super.onBlockPlacedBy(world, x, y, z, player, itemStack);
    }

@Override
public boolean onBlockEventReceived(World world, int x, int y, int z, int eventID, int eventParam)
  {
  super.onBlockEventReceived(world, x, y, z, eventID, eventParam);
  TileEntity tileentity = world.getTileEntity(x, y, z);
  return tileentity != null ? tileentity.receiveClientEvent(eventID, eventParam) : false;
  }

}
