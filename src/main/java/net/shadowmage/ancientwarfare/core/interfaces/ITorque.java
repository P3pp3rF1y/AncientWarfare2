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

/**
 * Interface for implementation by torque tiles.
 * @author Shadowmage
 */
public static interface ITorqueTile
{
double getMaxTorque(ForgeDirection from);
double getTorqueStored(ForgeDirection from);
double addTorque(ForgeDirection from, double energy);
double drainTorque(ForgeDirection from, double energy);
double getMaxTorqueOutput(ForgeDirection from);
double getMaxTorqueInput(ForgeDirection from);
boolean canOutputTorque(ForgeDirection towards);
boolean canInputTorque(ForgeDirection from);

double getClientOutputRotation(ForgeDirection from);
double getPrevClientOutputRotation(ForgeDirection from);
boolean useOutputRotation(ForgeDirection from);
}

/**
 * default (simple) implementation of a torque delegate class
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
  in = Math.min(getMaxInput(), in);
  energy+=in;
  return in;
  }

public double drainEnergy(double request)
  {
  request = Math.min(getMaxOutput(), request);
  energy-=request;
  return request;
  }

public double getMaxInput()
  {
  return Math.min(maxInput, maxEnergy - energy);
  }

public double getMaxOutput()
  {
  return Math.min(maxOutput, energy);
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
}

//public static double addEnergy(ITorqueTile tile, ForgeDirection from, double energy)
//  {
//  if(tile.canInputTorque(from))
//    {
//    energy = energy > tile.getMaxTorqueInput(null) ? tile.getMaxTorqueInput(null) : energy;
//    energy = energy + tile.getTorqueStored(null) > tile.getMaxTorque(null) ? tile.getMaxTorque(null)-tile.getTorqueStored(null) : energy;
//    tile.setTorqueEnergy(tile.getTorqueStored(null)+energy);
//    return energy;
//    }
//  return 0;
//  }
//
//public static void applyPowerDrain(ITorqueTile tile)
//  {
//  World world = ((TileEntity)tile).getWorldObj();
//  world.theProfiler.startSection("AWPowerDrain");
//  double e = tile.getTorqueStored(null);
//  double m = tile.getMaxTorque(null);
//  double d = tile.getTorqueTransferLossPercent();
//  if(e < 0.01d || m <=0 || d <= 0)
//    {
//    world.theProfiler.endSection();
//    return;    
//    }
//  double p = e/m;
//  double edpt = p*d*0.05d;
//  tile.setTorqueEnergy(e-edpt);
//  world.theProfiler.endSection();
//  }
//
///**
// * cached arrays used during tile updates, to remove per-tile-per-tick garbage creation
// */
//private static double[] requestedEnergy = new double[6];
//private static ITorqueTile[] targets = new ITorqueTile[6];
//
//public static void transferPower(World world, int x, int y, int z, ITorqueTile generator)
//  {
//  if(generator.getMaxTorqueOutput(null)<=0){return;}
//  world.theProfiler.startSection("AWPowerTransfer");
//  ITorqueTile[] tes = generator.getNeighborTorqueTiles();
//  ITorqueTile te;
//  ITorqueTile target;
//  
//  double maxOutput = generator.getMaxTorqueOutput(null);
//  if(maxOutput > generator.getTorqueStored(null)){maxOutput = generator.getTorqueStored(null);}
//  double request;
//  double totalRequest = 0;
//  
//  ForgeDirection d;
//  for(int i = 0; i < 6; i++)
//    {    
//    d = ForgeDirection.getOrientation(i);
//    targets[d.ordinal()]=null;
//    requestedEnergy[d.ordinal()]=0;
//    if(!generator.canOutputTorque(d)){continue;}
//    te = tes[i];
//    if(te !=null)      
//      {
//      target = te;
//      if(target.canInputTorque(d.getOpposite()))
//        {
//        targets[d.ordinal()]=target;  
//        request = target.getMaxTorqueInput(null);
//        if(target.cascadedInput() && generator.cascadedInput())
//          {          
//          double teo = target.getTorqueOutput()*0.5d;
//          if(target.getTorqueStored(null) - teo < generator.getTorqueStored(null))
//            {
//            double diff = (generator.getTorqueStored(null) - target.getTorqueStored(null)) * 0.5d + teo;
//            if(request>diff){request=diff;}
//            }
//          else
//            {
//            request = 0;
//            }
//          }
//        if(request + target.getTorqueStored(null) > target.getMaxTorque(null)){request = target.getMaxTorque(null)-target.getTorqueStored(null);}
//        if(request>0)
//          {
//          requestedEnergy[d.ordinal()]=request;
//          totalRequest += request;          
//          }          
//        }
//      }  
//    }
//  if(totalRequest>0)
//    {
//    double percentFullfilled = maxOutput / totalRequest;  
//    if(percentFullfilled>1.f){percentFullfilled=1.f;}
//    for(int i = 0; i<6; i++)
//      {
//      if(targets[i]==null){continue;}
//      target = targets[i];
//      request = requestedEnergy[i];
//      request *= percentFullfilled;
//      request = target.addTorque(ForgeDirection.getOrientation(i).getOpposite(), request);
//      generator.setTorqueEnergy(generator.getTorqueStored(null)-request);
//      if(target.getTorqueStored(null)>target.getMaxTorque(null)){target.setTorqueEnergy(target.getMaxTorque(null));}
//      }
//    }
//  BCProxy.instance.transferPower(world, x, y, z, generator);
//  world.theProfiler.endSection();
//  }

}
