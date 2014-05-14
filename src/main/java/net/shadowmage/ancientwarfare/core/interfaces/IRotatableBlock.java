package net.shadowmage.ancientwarfare.core.interfaces;

public interface IRotatableBlock
{

public static enum RotationType
{
/**
 * Can have 6 textures / inventories.<br>
 * Top, Bottom, Front, Rear, Left, Right<br>
 * Can only face in one of four-directions - N/S/E/W
 */
FOUR_WAY,
/**
 * Can have 3 textures / inventories<br>
 * Top, Bottom, Sides<br>
 * Can face in any orientation - U/D/N/S/E/W
 */
SIX_WAY
}

public RotationType getRotationType();

}
