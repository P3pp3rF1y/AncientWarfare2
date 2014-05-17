package net.shadowmage.ancientwarfare.core.interfaces;

import java.util.EnumSet;

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

double getMaxOutput(ForgeDirection toSide);
EnumSet<ForgeDirection> getOutputDirection();
}

public static interface ITorqueReceiver
{
void setEnergy(double energy);
double getMaxEnergy();
double getEnergyStored();

double addEnergy(double energy);
double getMaxInput(ForgeDirection fromSide);
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
  double transferAmount = generator.getMaxOutput(from);
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
  if(transferAmount>target.getMaxInput(fromOp)){transferAmount=target.getMaxInput(fromOp);}
  if(transferAmount+target.getEnergyStored()>target.getMaxEnergy()){transferAmount=target.getMaxEnergy()-target.getEnergyStored();}
  if(transferAmount>=1)
    {
    AWLog.logDebug("transferring: "+transferAmount+" from: "+generator+" to "+target);
    generator.setEnergy(generator.getEnergyStored()-transferAmount);
    target.addEnergy(transferAmount);
    }
  }

}
