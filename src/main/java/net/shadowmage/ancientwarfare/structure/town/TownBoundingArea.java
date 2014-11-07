package net.shadowmage.ancientwarfare.structure.town;

public class TownBoundingArea
{

int chunkMinX;
int chunkMaxX;
int chunkMinZ;
int chunkMaxZ;
int minY;
int maxY;

private final static int borderSize = 8;//MUST be >0 or will cause weirdness when doing...everything...

int wallSize = 3;//should be >0 if walls are desired (must be set by generator prior to generating, not used in validation)
int wallHeight = 8;//must be >0 if wallSize >0 (must be set by generator prior to generating, not used in validation)

public int getChunkWidth(){return (chunkMaxX-chunkMinX)+1;}
public int getChunkLength(){return (chunkMaxZ-chunkMinZ)+1;}
public int getChunkMinX(){return chunkMinX;}
public int getChunkMaxX(){return chunkMaxX;}
public int getChunkMinZ(){return chunkMinZ;}
public int getChunkMaxZ(){return chunkMaxZ;}

public int getBlockMinX(){return chunkMinX*16;}
public int getBlockMaxX(){return chunkMaxX*16+15;}
public int getBlockMinZ(){return chunkMinZ*16;}
public int getBlockMaxZ(){return chunkMaxZ*16+15;}
public int getBlockWidth(){return getBlockMaxX()-getBlockMinX()+1;}
public int getBlockLength(){return getBlockMaxZ()-getBlockMinZ()+1;}

public int getWallMinX(){return getBlockMinX() - 1 + borderSize;}
public int getWallMaxX(){return getBlockMaxX() - 1 - borderSize;}
public int getWallMinZ(){return getBlockMinZ() - 1 + borderSize;}
public int getWallMaxZ(){return getBlockMaxZ() - 1 - borderSize;}

public int getTownMinX(){return getBlockMinX() - 1 + wallSize + borderSize;}
public int getTownMaxX(){return getBlockMaxX() - 1 - wallSize - borderSize;}
public int getTownMinZ(){return getBlockMinZ() - 1 + wallSize + borderSize;}
public int getTownMaxZ(){return getBlockMaxZ() - 1 - wallSize - borderSize;}

public int getMinY(){return minY;}
public int getMaxY(){return maxY;}
public int getSurfaceY(){return minY+7;}

@Override
public String toString()
  {
  int minX = getBlockMinX();
  int maxX = getBlockMaxX();
  int minZ = getBlockMinZ();
  int maxZ = getBlockMaxZ();
  return "TownArea: "+minX+"  :"+minZ+" :: "+maxX+" : "+maxZ +" size: "+getBlockWidth()+" : "+getBlockLength();
  }
}
