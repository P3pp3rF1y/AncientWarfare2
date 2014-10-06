package net.shadowmage.ancientwarfare.core.interfaces;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;

public final class ITorque
{
private ITorque(){}//noop the class, it is just a container for the interfaces and static methods

/**
 * Used for rendering of, well, anything that has a default north-oriented model
 */
public static final float[][] forgeDiretctionToRotationMatrix = new float[6][];
static
{
forgeDiretctionToRotationMatrix[0] = new float[]{ -90,   0,   0};//d
forgeDiretctionToRotationMatrix[1] = new float[]{  90,   0,   0};//u
forgeDiretctionToRotationMatrix[2] = new float[]{   0,   0,   0};//n
forgeDiretctionToRotationMatrix[3] = new float[]{   0, 180,   0};//s
forgeDiretctionToRotationMatrix[4] = new float[]{   0,  90,   0};//w
forgeDiretctionToRotationMatrix[5] = new float[]{   0, 270,   0};//e
}

public static interface foo1{}
public static interface foo2{}

public static interface foo3 extends foo1, foo2
{
}


/**
 * Interface for implementation by torque tiles.  Tiles may handle their power internally by any means.<br>
 * Tiles are responsible for outputting their own power, but should not request power from other torque tiles
 * (the other tiles will output power when ready).<br>
 * @author Shadowmage
 */
public static interface ITorqueTile
{

/**
 * Return the maximum amount of energy store-able in the passed in block side
 */
double getMaxTorque(ForgeDirection from);

/**
 * Return the value of energy accessible from the passed in block side
 */
double getTorqueStored(ForgeDirection from);

/**
 * Add energy to the specified block side, up to the specified amount.<br>
 * Return the value of energy actually added, or 0 for none.
 */
double addTorque(ForgeDirection from, double energy);

/**
 * Remove energy from the specified block side, up to the specified amount.<br>
 * Return the value of energy actually removed, or 0 for none.
 */
double drainTorque(ForgeDirection from, double energy);

/**
 * Return the maximum amount of torque that the given side may output AT THIS TIME.<br>
 * Analogous to the 'simulate' actions from other energy frameworks
 */
double getMaxTorqueOutput(ForgeDirection from);

/**
 * Return the maximum amount of torque that the given side may accept AT THIS TIME.<br>
 * Analogous to the 'simulate' actions from other energy frameworks
 */
double getMaxTorqueInput(ForgeDirection from);

/**
 * Return true if this tile can output torque from the given block side.<br>
 * Used by tiles for connection status.<br>
 * Must return the same value between calls, or issue a neighbor-block update when the value changes.<br>
 * You may return true from this method but return 0 for getMaxOutput() for 'toggleable' sides (side will connect but not always accept power)
 */
boolean canOutputTorque(ForgeDirection from);

/**
 * Return true if this tile can input torque into the given block side.<br>
 * Used by tiles for connection status.<br>
 * Must return the same value between calls, or issue a neighbor-block update when the value changes.
 * You may return true from this method but return 0 for getMaxInput() for 'toggleable' sides (side will connect but not always accept power)
 */
boolean canInputTorque(ForgeDirection from);

/**
 * Used by client for rendering of torque tiles.  If TRUE this tiles neighbor will
 * use this tiles output rotation values for rendering of the corresponding input side on the neighbor.
 */
boolean useOutputRotation(ForgeDirection from);

/**
 * Return output shaft rotation for the given side.  Will only be called if useOutputRotation(from) returns true.
 */
float getClientOutputRotation(ForgeDirection from, float delta);
}

/**
 * default (simple) reference implementation of a torque delegate class<br>
 * An ITorqueTile may have one or more of these for internal energy storage (or none, and handle energy entirely differently!).<br>
 * This template class is merely included for convenience. 
 * @author Shadowmage
 *
 */
public static class TorqueCell
{
protected double maxInput, maxOutput, maxEnergy, efficiency;
protected double energy;

public TorqueCell(double in, double out, double max, double eff)
  {
  maxInput = in;
  maxOutput = out;
  maxEnergy = max;
  efficiency = eff;
  }

public double getEnergy(){return energy;}

public double getMaxEnergy(){return maxEnergy;}

public void setEnergy(double in){energy = Math.max(0, Math.min(in, maxEnergy));}

public double addEnergy(double in)
  {
  if(Double.isNaN(in)){throw new RuntimeException("Requested input may not be NAN");}
  //TODO remove these when done with implementation
  in = Math.min(getMaxTickInput(), in);
  energy += in;
  return in;
  }

public double drainEnergy(double request)
  {
  if(Double.isNaN(request)){throw new RuntimeException("Requested drain may not be NAN");}
  //TODO remove these when done with implementation
  request = Math.min(getMaxTickOutput(), request);
  energy -= request;
  return request;
  }

public double getMaxInput(){return maxInput;}

public double getMaxOutput(){return maxOutput;}

public double getMaxTickInput()
  {
  return Math.min(maxInput, getMaxEnergy() - getEnergy());
  }

public double getMaxTickOutput()
  {
  return Math.min(maxOutput, getEnergy());
  }

public NBTTagCompound writeToNBT(NBTTagCompound tag)
  {
  tag.setDouble("energy", energy);
  return tag;
  }

public void readFromNBT(NBTTagCompound tag)
  {
  energy = tag.getDouble("energy");
  }

public double getPercentFull()
  {
  return maxEnergy > 0.d ? energy/maxEnergy : 0.d;
  }
}

/**
 * Owner-sided aware torque storage cell.  Used by MIMO type torque tiles (conduit, distributor) to maintain a cell for each side
 * without having to create new cells every time block orientation changes.<br>
 * Caveat:  the side maintains energy level regardless of block orientation, the new 'input' side will have the energy from an old 'output' side.
 * @author Shadowmage
 */
public static class SidedTorqueCell extends TorqueCell
{

ForgeDirection dir;
ITorqueTile owner;
public SidedTorqueCell(double in, double out, double max, double eff, ForgeDirection dir, ITorqueTile owner)
  {
  super(in, out, max, eff);
  this.dir = dir;
  this.owner = owner;
  }

@Override
public double getMaxTickInput()
  {
  return owner.canInputTorque(dir) ? super.getMaxTickInput() : 0;
  }

@Override
public double getMaxTickOutput()
  {
  return owner.canOutputTorque(dir) ? super.getMaxTickOutput() : 0;
  }

@Override
public double addEnergy(double in)
  {
  return owner.canInputTorque(dir) ? super.addEnergy(in) : 0;
  }

@Override
public double drainEnergy(double request)
  {
  return owner.canOutputTorque(dir) ? super.drainEnergy(request) : 0;
  }

}

}
