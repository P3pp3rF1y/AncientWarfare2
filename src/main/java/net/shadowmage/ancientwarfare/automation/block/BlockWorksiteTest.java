package net.shadowmage.ancientwarfare.automation.block;

import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.automation.tile.WorkSiteQuarry;
import net.shadowmage.ancientwarfare.core.block.RelativeSide;

public class BlockWorksiteTest extends BlockWorksiteBase
{

public BlockWorksiteTest(String regName)
  {
  super(Material.rock, regName);
  iconMap.setIconTexture(RelativeSide.TOP, "ancientwarfare:civic/civicMineQuarryTop");
  iconMap.setIconTexture(RelativeSide.BOTTOM, "ancientwarfare:civic/civicFarmChickenSides");
  iconMap.setIconTexture(RelativeSide.FRONT, "ancientwarfare:civic/civicMineQuarrySides");
  iconMap.setIconTexture(RelativeSide.REAR, "ancientwarfare:civic/civicFarmCocoaSides");
  iconMap.setIconTexture(RelativeSide.LEFT, "ancientwarfare:civic/civicFarmNetherSides");
  iconMap.setIconTexture(RelativeSide.RIGHT, "ancientwarfare:civic/civicFarmOakSides");  
  }

@Override
public TileEntity createTileEntity(World world, int metadata)
  {  
  return new WorkSiteQuarry();
  }


}
