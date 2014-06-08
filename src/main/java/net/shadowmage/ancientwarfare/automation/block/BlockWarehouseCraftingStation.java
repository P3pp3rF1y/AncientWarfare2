package net.shadowmage.ancientwarfare.automation.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.automation.item.AWAutomationItemLoader;
import net.shadowmage.ancientwarfare.automation.tile.warehouse.TileWarehouseCraftingStation;
import net.shadowmage.ancientwarfare.core.block.BlockIconMap;
import net.shadowmage.ancientwarfare.core.interfaces.IInteractableTile;
import net.shadowmage.ancientwarfare.core.util.InventoryTools;

public class BlockWarehouseCraftingStation extends Block
{

private BlockIconMap iconMap = new BlockIconMap();

public BlockWarehouseCraftingStation(String regName)
  {
  super(Material.rock);
  this.setBlockName(regName);
  setCreativeTab(AWAutomationItemLoader.automationTab);
  }

public BlockWarehouseCraftingStation setIcon(int meta, int side, String texName)
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
  return new TileWarehouseCraftingStation();
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
    TileEntity te = world.getTileEntity(x, y, z);
    if(te instanceof TileWarehouseCraftingStation)
      {
      TileWarehouseCraftingStation twcs = (TileWarehouseCraftingStation)te;      
      InventoryTools.dropInventoryInWorld(world, twcs.layoutMatrix, x, y, z);
      InventoryTools.dropInventoryInWorld(world, twcs.bookInventory, x, y, z);
      }    
    }
  super.breakBlock(world, x, y, z, block, fortune);  
  }


}
