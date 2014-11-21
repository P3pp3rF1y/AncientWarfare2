package net.shadowmage.ancientwarfare.structure.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.block.BlockIconMap;
import net.shadowmage.ancientwarfare.structure.item.AWStructuresItemLoader;
import net.shadowmage.ancientwarfare.structure.tile.TileStructureBuilder;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockStructureBuilder extends Block
{

private BlockIconMap iconMap = new BlockIconMap();

public BlockStructureBuilder(String regName)
  {
  super(Material.rock);
  this.setBlockName(regName);
  this.setCreativeTab(AWStructuresItemLoader.structureTab);
  setHardness(2.f);
  }

public BlockStructureBuilder setIcon(int meta, int side, String texName)
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
  return new TileStructureBuilder();
  }

@Override
public void breakBlock(World world, int x, int y, int z, Block block, int meta)
  {
  if(!world.isRemote)
    {
    TileEntity te = world.getTileEntity(x, y, z);
    if(te instanceof TileStructureBuilder)
      {
      ((TileStructureBuilder) te).onBlockBroken();
      }
    }
  }

@Override
public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float vecX, float vecY, float vecZ)
  {
  if(!world.isRemote)
    {
    TileEntity te = world.getTileEntity(x, y, z);
    if(te instanceof TileStructureBuilder)
      {
      TileStructureBuilder builder = (TileStructureBuilder)te;
      builder.onBlockClicked(player);
      }
    }
  return true;
  }
  

}
