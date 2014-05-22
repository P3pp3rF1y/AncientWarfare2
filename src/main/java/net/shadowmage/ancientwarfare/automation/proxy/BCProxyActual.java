package net.shadowmage.ancientwarfare.automation.proxy;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.shadowmage.ancientwarfare.core.interfaces.ITorque.ITorqueGenerator;
import net.shadowmage.ancientwarfare.core.interfaces.ITorque.ITorqueReceiver;
import buildcraft.api.mj.IBatteryObject;
import buildcraft.api.mj.MjAPI;
import buildcraft.api.power.IPowerEmitter;
import buildcraft.transport.PipeTransportPower;
import buildcraft.transport.TileGenericPipe;

public class BCProxyActual extends BCProxy
{

@Override
public boolean isPowerPipe(World world, TileEntity te)
  {
  if(te==null){return false;}
  if(te instanceof TileGenericPipe)
    {
    TileGenericPipe tgp = (TileGenericPipe)te;
    if(tgp.pipe!=null && tgp.pipe.transport instanceof PipeTransportPower)
      {
      return true;
      }
    }
  return false;
  }

@Override
public void transferPower(World world, int x, int y, int z, ITorqueGenerator generator)
  {
  if(!(generator instanceof IPowerEmitter)){return;}
  world.theProfiler.startSection("AW-BC-PowerUpdate");
  double[] requestedEnergy = new double[6];
  
  IBatteryObject[] targets = new IBatteryObject[6];
  TileEntity[] tes = generator.getNeighbors();
  TileEntity te;
  
  IBatteryObject target;
  
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
    te = tes[i];//world.getTileEntity(x+d.offsetX, y+d.offsetY, z+d.offsetZ);
    if(te instanceof ITorqueReceiver){continue;}
    target = MjAPI.getMjBattery(te);
    if(target==null){continue;}
    targets[d.ordinal()]=target;  
    request = target.maxReceivedPerCycle();
    if(request +target.getEnergyStored() > target.maxCapacity()){request = target.maxCapacity()-target.getEnergyStored();}
    if(request>0)
      {
      requestedEnergy[d.ordinal()]=request;
      totalRequest += request;          
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
      request = target.addEnergy(request);
      generator.setEnergy(generator.getEnergyStored()-request);  
      }
    }
  world.theProfiler.endSection();
  }

}
