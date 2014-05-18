package net.shadowmage.ancientwarfare.core.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.tile.TileResearchStation;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockResearchStation extends Block
{

BlockIconMap iconMap = new BlockIconMap();

public BlockResearchStation(String regName)
  {
  super(Material.rock);
  this.setBlockName(regName);
  this.setCreativeTab(AWCoreBlockLoader.coreTab);
  iconMap.setIconTexture(0, 0, "ancientwarfare:core/research_station_bottom");
  iconMap.setIconTexture(1, 0, "ancientwarfare:core/research_station_top");
  iconMap.setIconTexture(2, 0, "ancientwarfare:core/research_station_front");
  iconMap.setIconTexture(3, 0, "ancientwarfare:core/research_station_front");
  iconMap.setIconTexture(4, 0, "ancientwarfare:core/research_station_side");
  iconMap.setIconTexture(5, 0, "ancientwarfare:core/research_station_side");
  }

@Override
@SideOnly(Side.CLIENT)
public IIcon getIcon(int p_149691_1_, int p_149691_2_)
  {
  return iconMap.getIconFor(p_149691_1_, p_149691_2_);
  }

@Override
@SideOnly(Side.CLIENT)
public void registerBlockIcons(IIconRegister p_149651_1_)
  {
  iconMap.registerIcons(p_149651_1_);
  }

@Override
public boolean hasTileEntity(int metadata)
  {
  return true;
  }

@Override
public TileEntity createTileEntity(World world, int metadata)
  {
  return new TileResearchStation();
  }

@Override
public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int p_149727_6_, float p_149727_7_, float p_149727_8_, float p_149727_9_)
  {
  if(!world.isRemote)
    {
    NetworkHandler.INSTANCE.openGui(player, NetworkHandler.GUI_RESEARCH_STATION, x, y, z);    
    }
  return true;
  }

}
