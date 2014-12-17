package net.shadowmage.ancientwarfare.structure.gamedata;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;
import net.shadowmage.ancientwarfare.core.util.Trig;
import net.shadowmage.ancientwarfare.structure.template.build.StructureBB;

public class TownMap extends WorldSavedData
{

public static final String NAME = "AWTownMap";

private HashMap<Integer, List<StructureBB>> townBoundingBoxesByDim = new HashMap<Integer, List<StructureBB>>();

public TownMap(String name)
  {
  super(name);
  }

public TownMap()
  {
  this(NAME);
  }

public void setGenerated(World world, StructureBB bb)
  {
  int dim = world.provider.dimensionId;
  List<StructureBB> bbs = townBoundingBoxesByDim.get(dim);
  if(bbs==null){townBoundingBoxesByDim.put(dim, bbs = new ArrayList<StructureBB>());}
  bbs.add(bb);
  markDirty();
  }

/**
 * return the distance of the closest found town or defaultVal if no town was found closer
 * @param world
 * @param bx
 * @param bz
 * @param defaultVal
 * @return
 */
public float getClosestTown(World world, int bx, int bz, float defaultVal)
  {
  float distance = defaultVal;
  float d;  
  int dim = world.provider.dimensionId;
  List<StructureBB> bbs = townBoundingBoxesByDim.get(dim);
  if(bbs!=null && !bbs.isEmpty())
    {
    for(StructureBB bb : bbs)
      {
      d = Trig.getDistance(bx, 0, bz, bb.getCenterX(), 0, bb.getCenterZ());
      if(d<distance){distance = d;}
      }
    }
  return distance;
  }

public boolean isChunkInUse(World world, int cx, int cz)
  {
  int dim = world.provider.dimensionId;
  List<StructureBB> bbs = townBoundingBoxesByDim.get(dim);
  if(bbs!=null && !bbs.isEmpty())
    {
    cx *= 16;
    cz *= 16;
    for(StructureBB bb : bbs)
      {
      if(bb.isPositionInBoundingBox(cx, bb.min.y, cz)){return true;}
      }
    }
  return false;
  }

@Override
public void readFromNBT(NBTTagCompound tag)
  {
  
  }

@Override
public void writeToNBT(NBTTagCompound tag)
  {
  
  }

}
