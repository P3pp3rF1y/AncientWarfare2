package net.shadowmage.ancientwarfare.core.interfaces;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.shadowmage.ancientwarfare.automation.proxy.BCProxy;

public final class ITorque
{
private ITorque(){}//noop the class, it is just a container for the interfaces and static methods

////TODO re-implement torque tiles under these interfaces
public static interface ITorqueProvider
{
ITorqueCell getTorqueCell(ForgeDirection blockSide);//may return different cell for each side, or same cell
boolean canInput(ForgeDirection blockSide);//used for connection rules for rendering
boolean canOutput(ForgeDirection blockSide);//used for connection rules for rendering
}

public static interface ITorqueOutputRender
{
boolean useClientOutputRotation(ForgeDirection blockSide);
double getClientRotation();
double getClientPrevRotation();
}

public static interface ITorqueCell
{
double getEnergyStored(ForgeDirection side);
double getMaxEnergyStored(ForgeDirection side);
double getMaxInput(ForgeDirection side);
double getMaxOutput(ForgeDirection side);
double addEnergy(ForgeDirection side, double energy);
double extractEnergy(ForgeDirection side, double energy);
double getEnergyLossPercentPerTick();
boolean cascadedInput(ForgeDirection blockSide);
boolean cascadedOutput(ForgeDirection blockSide);
}

public abstract static class TorqueCell implements ITorqueCell
{

double energy, maxEnergy;

public TorqueCell(double maxEnergy)
  {
  this.maxEnergy = maxEnergy;
  }

@Override
public double getMaxEnergyStored(ForgeDirection side){return maxEnergy;}

@Override
public double getEnergyStored(ForgeDirection side){return energy;}

}

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
double getClientOutputRotation();
double getPrevClientOutputRotation();
boolean useClientRotation();
}

public static double addEnergy(ITorqueTile tile, ForgeDirection from, double energy)
  {
  if(tile.canInput(from))
    {
    energy = energy > tile.getMaxInput() ? tile.getMaxInput() : energy;
    energy = energy + tile.getEnergyStored() > tile.getMaxEnergy() ? tile.getMaxEnergy()-tile.getEnergyStored() : energy;
    tile.setEnergy(tile.getEnergyStored()+energy);
    return energy;
    }
  return 0;
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
