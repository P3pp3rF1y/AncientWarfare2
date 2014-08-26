package net.shadowmage.ancientwarfare.core.upgrade;

import java.util.EnumSet;
import java.util.Set;

import net.shadowmage.ancientwarfare.automation.item.ItemWorksiteUpgrade;
import net.shadowmage.ancientwarfare.core.api.AWItems;

public enum WorksiteUpgrade
{

SIZE_MEDIUM,
SIZE_LARGE,
QUARRY_MEDIUM,
QUARRY_LARGE,
ENCHANTED_TOOLS_1,
ENCHATNED_TOOLS_2,
TOOL_QUALITY_1,
TOOL_QUALITY_2,
TOOL_QUALITY_3,
;

public static void init()
  {
  ItemWorksiteUpgrade item = (ItemWorksiteUpgrade)AWItems.worksiteUpgrade;
  //TODO register icons for upgrade items...
  }

public String unlocalizedName(){return AWItems.worksiteUpgrade.getUnlocalizedName()+"."+ordinal();}

/**
 * Set of upgrades that prevent this upgrade from being applied<br>
 * e.g. SIZE_LARGE prevents SIZE_MEDIUM from being applied
 */
public Set<WorksiteUpgrade> exclusiveSet = EnumSet.noneOf(WorksiteUpgrade.class);

/**
 * Set of upgrades that are replaced when this upgrade is installed<br>
 * e.g. SIZE_LARGE overrides SIZE_MEDIUM
 */
public Set<WorksiteUpgrade> overrideSet = EnumSet.noneOf(WorksiteUpgrade.class);

}
