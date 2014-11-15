package net.shadowmage.ancientwarfare.structure.town;

import net.shadowmage.ancientwarfare.core.config.AWLog;
import net.shadowmage.ancientwarfare.structure.template.build.StructureBB;

public class TownPartPlot
{

int x, z;//indices in array for that block
private int minX, minZ, maxX, maxZ;

TownPartBlock block;//the owning block
StructureBB bb;//bb of the plot
boolean[] roadBorders;//what directions are adjacent to a road, can be 0-2 total sides (0=center plot, cannot have struct, can only merge with other plots or be 'cosmetic' structs)
boolean closed;//has been used or not (if true, plot has been used by a structure)

public TownPartPlot(TownPartBlock block, StructureBB bb, int x, int z)
  {
  this.block = block;
  this.bb = bb;
  roadBorders = new boolean[4];
  this.x = x;
  this.z = z;
  reseatMinMax();
  }

/**
 * mark this plot and any merged plots as closed
 */
public void markClosed()
  {
  for(int x = minX; x<=maxX; x++)
    {
    for(int z = minZ; z<=maxZ; z++)
      {
      AWLog.logDebug("marking plot as closed: "+x+","+z);
      block.getPlot(x, z).closed=true;
      }
    }
  }

public boolean hasRoadBorder()
  {
  for(int i = 0; i<4; i++)
    {
    if(roadBorders[i])
      {
      return true;
      }
    }
  return false;
  }

private void reseatMinMax()
  {
  this.minX = x;
  this.minZ = z;
  this.maxX = x;
  this.maxZ = z;  
  }

/**
 * Expands THIS plot to include the passed in plot.<br>
 * The passed-in plot should be discarded as it is no longer valid
 * @param other
 */
public void merge(TownPartPlot other)
  {
  if(other.bb.min.x<bb.min.x){bb.min.x=other.bb.min.x;}
  if(other.bb.max.x>bb.max.x){bb.max.x=other.bb.max.x;}
  if(other.bb.min.z<bb.min.z){bb.min.z=other.bb.min.z;}
  if(other.bb.max.z>bb.max.z){bb.max.z=other.bb.max.z;}
  for(int i = 0; i < 4; i++)
    {
    if(other.roadBorders[i]){this.roadBorders[i]=true;}
    }
  }

public int getWidth()
  {
  return (bb.max.x - bb.min.x)+1;  
  }

public int getLength()
  {
  return (bb.max.z - bb.min.z)+1;
  }

public boolean expand(int xSize, int zSize)
  {
  AWLog.logDebug("trying to expand plot: "+bb);
  StructureBB bb = this.bb.copy();//will revert to this bb if expansion fails for any reason
  boolean val = tryExpand(xSize, zSize);
  if(!val)//no expansion...reset bb and min/max indices
    {
    reseatMinMax();
    this.bb = bb;
    } 
  return val;
  }

private boolean tryExpand(int xSize, int zSize)
  {
  while(getWidth()<xSize)
    {
    if(!expandEast() && !expandWest())
      {
      AWLog.logDebug("could not expand east/west");
      return false;
      }
    }  
  while(getLength()<zSize)
    {
    if(!expandNorth() && !expandSouth())
      {
      AWLog.logDebug("could not expand north/south");
      return false;
      }
    }
  return true;
  }

private boolean expandNorth()
  {
  if(minZ<=0){return false;}
  for(int x = minX; x<=maxX; x++)
    {
    if(block.getPlot(x, minZ-1).closed)
      {
      AWLog.logDebug("found closed plot, not expanding.");
      return false;
      }
    }
  minZ--;
  TownPartPlot p = block.getPlot(x, minZ);
  this.bb.min.z = p.bb.min.z;
  AWLog.logDebug("expanded plot: "+bb);
  return true;  
  }

private boolean expandSouth()
  {
  if(maxZ+1>=block.plotsLength){return false;}
  for(int x = minX; x<=maxX; x++)
    {
    if(block.getPlot(x, maxZ+1).closed)
      {
      AWLog.logDebug("found closed plot, not expanding.");
      return false;
      }
    }
  maxZ++;
  TownPartPlot p = block.getPlot(x, maxZ);
  this.bb.max.z = p.bb.max.z;
  AWLog.logDebug("expanded plot: "+bb);
  return true;
  }

private boolean expandEast()
  {
  if(minX<=0){return false;}
  for(int z = minZ; z<=maxZ; z++)
    {
    if(block.getPlot(minX-1, z).closed)
      {
      AWLog.logDebug("found closed plot, not expanding.");
      return false;
      }
    }  
  minX--;
  TownPartPlot p = block.getPlot(minX, z);
  this.bb.min.x = p.bb.min.x;
  AWLog.logDebug("expanded plot: "+bb);
  return true;
  }

private boolean expandWest()
  {
  if(maxX+1>=block.plotsWidth){return false;}
  for(int z = minZ; z<=maxZ; z++)
    {
    if(block.getPlot(maxX+1, z).closed)
      {
      AWLog.logDebug("found closed plot, not expanding.");
      return false;
      }
    }  
  maxX++;
  TownPartPlot p = block.getPlot(maxX, z);
  this.bb.max.x = p.bb.max.x;
  AWLog.logDebug("expanded plot: "+bb);
  return true;
  }

}
