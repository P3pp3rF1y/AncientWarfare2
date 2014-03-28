package net.shadowmage.ancientwarfare.core.block;

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

private static final int DOWN = 0;
private static final int UP = 1;
private static final int NORTH = 2;
private static final int SOUTH = 3;
private static final int WEST = 4;
private static final int EAST = 5;
private static final RelativeSide[][] sideMap = new RelativeSide[6][16];

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

}
