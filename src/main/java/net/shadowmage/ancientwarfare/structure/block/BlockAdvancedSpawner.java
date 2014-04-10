package net.shadowmage.ancientwarfare.structure.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.automation.item.AWAutomationItemLoader;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.structure.item.AWStructuresItemLoader;
import net.shadowmage.ancientwarfare.structure.tile.TileAdvancedSpawner;

public class BlockAdvancedSpawner extends Block
{

public BlockAdvancedSpawner(String regName)
  {
  super(Material.rock);
  this.setCreativeTab(AWStructuresItemLoader.structureTab);
  this.setBlockName(regName);
  this.setBlockTextureName("ancientwarfare:civic/civicMineQuarrySides");
  }

@Override
public boolean hasTileEntity(int metadata)
  {
  return true;
  }

@Override
public TileEntity createTileEntity(World world, int metadata)
  {
  return new TileAdvancedSpawner();
  }

@Override
public void breakBlock(World world, int x, int y, int z, Block block, int meta)
  {
  TileEntity te = world.getTileEntity(x, y, z);
  if(!world.isRemote && te instanceof TileAdvancedSpawner)
    {
    TileAdvancedSpawner spawner = (TileAdvancedSpawner)te;
    spawner.onBlockBroken();
    }
  super.breakBlock(world, x, y, z, block, meta);
  }

@Override
public float getBlockHardness(World world, int x, int y, int z)
  {
  TileEntity te = world.getTileEntity(x, y, z);
  if(te instanceof TileAdvancedSpawner)
    {
    TileAdvancedSpawner spawner = (TileAdvancedSpawner)te;
    return spawner.getBlockHardness();
    }
  return super.getBlockHardness(world, x, y, z);
  }

@Override
public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z)
  {
  TileEntity te = world.getTileEntity(x, y, z);
  if(te instanceof TileAdvancedSpawner)
    {
    ItemStack item = new ItemStack(this);
    NBTTagCompound settings = new NBTTagCompound();
    ((TileAdvancedSpawner) te).getSettings().writeToNBT(settings);
    item.setTagInfo("spawnerSettings", settings);
    return item;
    }
  return super.getPickBlock(target, world, x, y, z);
  }

@Override
public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int sideHit, float hitX, float hitY, float hitZ)
  {
  if(player.capabilities.isCreativeMode && !world.isRemote)
    {
    NetworkHandler.INSTANCE.openGui(player, NetworkHandler.GUI_SPAWNER_ADVANCED_BLOCK, x, y, z);
    return false;
    }
  return super.onBlockActivated(world, x, y, z, player, sideHit, hitX, hitY, hitZ);
  }

}
