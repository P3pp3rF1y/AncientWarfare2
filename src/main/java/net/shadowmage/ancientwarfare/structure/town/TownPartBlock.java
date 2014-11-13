package net.shadowmage.ancientwarfare.structure.town;

import java.util.ArrayList;
import java.util.List;

import net.shadowmage.ancientwarfare.core.config.AWLog;
import net.shadowmage.ancientwarfare.core.util.BlockPosition;
import net.shadowmage.ancientwarfare.core.util.Trig;
import net.shadowmage.ancientwarfare.structure.template.build.StructureBB;

public class TownPartBlock
{

float distFromTownCenter;
boolean[] roadBorders;
int plotSize = 10;
TownPartQuadrant quadrant;
StructureBB bb;
List<TownPartPlot> plots;

TownPartPlot[][] plotsA;

public TownPartBlock(TownPartQuadrant quadrant, StructureBB bb, boolean[] roadBorders)
  {
  this.quadrant = quadrant;
  this.bb = bb;
  this.roadBorders = roadBorders;
  plots = new ArrayList<TownPartPlot>();
  
  int townCenterX = quadrant.town.width/2;
  int townCenterZ = quadrant.town.length/2;
  int w = (bb.max.x - bb.min.x) + 1;
  int l = (bb.max.z - bb.min.z) + 1;
  int bcx = bb.min.x + (w/2);
  int bcz = bb.min.z + (l/2);
  distFromTownCenter = Trig.getDistance(townCenterX, 0, townCenterZ, bcx, 0, bcz);
  }

public void subdivide()
  {
  int xWidth = (bb.max.x - bb.min.x)+1;
  int zLength = (bb.max.z - bb.min.z)+1;
  if(roadBorders[1]){xWidth--;}
  if(roadBorders[3]){xWidth--;}
  if(roadBorders[0]){zLength--;}
  if(roadBorders[2]){zLength--;}
  int xDivs, zDivs;
  xDivs = xWidth/plotSize;
  if(xWidth%plotSize!=0){xDivs++;}
  zDivs = zLength/plotSize;
  if(zLength%plotSize!=0){zDivs++;}  
  
  AWLog.logDebug("subdividing town block: "+bb+" :: "+xDivs+" : "+zDivs);
  
  plotsA = new TownPartPlot[xDivs][zDivs];
  int widthToUse, lengthToUse;
  int xStart, xEnd;
  int zStart, zEnd;  
  int xSize, zSize;
  int xIndex, zIndex;
  
  TownPartPlot plot;
  boolean[] roadBorders;
  
  xStart = quadrant.xDir<0 ? bb.max.x : bb.min.x;
  if(quadrant.xDir<0 && this.roadBorders[3]){xStart--;}//if generating west && has eastern road
  else if(quadrant.xDir>0 && this.roadBorders[1]){xStart++;}//if generating east && has western road
  
  widthToUse = xWidth;
  for(int x = 0; x<xDivs; x++)
    {
    xSize = widthToUse > plotSize ? plotSize : widthToUse;
    xEnd = xStart + (xSize-1) * quadrant.xDir;
    xIndex = quadrant.xDir < 0? (xDivs-1)-x : x;
    
    zStart = quadrant.zDir<0 ? bb.max.z : bb.min.z;
    if(quadrant.zDir<0 && this.roadBorders[0]){zStart--;}//generation is to the north && has southern road
    else if(quadrant.zDir>0 && this.roadBorders[2]){zStart++;}//generation is to the south && has northern road
    lengthToUse = zLength;
    for(int z = 0; z<zDivs; z++)
      {
      roadBorders = new boolean[4];
      setRoadBorders(x==0, z==0, x==xDivs-1, z==zDivs-1, roadBorders);
      zSize = lengthToUse > plotSize ? plotSize : lengthToUse;
      zEnd = zStart + quadrant.zDir * (zSize - 1);
      zIndex = quadrant.zDir < 0 ? (zDivs-1)-z : z;
      
      plot = new TownPartPlot(this, new StructureBB(new BlockPosition(xStart, 0, zStart), new BlockPosition(xEnd, 0, zEnd)), roadBorders, xIndex, zIndex);
      plots.add(plot);
      plotsA[xIndex][zIndex]=plot;
      AWLog.logDebug("set plot to index: "+plot.bb+" x: "+xIndex+" z: "+zIndex);
      
      lengthToUse -= plotSize;
      zStart = zEnd + quadrant.zDir;
      }
    
    widthToUse -= plotSize;
    xStart = xEnd + quadrant.xDir;
    }
  }

private void setRoadBorders(boolean startX, boolean startZ, boolean endX, boolean endZ, boolean[] borders)
  {
  if(startX)
    {
    if(quadrant.xDir>0){borders[3]=true;}//w
    else{borders[1]=true;}//e
    }
  if(endX)
    {
    if(quadrant.xDir>0){borders[1]=true;}//e
    else{borders[3]=true;}//w
    }
  if(startZ)
    {
    if(quadrant.zDir>0){borders[0]=true;}//n
    else{borders[2]=true;}//s
    }
  if(endZ)
    {
    if(quadrant.zDir>0){borders[2]=true;}//s
    else{borders[0]=true;}//n
    }
  }


}
