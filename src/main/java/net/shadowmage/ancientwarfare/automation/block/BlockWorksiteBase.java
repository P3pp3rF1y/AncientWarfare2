package net.shadowmage.ancientwarfare.automation.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;
import net.shadowmage.ancientwarfare.automation.item.AWAutomationItemLoader;
import net.shadowmage.ancientwarfare.core.block.BlockIconRotationMap;
import net.shadowmage.ancientwarfare.core.block.RelativeSide;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockWorksiteBase extends Block
{

BlockIconRotationMap iconMap = new BlockIconRotationMap();
public BlockWorksiteBase(Material p_i45394_1_, String regName)
  {
  super(p_i45394_1_);
  this.setCreativeTab(AWAutomationItemLoader.automationTab);
  this.setBlockName(regName);
  }

public BlockWorksiteBase addIcon(RelativeSide relativeSide, String texName)
  {
  this.iconMap.setIconTexture(relativeSide, texName);
  return this;
  }

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
public IIcon getIcon(int p_149691_1_, int p_149691_2_)
  {
  return iconMap.getIconFor(p_149691_1_, p_149691_2_);
  }

}
