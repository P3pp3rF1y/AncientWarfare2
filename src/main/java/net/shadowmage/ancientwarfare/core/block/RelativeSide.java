package net.shadowmage.ancientwarfare.core.block;

import net.minecraftforge.common.util.ForgeDirection;
import net.shadowmage.ancientwarfare.core.config.AWLog;


/**
 * relative side rotation mapping<br>
 *<br>
 * relative side metadata directional map<br>
 * meta=direction that front of block faces towards (e.g. north==visible when facing south)<br>
 * 0=south<br>
 * 1=west<br>
 * 2=north<br>
 * 3=east<br>
 * 4=up:top=south<br>
 * 5=up:top=west<br>
 * 6=up:top=north<br>
 * 7=up:top=east<br>
 * 8=down:top=south<br>
 * 9=down:top=west<br>
 * 10=down:top=north<br>
 * 11=down:top=east<br>
 *
 * @author Shadowmage
 */
public enum RelativeSide
{

TOP("guistrings.inventory.side.top"),
BOTTOM("guistrings.inventory.side.bottom"),
FRONT("guistrings.inventory.side.front"),
REAR("guistrings.inventory.side.rear"),
LEFT("guistrings.inventory.side.left"),
RIGHT("guistrings.inventory.side.right");

/**
 * 0-5 forge-direction ordinals
 */
private static final int DOWN = 0;
private static final int UP = 1;
private static final int NORTH = 2;
private static final int SOUTH = 3;
private static final int WEST = 4;
private static final int EAST = 5;

private static final int META_SOUTH = 0;
private static final int META_WEST = 1;
private static final int META_NORTH = 2;
private static final int META_EAST = 3;
private static final int META_UP_SOUTH = 4;
private static final int META_UP_WEST = 5;
private static final int META_UP_NORTH = 6;
private static final int META_UP_EAST = 7;
private static final int META_DOWN_SOUTH = 8;
private static final int META_DOWN_WEST = 9;
private static final int META_DOWN_NORTH = 10;
private static final int META_DOWN_EAST = 11;

private static final RelativeSide[][] sideMap = new RelativeSide[6][16];//[SIDE_VIEWED][BLOCK_META]=LOCAL SIDE ICON

private static final int[][] rotationMap = new int[16][6];//[BLOCK_META][AXIS_CLICKED_ORDINAL]=ROTATED META

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
sideMap[SOUTH][4] = TOP;
sideMap[SOUTH][5] = RIGHT;
sideMap[SOUTH][6] = BOTTOM;
sideMap[SOUTH][7] = LEFT;
sideMap[SOUTH][8] = TOP;
sideMap[SOUTH][9] = LEFT;
sideMap[SOUTH][10] = BOTTOM;
sideMap[SOUTH][11] = RIGHT;

//[SIDE_VIEWED][BLOCK_META]=LOCAL SIDE ICON
sideMap[NORTH][0] = REAR;
sideMap[NORTH][1] = RIGHT;
sideMap[NORTH][2] = FRONT;
sideMap[NORTH][3] = LEFT;
sideMap[NORTH][4] = BOTTOM;
sideMap[NORTH][5] = LEFT;
sideMap[NORTH][6] = TOP;
sideMap[NORTH][7] = RIGHT;
sideMap[NORTH][8] = BOTTOM;
sideMap[NORTH][9] = RIGHT;
sideMap[NORTH][10] = TOP;
sideMap[NORTH][11] = LEFT;

//[SIDE_VIEWED][BLOCK_META]=LOCAL SIDE ICON
sideMap[EAST][0] = LEFT;
sideMap[EAST][1] = REAR;
sideMap[EAST][2] = RIGHT;
sideMap[EAST][3] = FRONT;
sideMap[EAST][4] = RIGHT;
sideMap[EAST][5] = BOTTOM;
sideMap[EAST][6] = LEFT;
sideMap[EAST][7] = TOP;
sideMap[EAST][8] = LEFT;
sideMap[EAST][9] = BOTTOM;
sideMap[EAST][10] = RIGHT;
sideMap[EAST][11] = TOP;

//[SIDE_VIEWED][BLOCK_META]=LOCAL SIDE ICON
sideMap[WEST][0] = RIGHT;
sideMap[WEST][1] = FRONT;
sideMap[WEST][2] = LEFT;
sideMap[WEST][3] = REAR;
sideMap[WEST][4] = LEFT;
sideMap[WEST][5] = TOP;
sideMap[WEST][6] = RIGHT;
sideMap[WEST][7] = BOTTOM;
sideMap[WEST][8] = RIGHT;
sideMap[WEST][9] = TOP;
sideMap[WEST][10] = LEFT;
sideMap[WEST][11] = BOTTOM;


//[BLOCK_META][AXIS_CLICKED_ORDINAL]=ROTATED META
rotationMap[META_SOUTH][DOWN]=META_EAST;
rotationMap[META_SOUTH][UP]=META_WEST;
rotationMap[META_SOUTH][SOUTH]=META_SOUTH;
rotationMap[META_SOUTH][WEST]=META_DOWN_SOUTH;
rotationMap[META_SOUTH][NORTH]=META_SOUTH;
rotationMap[META_SOUTH][EAST]=META_UP_NORTH;

rotationMap[META_EAST][DOWN]=META_NORTH;
rotationMap[META_EAST][UP]=META_SOUTH;
rotationMap[META_EAST][SOUTH]=META_DOWN_EAST;
rotationMap[META_EAST][WEST]=META_EAST;
rotationMap[META_EAST][NORTH]=META_UP_WEST;
rotationMap[META_EAST][EAST]=META_EAST;

rotationMap[META_NORTH][DOWN]=META_WEST;
rotationMap[META_NORTH][UP]=META_EAST;
rotationMap[META_NORTH][SOUTH]=META_NORTH;
rotationMap[META_NORTH][WEST]=META_UP_SOUTH;
rotationMap[META_NORTH][NORTH]=META_NORTH;
rotationMap[META_NORTH][EAST]=META_DOWN_NORTH;

rotationMap[META_WEST][DOWN]=META_SOUTH;
rotationMap[META_WEST][UP]=META_NORTH;
rotationMap[META_WEST][SOUTH]=META_UP_EAST;
rotationMap[META_WEST][WEST]=META_WEST;
rotationMap[META_WEST][NORTH]=META_DOWN_WEST;
rotationMap[META_WEST][EAST]=META_WEST;

rotationMap[META_UP_SOUTH][DOWN]=META_UP_EAST;
rotationMap[META_UP_SOUTH][UP]=META_UP_WEST;
rotationMap[META_UP_SOUTH][SOUTH]=META_UP_SOUTH;
rotationMap[META_UP_SOUTH][WEST]=META_SOUTH;
rotationMap[META_UP_SOUTH][NORTH]=META_UP_SOUTH;
rotationMap[META_UP_SOUTH][EAST]=META_NORTH;

rotationMap[META_UP_WEST][DOWN]=META_UP_SOUTH;
rotationMap[META_UP_WEST][UP]=META_UP_NORTH;
rotationMap[META_UP_WEST][SOUTH]=META_EAST;
rotationMap[META_UP_WEST][WEST]=META_UP_WEST;
rotationMap[META_UP_WEST][NORTH]=META_WEST;
rotationMap[META_UP_WEST][EAST]=META_UP_WEST;

rotationMap[META_UP_NORTH][DOWN]=META_UP_WEST;
rotationMap[META_UP_NORTH][UP]=META_UP_EAST;
rotationMap[META_UP_NORTH][SOUTH]=META_UP_NORTH;
rotationMap[META_UP_NORTH][WEST]=META_SOUTH;
rotationMap[META_UP_NORTH][NORTH]=META_UP_NORTH;
rotationMap[META_UP_NORTH][EAST]=META_NORTH;

rotationMap[META_UP_EAST][DOWN]=META_UP_NORTH;
rotationMap[META_UP_EAST][UP]=META_UP_SOUTH;
rotationMap[META_UP_EAST][SOUTH]=META_EAST;
rotationMap[META_UP_EAST][WEST]=META_UP_EAST;
rotationMap[META_UP_EAST][NORTH]=META_WEST;
rotationMap[META_UP_EAST][EAST]=META_UP_EAST;

rotationMap[META_DOWN_SOUTH][DOWN]=META_DOWN_EAST;
rotationMap[META_DOWN_SOUTH][UP]=META_DOWN_WEST;
rotationMap[META_DOWN_SOUTH][SOUTH]=META_DOWN_SOUTH;
rotationMap[META_DOWN_SOUTH][WEST]=META_NORTH;
rotationMap[META_DOWN_SOUTH][NORTH]=META_DOWN_SOUTH;
rotationMap[META_DOWN_SOUTH][EAST]=META_SOUTH;

rotationMap[META_DOWN_WEST][DOWN]=META_DOWN_SOUTH;
rotationMap[META_DOWN_WEST][UP]=META_DOWN_NORTH;
rotationMap[META_DOWN_WEST][SOUTH]=META_WEST;
rotationMap[META_DOWN_WEST][WEST]=META_DOWN_WEST;
rotationMap[META_DOWN_WEST][NORTH]=META_EAST;
rotationMap[META_DOWN_WEST][EAST]=META_DOWN_WEST;

rotationMap[META_DOWN_NORTH][DOWN]=META_DOWN_WEST;
rotationMap[META_DOWN_NORTH][UP]=META_DOWN_EAST;
rotationMap[META_DOWN_NORTH][SOUTH]=META_DOWN_NORTH;
rotationMap[META_DOWN_NORTH][WEST]=META_NORTH;
rotationMap[META_DOWN_NORTH][NORTH]=META_DOWN_NORTH;
rotationMap[META_DOWN_NORTH][EAST]=META_SOUTH;

rotationMap[META_DOWN_EAST][DOWN]=META_DOWN_NORTH;
rotationMap[META_DOWN_EAST][UP]=META_DOWN_SOUTH;
rotationMap[META_DOWN_EAST][SOUTH]=META_WEST;
rotationMap[META_DOWN_EAST][WEST]=META_DOWN_EAST;
rotationMap[META_DOWN_EAST][NORTH]=META_EAST;
rotationMap[META_DOWN_EAST][EAST]=META_DOWN_EAST;
}

private final String regName;

RelativeSide(String transKey)
  {
  regName = transKey;
  }

public String getTranslationKey()
  {
  return regName;
  }

public static RelativeSide getRelativeSide(int mcSide, int meta)
  {
  return sideMap[mcSide][meta];
  }

/**
 * get the nsew direction to access a relative side from for a given meta
 * @param side
 * @param meta
 * @return
 */
public static int getAccessDirection(RelativeSide side, int meta)
  {
  for(int i = 0; i < sideMap.length; i++)
    {
    if(sideMap[i][meta]==side)
      {
      return i;
      }
    }
  return -1;
  }

public static ForgeDirection getFacingDirectionFor(RelativeSide side, int meta)
  {
  int dir = getAccessDirection(side, meta);
  ForgeDirection d = ForgeDirection.getOrientation(dir);
  return d;
  }

public static int getRotatedMeta(int currentMeta, ForgeDirection rotateAxis, boolean canFaceVertical)
  {
  if(!canFaceVertical && (rotateAxis!=ForgeDirection.UP && rotateAxis!=ForgeDirection.DOWN))
    {
    return (currentMeta+1)%4;
    }
  return rotationMap[currentMeta][rotateAxis.ordinal()];
  }

}
