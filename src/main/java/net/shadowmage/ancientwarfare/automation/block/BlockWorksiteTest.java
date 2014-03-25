package net.shadowmage.ancientwarfare.automation.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.automation.item.AWAutomationItemLoader;
import net.shadowmage.ancientwarfare.automation.tile.WorkSiteQuarry;
import net.shadowmage.ancientwarfare.core.block.BlockIconRotationMap;

public class BlockWorksiteTest extends Block
{

BlockIconRotationMap iconMap = new BlockIconRotationMap();

public BlockWorksiteTest(String regName)
  {
  super(Material.rock);
  this.setCreativeTab(AWAutomationItemLoader.automationTab);
  this.setBlockName(regName);
  iconMap.setIconTexture(iconMap.TOP, "ancientwarfare:civic/civicMineQuarryTop");
  iconMap.setIconTexture(iconMap.BOTTOM, "ancientwarfare:civic/civicFarmChickenSides");
  iconMap.setIconTexture(iconMap.FRONT, "ancientwarfare:civic/civicMineQuarrySides");
  iconMap.setIconTexture(iconMap.REAR, "ancientwarfare:civic/civicFarmCocoaSides");
  iconMap.setIconTexture(iconMap.LEFT, "ancientwarfare:civic/civicFarmNetherSides");
  iconMap.setIconTexture(iconMap.RIGHT, "ancientwarfare:civic/civicFarmOakSides");  
  }

@Override
public TileEntity createTileEntity(World world, int metadata)
  {  
  return new WorkSiteQuarry();
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
