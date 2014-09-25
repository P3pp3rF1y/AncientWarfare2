package net.shadowmage.ancientwarfare.core.interfaces;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.shadowmage.ancientwarfare.automation.proxy.BCProxy;
import net.shadowmage.ancientwarfare.core.config.AWLog;

public final class ITorque
{
private ITorque(){}//noop the class, it is just a container for the interfaces and static methods

public static interface ITorqueTile
{
ForgeDirection getPrimaryFacing();
void setEnergy(double energy);
double getMaxEnergy();
double getEnergyStored();
double addEnergy(ForgeDirection from, double energy);
double getEnergyDrainFactor();
double getMaxOutput();
double getMaxInput();
double getEnergyOutput();//return the energy output last tick
boolean canOutput(ForgeDirection towards);
boolean canInput(ForgeDirection from);
boolean cascadedInput();
TileEntity[] getNeighbors();
ITorqueTile[] getNeighborTorqueTiles(); 
double getClientRotation();
double getPrevClientRotation();
boolean useClientRotation();
}

public static void applyPowerDrain(ITorqueTile tile)
  {
  World world = ((TileEntity)tile).getWorldObj();
  world.theProfiler.startSection("AWPowerDrain");
  double e = tile.getEnergyStored();
  double m = tile.getMaxEnergy();
  double d = tile.getEnergyDrainFactor();
  if(e < 0.01d || m <=0 || d <= 0)
    {
    world.theProfiler.endSection();
    return;    
    }
  double p = e/m;
  double edpt = p*d*0.05d;
  tile.setEnergy(e-edpt);
  world.theProfiler.endSection();
  }

/**
 * cached arrays used during tile updates, to remove per-tile-per-tick garbage creation
 */
private static double[] requestedEnergy = new double[6];
private static ITorqueTile[] targets = new ITorqueTile[6];

public static void transferPower(World world, int x, int y, int z, ITorqueTile generator)
  {
  if(generator.getMaxOutput()<=0){return;}
  world.theProfiler.startSection("AWPowerTransfer");
  ITorqueTile[] tes = generator.getNeighborTorqueTiles();
  ITorqueTile te;
  ITorqueTile target;
  
  double maxOutput = generator.getMaxOutput();
  if(maxOutput > generator.getEnergyStored()){maxOutput = generator.getEnergyStored();}
//  if(maxOutput<1)
//    {
//    world.theProfiler.endSection();
//    return;
//    }  
  double request;
  double totalRequest = 0;
  
  ForgeDirection d;
  for(int i = 0; i < 6; i++)
    {    
    d = ForgeDirection.getOrientation(i);
    targets[d.ordinal()]=null;
    requestedEnergy[d.ordinal()]=0;
    if(!generator.canOutput(d)){continue;}
    te = tes[i];
    if(te !=null)      
      {
      target = te;
      if(target.canInput(d.getOpposite()))
        {
        targets[d.ordinal()]=target;  
        request = target.getMaxInput();
        if(target.cascadedInput() && generator.cascadedInput())
          {          
          double teo = target.getEnergyOutput()*0.5d;
          if(target.getEnergyStored() - teo < generator.getEnergyStored())
            {
            double diff = (generator.getEnergyStored() - target.getEnergyStored()) * 0.5d + teo;
            if(request>diff){request=diff;}
            }
          else
            {
            request = 0;
            }
//          AWLog.logDebug("doing cascade transfer from "+generator+" to: "+target+" calcd req: "+request + " tout "+target.getEnergyOutput() +" tmax: "+target.getMaxEnergy());
          }
        if(request + target.getEnergyStored() > target.getMaxEnergy()){request = target.getMaxEnergy()-target.getEnergyStored();}
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
      request = target.addEnergy(ForgeDirection.getOrientation(i).getOpposite(), request);
      generator.setEnergy(generator.getEnergyStored()-request);
      if(target.getEnergyStored()>target.getMaxEnergy()){target.setEnergy(target.getMaxEnergy());}
      }
    }
  BCProxy.instance.transferPower(world, x, y, z, generator);
  world.theProfiler.endSection();
  }

}
