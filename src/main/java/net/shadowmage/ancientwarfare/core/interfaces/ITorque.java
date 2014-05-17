package net.shadowmage.ancientwarfare.core.interfaces;

import java.util.EnumSet;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.shadowmage.ancientwarfare.core.config.AWLog;

public final class ITorque
{
private ITorque(){}//noop the class, it is just a container for the interfaces and static methods

public static interface ITorqueGenerator
{
void setEnergy(double energy);
double getMaxEnergy();
double getEnergyStored();
double getMaxOutput();
EnumSet<ForgeDirection> getOutputDirection();
}

public static interface ITorqueReceiver
{
void setEnergy(double energy);
double getMaxEnergy();
double getEnergyStored();
double getMaxInput();
EnumSet<ForgeDirection> getInputDirections();
}

public static interface ITorqueStorage extends ITorqueGenerator, ITorqueReceiver
{

}

public static interface ITorqueTransport extends ITorqueGenerator, ITorqueReceiver
{

}

public static void transferPower(ITorqueGenerator generator, ForgeDirection from, ITorqueReceiver target)
  {
  ForgeDirection fromOp = from.getOpposite();
  double transferAmount = generator.getMaxOutput();
  if(!target.getInputDirections().contains(fromOp)){transferAmount = 0;}
  if(generator instanceof ITorqueTransport && target instanceof ITorqueTransport)
    {
    if(target.getEnergyStored()<generator.getEnergyStored())
      {
      double diff = (generator.getEnergyStored() - target.getEnergyStored())*0.5d;
      if(transferAmount>diff){transferAmount=diff;}
      }
    else
      {
      transferAmount = 0;
      }
    }
  if(transferAmount>generator.getEnergyStored()){transferAmount = generator.getEnergyStored();}
  if(transferAmount>target.getMaxInput()){transferAmount=target.getMaxInput();}
  if(transferAmount+target.getEnergyStored()>target.getMaxEnergy()){transferAmount=target.getMaxEnergy()-target.getEnergyStored();}
  if(transferAmount>=1)
    {
    AWLog.logDebug("transferring: "+transferAmount+" from: "+generator+" to "+target);
    generator.setEnergy(generator.getEnergyStored()-transferAmount);
    generator.setEnergy(target.getEnergyStored()+transferAmount);
    }
  }

public static void transferPower(World world, int x, int y, int z, ITorqueGenerator generator)
  {
  double[] requestedEnergy = new double[6];
  ITorqueReceiver[] targets = new ITorqueReceiver[6];
  TileEntity te;
  ITorqueReceiver target;
  
  double maxOutput = generator.getMaxOutput();
  if(maxOutput>generator.getEnergyStored()){maxOutput = generator.getEnergyStored();}
  if(maxOutput<1)
    {
    return;
    }  
  double request;
  double totalRequest = 0;  
  for(ForgeDirection d : generator.getOutputDirection())
    {
    te = world.getTileEntity(x+d.offsetX, y+d.offsetY, z+d.offsetZ);
    if(te instanceof ITorqueReceiver)      
      {
      target = (ITorqueReceiver)te;
      if(target.getInputDirections().contains(d.getOpposite()))
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
    for(int i = 0; i<6; i++)
      {
      if(targets[i]==null){continue;}
      target = targets[i];
      request = requestedEnergy[i];
      request *= percentFullfilled;
      target.setEnergy(target.getEnergyStored()+request);
      generator.setEnergy(generator.getEnergyStored()-request);
      if(target.getEnergyStored()>target.getMaxEnergy()){target.setEnergy(target.getMaxEnergy());}
      AWLog.logDebug("transferring: "+request+" from: "+generator+" to "+target);
      }
    }
  }

}
