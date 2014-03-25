package net.shadowmage.ancientwarfare.core.block;

import java.util.HashMap;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.Direction;
import net.minecraft.util.IIcon;
import net.shadowmage.ancientwarfare.core.config.AWLog;
import net.shadowmage.ancientwarfare.core.util.BlockTools;

/**
 * icon storage class for meta-rotatable blocks
 * @author Shadowmage
 */
public class BlockIconRotationMap
{

private static final int DOWN = 0;
private static final int UP = 1;
private static final int NORTH = 2;
private static final int SOUTH = 3;
private static final int WEST = 4;
private static final int EAST = 5;

public static final int TOP = 0;
public static final int BOTTOM = 1;
public static final int FRONT = 2;
public static final int REAR = 3;
public static final int LEFT = 4;
public static final int RIGHT = 5;

private static final int[][] sideMap = new int[6][16];

/**
  iconMap.setIconTexture(iconMap.TOP, "ancientwarfare:civic/civicMineQuarryTop");//blank
  iconMap.setIconTexture(iconMap.BOTTOM, "ancientwarfare:civic/civicFarmChickenSides");//chicken
  iconMap.setIconTexture(iconMap.FRONT, "ancientwarfare:civic/civicMineQuarrySides");
  iconMap.setIconTexture(iconMap.REAR, "ancientwarfare:civic/civicFarmCocoaSides");
  iconMap.setIconTexture(iconMap.LEFT, "ancientwarfare:civic/civicFarmNetherSides");
  iconMap.setIconTexture(iconMap.RIGHT, "ancientwarfare:civic/civicFarmOakSides");  
 */
static
{
// [SIDE_VIEWED][BLOCK_META]=LOCAL SIDE ICON
sideMap[DOWN][0] = BOTTOM;
sideMap[DOWN][1] = BOTTOM;
sideMap[DOWN][2] = BOTTOM;
sideMap[DOWN][3] = BOTTOM;
sideMap[DOWN][4] = REAR;
sideMap[DOWN][5] = REAR;
sideMap[DOWN][6] = REAR;
sideMap[DOWN][7] = REAR;
sideMap[DOWN][8] = FRONT;
sideMap[DOWN][9] = FRONT;
sideMap[DOWN][10] = FRONT;
sideMap[DOWN][11] = FRONT;

//[SIDE_VIEWED][BLOCK_META]=LOCAL SIDE ICON
sideMap[UP][0] = TOP;
sideMap[UP][1] = TOP;
sideMap[UP][2] = TOP;
sideMap[UP][3] = TOP;
sideMap[UP][4] = FRONT;
sideMap[UP][5] = FRONT;
sideMap[UP][6] = FRONT;
sideMap[UP][7] = FRONT;
sideMap[UP][8] = REAR;
sideMap[UP][9] = REAR;
sideMap[UP][10] = REAR;
sideMap[UP][11] = REAR;

//[SIDE_VIEWED][BLOCK_META]=LOCAL SIDE ICON
sideMap[SOUTH][0] = FRONT;
sideMap[SOUTH][1] = LEFT;
sideMap[SOUTH][2] = REAR;
sideMap[SOUTH][3] = RIGHT;
sideMap[SOUTH][4] = BOTTOM;
sideMap[SOUTH][5] = LEFT;
sideMap[SOUTH][6] = TOP;
sideMap[SOUTH][7] = RIGHT;
sideMap[SOUTH][8] = BOTTOM;
sideMap[SOUTH][9] = RIGHT;
sideMap[SOUTH][10] = TOP;
sideMap[SOUTH][11] = LEFT;

//[SIDE_VIEWED][BLOCK_META]=LOCAL SIDE ICON
sideMap[NORTH][0] = REAR;
sideMap[NORTH][1] = RIGHT;
sideMap[NORTH][2] = FRONT;
sideMap[NORTH][3] = LEFT;
sideMap[NORTH][4] = TOP;
sideMap[NORTH][5] = RIGHT;
sideMap[NORTH][6] = BOTTOM;
sideMap[NORTH][7] = LEFT;
sideMap[NORTH][8] = TOP;
sideMap[NORTH][9] = LEFT;
sideMap[NORTH][10] = BOTTOM;
sideMap[NORTH][11] = RIGHT;

//[SIDE_VIEWED][BLOCK_META]=LOCAL SIDE ICON
sideMap[EAST][0] = LEFT;
sideMap[EAST][1] = REAR;
sideMap[EAST][2] = RIGHT;
sideMap[EAST][3] = FRONT;
sideMap[EAST][4] = LEFT;
sideMap[EAST][5] = TOP;
sideMap[EAST][6] = RIGHT;
sideMap[EAST][7] = BOTTOM;
sideMap[EAST][8] = RIGHT;
sideMap[EAST][9] = TOP;
sideMap[EAST][10] = LEFT;
sideMap[EAST][11] = BOTTOM;

//[SIDE_VIEWED][BLOCK_META]=LOCAL SIDE ICON
sideMap[WEST][0] = RIGHT;
sideMap[WEST][1] = FRONT;
sideMap[WEST][2] = LEFT;
sideMap[WEST][3] = REAR;
sideMap[WEST][4] = RIGHT;
sideMap[WEST][5] = BOTTOM;
sideMap[WEST][6] = LEFT;
sideMap[WEST][7] = TOP;
sideMap[WEST][8] = LEFT;
sideMap[WEST][9] = BOTTOM;
sideMap[WEST][10] = RIGHT;
sideMap[WEST][11] = TOP;
}

/**
 * relative side metadata directional map
 * meta=direction that front of block faces towards (e.g. north==visible when facing south)
 * 0=south
 * 1=west
 * 2=north
 * 3=east
 * 4=up:top=south
 * 5=up:top=west
 * 6=up:top=north
 * 7=up:top=east
 * 8=down:top=south
 * 9=down:top=west
 * 10=down:top=north 
 * 11=down:top=east
 * 
 *   -Y 0
 *  DOWN(0, -1, 0),
     +Y 1
    UP(0, 1, 0),
     -Z 2
    NORTH(0, 0, -1),
     +Z 3
    SOUTH(0, 0, 1),
    -X 4
    WEST(-1, 0, 0),
     +X 5
    EAST(1, 0, 0),
 */

HashMap<Integer, IIcon> iconMap = new HashMap<Integer, IIcon>();
HashMap<Integer, String> iconTexMap = new HashMap<Integer, String>();

public void setIconTexture(int relativeSide, String texName)
  {
  iconTexMap.put(relativeSide, texName);
  }

public void registerIcons(IIconRegister reg)
  {
  String tex;
  IIcon icon;
  for(Integer key : iconTexMap.keySet())
    {
    tex = iconTexMap.get(key);
    icon = reg.registerIcon(tex);
    iconMap.put(key, icon);
    }
  }

public IIcon getIconFor(int side, int meta)
  {
//  AWLog.logDebug("getting icon for side: "+side+" meta: "+meta);
  int relativeSide = getRelativeSide(side, meta);
//  AWLog.logDebug("relative side: "+relativeSide);
  return iconMap.get(relativeSide);
  }

private static int getRelativeSide(int mcSide, int meta)
  {
  return sideMap[mcSide][meta];
  }

public static int getBlockMetaForPlacement(EntityPlayer player)
  {
  boolean invert = player.isSneaking();
  int face = BlockTools.getPlayerFacingFromYaw(player.rotationYaw);
  if(player.isSneaking())
    {
    face = (face+2)%4;
    }
  if(player.rotationPitch<-45)
    {
    return ((face+2)%4) + (invert? 4 : 8);
    }  
  else if(player.rotationPitch>45)
    {
    return ((face+2)%4) + (invert? 8 : 4);
    }
  else
    {
    return (face+2)%4;
    }
  }


}
