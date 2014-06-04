package net.shadowmage.ancientwarfare.npc.block;

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
  //TODO set town hall icons
  
  GameRegistry.registerBlock(recruitingStation, ItemBlockOwnedRotatable.class, "recruiting_station");
  GameRegistry.registerTileEntity(TileRecruitingStation.class, "recruiting_station_tile");
  }

}
