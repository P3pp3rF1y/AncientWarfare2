package net.shadowmage.ancientwarfare.core.interfaces;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.shadowmage.ancientwarfare.automation.proxy.BCProxy;

public final class ITorque
{
private ITorque(){}//noop the class, it is just a container for the interfaces and static methods

public static interface ITorqueTile
{
double getMaxTorque();
double getTorqueStored();
double addTorque(ForgeDirection from, double energy);
double getTorqueTransferLossPercent();
double getMaxTorqueOutput();
double getMaxTorqueInput();
double getTorqueOutput();//return the energy output last tick
boolean canOutputTorque(ForgeDirection towards);
boolean canInputTorque(ForgeDirection from);

@Deprecated
TileEntity[] getNeighbors();
@Deprecated
ITorqueTile[] getNeighborTorqueTiles();
@Deprecated
boolean cascadedInput();
@Deprecated
ForgeDirection getPrimaryFacing();
@Deprecated
void setTorqueEnergy(double energy);


double getClientOutputRotation();
double getPrevClientOutputRotation();
boolean useClientRotation();
}

public static double addEnergy(ITorqueTile tile, ForgeDirection from, double energy)
  {
  if(tile.canInputTorque(from))
    {
    energy = energy > tile.getMaxTorqueInput() ? tile.getMaxTorqueInput() : energy;
    energy = energy + tile.getTorqueStored() > tile.getMaxTorque() ? tile.getMaxTorque()-tile.getTorqueStored() : energy;
    tile.setTorqueEnergy(tile.getTorqueStored()+energy);
    return energy;
    }
  return 0;
  }

public static void applyPowerDrain(ITorqueTile tile)
  {
  World world = ((TileEntity)tile).getWorldObj();
  world.theProfiler.startSection("AWPowerDrain");
  double e = tile.getTorqueStored();
  double m = tile.getMaxTorque();
  double d = tile.getTorqueTransferLossPercent();
  if(e < 0.01d || m <=0 || d <= 0)
    {
    world.theProfiler.endSection();
    return;    
    }
  double p = e/m;
  double edpt = p*d*0.05d;
  tile.setTorqueEnergy(e-edpt);
  world.theProfiler.endSection();
  }

/**
 * cached arrays used during tile updates, to remove per-tile-per-tick garbage creation
 */
private static double[] requestedEnergy = new double[6];
private static ITorqueTile[] targets = new ITorqueTile[6];

public static void transferPower(World world, int x, int y, int z, ITorqueTile generator)
  {
  if(generator.getMaxTorqueOutput()<=0){return;}
  world.theProfiler.startSection("AWPowerTransfer");
  ITorqueTile[] tes = generator.getNeighborTorqueTiles();
  ITorqueTile te;
  ITorqueTile target;
  
  double maxOutput = generator.getMaxTorqueOutput();
  if(maxOutput > generator.getTorqueStored()){maxOutput = generator.getTorqueStored();}
  double request;
  double totalRequest = 0;
  
  ForgeDirection d;
  for(int i = 0; i < 6; i++)
    {    
    d = ForgeDirection.getOrientation(i);
    targets[d.ordinal()]=null;
    requestedEnergy[d.ordinal()]=0;
    if(!generator.canOutputTorque(d)){continue;}
    te = tes[i];
    if(te !=null)      
      {
      target = te;
      if(target.canInputTorque(d.getOpposite()))
        {
        targets[d.ordinal()]=target;  
        request = target.getMaxTorqueInput();
        if(target.cascadedInput() && generator.cascadedInput())
          {          
          double teo = target.getTorqueOutput()*0.5d;
          if(target.getTorqueStored() - teo < generator.getTorqueStored())
            {
            double diff = (generator.getTorqueStored() - target.getTorqueStored()) * 0.5d + teo;
            if(request>diff){request=diff;}
            }
          else
            {
            request = 0;
            }
          }
        if(request + target.getTorqueStored() > target.getMaxTorque()){request = target.getMaxTorque()-target.getTorqueStored();}
        if(request>0)
          {
          requestedEnergy[d.ordinal()]=request;
          totalRequest += request;          
          }          
        }
      }  
    }
  if(totalRequest>0)
    {
    double percentFullfilled = maxOutput / totalRequest;  
    if(percentFullfilled>1.f){percentFullfilled=1.f;}
    for(int i = 0; i<6; i++)
      {
      if(targets[i]==null){continue;}
      target = targets[i];
      request = requestedEnergy[i];
      request *= percentFullfilled;
      request = target.addTorque(ForgeDirection.getOrientation(i).getOpposite(), request);
      generator.setTorqueEnergy(generator.getTorqueStored()-request);
      if(target.getTorqueStored()>target.getMaxTorque()){target.setTorqueEnergy(target.getMaxTorque());}
      }
    }
  BCProxy.instance.transferPower(world, x, y, z, generator);
  world.theProfiler.endSection();
  }

}
