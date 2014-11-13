package net.shadowmage.ancientwarfare.structure.town;

import net.shadowmage.ancientwarfare.structure.template.build.StructureBB;

public class TownPartPlot
{

int x, z;//indices in array for that block
TownPartBlock block;//the owning block
StructureBB bb;//bb of the plot
boolean[] roadBorders;//what directions are adjacent to a road, can be 0-2 total sides (0=center plot, cannot have struct, can only merge with other plots or be 'cosmetic' structs)
boolean closed;//has been used or not (if true, plot has been used by a structure)

public TownPartPlot(TownPartBlock block, StructureBB bb, boolean[] borders, int x, int z)
  {
  this.block = block;
  this.bb = bb;
  roadBorders = borders;
  this.x = x;
  this.z = z;
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

public TownPartPlot getNeighborPlot(Direction d)
  {
  int x = this.x + d.xDirection;
  if(x<0 || x>=block.plotsA.length){return null;}
  int z = this.z + d.zDirection;
  if(z<0 || z>=block.plotsA[x].length){return null;}
  return block.plotsA[x][z];
  }

public int getWidth()
  {
  return (bb.max.x - bb.min.x)+1;  
  }

public int getLength()
  {
  return (bb.max.z - bb.min.z)+1;
  }

}
