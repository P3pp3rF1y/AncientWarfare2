package net.shadowmage.ancientwarfare.structure.town;

import java.util.ArrayList;
import java.util.List;

import net.shadowmage.ancientwarfare.core.config.AWLog;
import net.shadowmage.ancientwarfare.core.util.BlockPosition;
import net.shadowmage.ancientwarfare.core.util.Trig;
import net.shadowmage.ancientwarfare.structure.template.build.StructureBB;

public class TownPartBlock
{

int x, z;//index of block in the quadrant
float distFromTownCenter;
private boolean[] roadBorders;
TownPartQuadrant quadrant;
StructureBB bb;
List<TownPartPlot> plots;

private TownPartPlot[] plotsArray;
int plotsWidth, plotsLength;

public TownPartBlock(TownPartQuadrant quadrant, StructureBB bb, int x, int z, boolean[] roadBorders)
  {
  this.quadrant = quadrant;
  this.bb = bb;
  this.x = x;
  this.z = z;
  plots = new ArrayList<TownPartPlot>();
  this.roadBorders = roadBorders;
  
  int townCenterX = quadrant.town.width/2;
  int townCenterZ = quadrant.town.length/2;
  int w = (bb.max.x - bb.min.x) + 1;
  int l = (bb.max.z - bb.min.z) + 1;
  int bcx = bb.min.x + (w/2);
  int bcz = bb.min.z + (l/2);
  distFromTownCenter = Trig.getDistance(townCenterX, 0, townCenterZ, bcx, 0, bcz);
  }

public boolean hasRoadBorder(Direction d)
  {
  return roadBorders[d.ordinal()];
  }

protected void setRoadBorder(Direction d, boolean val)
  {
  roadBorders[d.ordinal()]=val;
  }

public void subdivide()
  {
  int plotSize = quadrant.town.plotSize;
  int xWidth = (bb.max.x - bb.min.x)+1;
  int zLength = (bb.max.z - bb.min.z)+1;
  int xDivs, zDivs;
  xDivs = xWidth/plotSize;
  if(xWidth%plotSize!=0){xDivs++;}
  zDivs = zLength/plotSize;
  if(zLength%plotSize!=0){zDivs++;}  
  plotsWidth = xDivs;
  plotsLength = zDivs;
  AWLog.logDebug("subdividing town block: "+bb+" :: "+xDivs+" : "+zDivs);
  
  plotsArray = new TownPartPlot[xDivs*zDivs];
  int widthToUse, lengthToUse;
  int xStart, xEnd;
  int zStart, zEnd;  
  int xSize, zSize;
  int xIndex, zIndex;
  
  TownPartPlot plot;
  
  xStart = quadrant.getXDir()==Direction.WEST ? bb.max.x : bb.min.x;  
  widthToUse = xWidth;
  for(int x = 0; x<xDivs; x++)
    {
    xSize = widthToUse > plotSize ? plotSize : widthToUse;
    xEnd = xStart + (xSize-1) * quadrant.getXDir().xDirection;
    xIndex = quadrant.getXDir() == Direction.WEST? (xDivs-1)-x : x;
    
    zStart = quadrant.getZDir() == Direction.NORTH ? bb.max.z : bb.min.z;
    lengthToUse = zLength;
    for(int z = 0; z<zDivs; z++)
      {
      zSize = lengthToUse > plotSize ? plotSize : lengthToUse;
      zEnd = zStart + quadrant.getZDir().zDirection * (zSize - 1);
      zIndex = quadrant.getZDir() == Direction.NORTH ? (zDivs-1)-z : z;
      
      plot = new TownPartPlot(this, new StructureBB(new BlockPosition(xStart, 0, zStart), new BlockPosition(xEnd, 0, zEnd)), xIndex, zIndex);
      setRoadBorders(plot);
      
      plots.add(plot);
      setPlot(plot, xIndex, zIndex);
      
      lengthToUse -= plotSize;
      zStart = zEnd + quadrant.getZDir().zDirection;
      }
    
    widthToUse -= plotSize;
    xStart = xEnd + quadrant.getXDir().xDirection;
    }
  }

private void setPlot(TownPartPlot plot, int x, int z)
  {
  plotsArray[getIndex(x, z)]=plot;
  }

private int getIndex(int x, int z)
  {
  return z*plotsWidth + x;
  }

public TownPartPlot getPlot(int x, int z)
  {
  if(x<0 || z<0 || x>=plotsWidth || z>=plotsLength){return null;}
  return plotsArray[getIndex(x, z)];
  }

private void setRoadBorders(TownPartPlot plot)
  {
  //check north side
  if(roadBorders[2] && plot.z==0)
    {
    plot.roadBorders[2]=true;
    }    
  //check south side
  if(roadBorders[0] && plot.z==plotsLength-1)
    {
    plot.roadBorders[0]=true;
    }
  //check west side
  if(roadBorders[1] && plot.x==0)
    {
    plot.roadBorders[1]=true;
    }
  //check east side
  if(roadBorders[3] && plot.x==plotsWidth-1)
    {
    plot.roadBorders[3]=true;
    }
  }

}
