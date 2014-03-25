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

private static int DOWN = 0;
private static int UP = 1;
private static int NORTH = 2;
private static int SOUTH = 3;
private static int WEST = 4;
private static int EAST = 5;

public static int TOP = 0;
public static int BOTTOM = 1;
public static int FRONT = 2;
public static int REAR = 3;
public static int LEFT = 4;
public static int RIGHT = 5;

private static int[][] sideMap = new int[6][16];

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
sideMap[DOWN][8] = TOP;
sideMap[DOWN][9] = TOP;
sideMap[DOWN][10] = TOP;
sideMap[DOWN][11] = TOP;
//[SIDE_VIEWED][BLOCK_META]=LOCAL SIDE ICON
sideMap[UP][0] = TOP;
sideMap[UP][1] = TOP;
sideMap[UP][2] = TOP;
sideMap[UP][3] = TOP;
sideMap[UP][4] = FRONT;
sideMap[UP][5] = FRONT;
sideMap[UP][6] = FRONT;
sideMap[UP][7] = FRONT;
sideMap[UP][8] = BOTTOM;
sideMap[UP][9] = BOTTOM;
sideMap[UP][10] = BOTTOM;
sideMap[UP][11] = BOTTOM;
//[SIDE_VIEWED][BLOCK_META]=LOCAL SIDE ICON
sideMap[NORTH][0] = FRONT;
sideMap[NORTH][1] = LEFT;
sideMap[NORTH][2] = REAR;
sideMap[NORTH][3] = RIGHT;
sideMap[NORTH][4] = TOP;
sideMap[NORTH][5] = RIGHT;
sideMap[NORTH][6] = BOTTOM;
sideMap[NORTH][7] = LEFT;
sideMap[NORTH][8] = TOP;
sideMap[NORTH][9] = LEFT;
sideMap[NORTH][10] = BOTTOM;
sideMap[NORTH][11] = RIGHT;
//[SIDE_VIEWED][BLOCK_META]=LOCAL SIDE ICON
sideMap[SOUTH][0] = REAR;
sideMap[SOUTH][1] = RIGHT;
sideMap[SOUTH][2] = FRONT;
sideMap[SOUTH][3] = LEFT;
sideMap[SOUTH][4] = BOTTOM;
sideMap[SOUTH][5] = LEFT;
sideMap[SOUTH][6] = TOP;
sideMap[SOUTH][7] = RIGHT;
sideMap[SOUTH][8] = BOTTOM;
sideMap[SOUTH][9] = RIGHT;
sideMap[SOUTH][10] = TOP;
sideMap[SOUTH][11] = LEFT;
//[SIDE_VIEWED][BLOCK_META]=LOCAL SIDE ICON
sideMap[WEST][0] = LEFT;
sideMap[WEST][1] = REAR;
sideMap[WEST][2] = RIGHT;
sideMap[WEST][3] = FRONT;
sideMap[WEST][4] = RIGHT;
sideMap[WEST][5] = BOTTOM;
sideMap[WEST][6] = LEFT;
sideMap[WEST][7] = TOP;
sideMap[WEST][8] = LEFT;
sideMap[WEST][9] = BOTTOM;
sideMap[WEST][10] = RIGHT;
sideMap[WEST][11] = TOP;
//[SIDE_VIEWED][BLOCK_META]=LOCAL SIDE ICON
sideMap[EAST][0] = RIGHT;
sideMap[EAST][1] = FRONT;
sideMap[EAST][2] = LEFT;
sideMap[EAST][3] = REAR;
sideMap[EAST][4] = LEFT;
sideMap[EAST][5] = TOP;
sideMap[EAST][6] = RIGHT;
sideMap[EAST][7] = BOTTOM;
sideMap[EAST][8] = RIGHT;
sideMap[EAST][9] = TOP;
sideMap[EAST][10] = LEFT;
sideMap[EAST][11] = BOTTOM;
}

/**
 * relative side metadata directional map
 * meta=direction that front of block faces towards (e.g. north==visible when facing south)
 * 0=north
 * 1=east
 * 2=south
 * 3=west
 * 4=up:top=north
 * 5=up:top=east
 * 6=up:top=south
 * 7=up:top=west
 * 8=down:top=north
 * 9=down:top=east
 * 10=down:top=south 
 * 11=down:top=west
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
  if(player.rotationPitch<-45)
    {
    return invert? 8 : 4;
    }  
  else if(player.rotationPitch>45)
    {
    return invert? 4 : 8;
    }
  else
    {
    switch(face)
    {
    case 0://south
    return invert ? 2 : 0;
    case 1://west
    return invert ? 1 : 3;
    case 2://north
    return invert ? 0 : 2;
    case 3://east
    return invert ? 3 : 1;
    default:
    return face;
    }
    }
  }


}
