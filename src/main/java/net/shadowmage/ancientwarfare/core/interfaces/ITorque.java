package net.shadowmage.ancientwarfare.core.interfaces;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.shadowmage.ancientwarfare.automation.proxy.BCProxy;

public final class ITorque
{
private ITorque(){}//noop the class, it is just a container for the interfaces and static methods

public static interface ITorqueGenerator
{
void setEnergy(double energy);
double getMaxEnergy();
double getEnergyStored();
double getMaxOutput();
boolean canOutput(ForgeDirection towards);
}

public static interface ITorqueReceiver
{
double addEnergy(ForgeDirection from, double energy);
void setEnergy(double energy);
double getMaxEnergy();
double getEnergyStored();
double getMaxInput();
boolean canInput(ForgeDirection from);
}

public static interface ITorqueStorage extends ITorqueGenerator, ITorqueReceiver
{

}

public static interface ITorqueTransport extends ITorqueGenerator, ITorqueReceiver
{

}


public static void transferPower(World world, int x, int y, int z, ITorqueGenerator generator)
  {
  world.theProfiler.startSection("AWPower");
  double[] requestedEnergy = new double[6];
  ITorqueReceiver[] targets = new ITorqueReceiver[6];
  TileEntity te;
  ITorqueReceiver target;
  
  double maxOutput = generator.getMaxOutput();
  if(maxOutput>generator.getEnergyStored()){maxOutput = generator.getEnergyStored();}
  if(maxOutput<1)
    {
    world.theProfiler.endSection();
    return;
    }  
  double request;
  double totalRequest = 0;
  
  ForgeDirection d;
  for(int i = 0; i < 6; i++)
    {
    d = ForgeDirection.getOrientation(i);
    if(!generator.canOutput(d)){continue;}
    te = world.getTileEntity(x+d.offsetX, y+d.offsetY, z+d.offsetZ);
    if(te instanceof ITorqueReceiver)      
      {
      target = (ITorqueReceiver)te;
      if(target.canInput(d.getOpposite()))
        {
        targets[d.ordinal()]=target;  
        request = target.getMaxInput();
        if(generator instanceof ITorqueTransport && target instanceof ITorqueTransport)
          {
          if(target.getEnergyStored()<generator.getEnergyStored())
            {
            double diff = (generator.getEnergyStored() - target.getEnergyStored())*0.5d;
            if(request>diff){request=diff;}
            }
          else
            {
            request = 0;
            }
          }
        if(request +target.getEnergyStored() > target.getMaxEnergy()){request = target.getMaxEnergy()-target.getEnergyStored();}
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
//      AWLog.logDebug("transferring: "+request+" from: "+generator+" to "+target);
      }
    }
  BCProxy.instance.transferPower(world, x, y, z, generator);
  world.theProfiler.endSection();
  }

}
