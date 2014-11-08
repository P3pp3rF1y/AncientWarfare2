package net.shadowmage.ancientwarfare.structure.town;

public class TownBoundingArea
{

private final static int borderSize = 8;//MUST be >0 or will cause weirdness when doing...everything...

int chunkMinX;
int chunkMaxX;
int chunkMinZ;
int chunkMaxZ;
int minY;
int maxY;
int townCenterX;//calculated center of the town area, to be used for main road positioning and generation start
int townCenterZ;//calculated center of the town area, to be used for main road positioning and generation start
/**
 * 0=n, 1=e, 2=s, 3=w -- determines placement of town-hall building (if any) and direction that 'main street' runs.<br>
 * 0=road runs e/w, main structure is on north side of the road (facing south)<br>
 * 1=road runs n/s, main structure is on east side of the road (facing west)<br>
 * 2=road runs e/w, main structure is on south side of the road (facing north)<br>
 * 3=road runs n/s, main structure is on west side of the road (facing east)<br>
 */
int townOrientation;

int wallSize = 3;//should be >0 if walls are desired (must be set by generator prior to generating, not used in validation)

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

public int getCenterX(){return townCenterX;}
public int getCenterZ(){return townCenterZ;}

public int getTownOrientation(){return townOrientation;}

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
