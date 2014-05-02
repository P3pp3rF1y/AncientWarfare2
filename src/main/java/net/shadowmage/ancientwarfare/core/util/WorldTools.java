package net.shadowmage.ancientwarfare.core.util;

import java.util.Collections;
import java.util.List;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public class WorldTools
{

public static List<TileEntity> getTileEntitiesInArea(World world, int x1, int y1, int z1, int x2, int y2, int z2)
  {  
  if(world instanceof WorldServer)
    {
    return (List<TileEntity>) ((WorldServer)world).func_147486_a(x1, y1, z1, x2, y2, z2);
    }
  return Collections.emptyList();
  }

}
