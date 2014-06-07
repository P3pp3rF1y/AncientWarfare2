package net.shadowmage.ancientwarfare.npc.block;

import net.shadowmage.ancientwarfare.core.block.BlockRotationHandler.RelativeSide;
import net.shadowmage.ancientwarfare.core.item.ItemBlockOwnedRotatable;
import net.shadowmage.ancientwarfare.npc.tile.TileRecruitingStation;
import net.shadowmage.ancientwarfare.npc.tile.TileTownHall;
import cpw.mods.fml.common.registry.GameRegistry;

public class AWNPCBlockLoader
{

public static final BlockTownHall townHall = new BlockTownHall("town_hall");
public static final BlockRecruitingStation recruitingStation = new BlockRecruitingStation("recruiting_station");

public static void load()
  {
  GameRegistry.registerBlock(townHall, ItemBlockOwnedRotatable.class, "town_hall");
  GameRegistry.registerTileEntity(TileTownHall.class, "town_hall_tile");
  townHall.iconMap.setIcon(townHall, RelativeSide.TOP, "ancientwarfare:npc/town_hall_top");
  townHall.iconMap.setIcon(townHall, RelativeSide.BOTTOM, "ancientwarfare:npc/town_hall_bottom");
  townHall.iconMap.setIcon(townHall, RelativeSide.LEFT, "ancientwarfare:npc/town_hall_side");
  townHall.iconMap.setIcon(townHall, RelativeSide.RIGHT, "ancientwarfare:npc/town_hall_side");
  townHall.iconMap.setIcon(townHall, RelativeSide.FRONT, "ancientwarfare:npc/town_hall_side");
  townHall.iconMap.setIcon(townHall, RelativeSide.REAR, "ancientwarfare:npc/town_hall_side");
  
  GameRegistry.registerBlock(recruitingStation, ItemBlockOwnedRotatable.class, "recruiting_station");
  GameRegistry.registerTileEntity(TileRecruitingStation.class, "recruiting_station_tile");
  }

}
