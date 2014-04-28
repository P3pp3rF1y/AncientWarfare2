package net.shadowmage.ancientwarfare.automation.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.automation.item.AWAutomationItemLoader;
import net.shadowmage.ancientwarfare.automation.tile.TileMailbox;
import net.shadowmage.ancientwarfare.core.block.BlockIconRotationMap;
import net.shadowmage.ancientwarfare.core.block.RelativeSide;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockMailbox extends Block
{

BlockIconRotationMap iconMap = new BlockIconRotationMap();

public BlockMailbox(String regName)
  {
  super(Material.rock);
  this.setBlockName(regName);
  this.setCreativeTab(AWAutomationItemLoader.automationTab);
  }

@Override
public boolean hasTileEntity(int metadata)
  {
  return true;
  }

@Override
public TileEntity createTileEntity(World world, int metadata)
  {
  return new TileMailbox();
  }

public BlockMailbox setIcon(RelativeSide relativeSide, String texName)
  {
  this.iconMap.setIconTexture(relativeSide, texName);
  return this;
  }

@Override
@SideOnly(Side.CLIENT)
public void registerBlockIcons(IIconRegister p_149651_1_)
  {
  iconMap.registerIcons(p_149651_1_);
  }

@Override
@SideOnly(Side.CLIENT)
public IIcon getIcon(int p_149691_1_, int p_149691_2_)
  {
  return iconMap.getIconFor(p_149691_1_, p_149691_2_);
  }

@Override
public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ)
  {
  TileMailbox tile = (TileMailbox) world.getTileEntity(x, y, z);
  tile.checkOutputDirection();
  /**
   * TODO
   */
  return false;
  }

}
