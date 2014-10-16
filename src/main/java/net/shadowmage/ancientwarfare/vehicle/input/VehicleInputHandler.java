package net.shadowmage.ancientwarfare.vehicle.input;

import net.shadowmage.ancientwarfare.core.AncientWarfareCore;
import net.shadowmage.ancientwarfare.vehicle.entity.VehicleBase;
import net.shadowmage.ancientwarfare.vehicle.network.PacketInputChange;
import net.shadowmage.ancientwarfare.vehicle.network.PacketInputReply;
import net.shadowmage.ancientwarfare.vehicle.network.PacketInputState;

public class VehicleInputHandler
{

private VehicleBase vehicle;
private boolean wasRidden;
private InputState inputState = new InputState();

InputSnapshot[] inputBuffer;
int bufferIndex;

double x, y, z;
float yaw, pitch;
int lerpTicks;

public VehicleInputHandler(VehicleBase vehicle)
  {
  this.vehicle = vehicle;
  if(vehicle.worldObj.isRemote){inputBuffer = new InputSnapshot[40];}//two seconds worth of buffer..should be WAY more than needed
  }

public void onKeyChanged(VehicleInputKey key, boolean newState)
  {
  inputState.state[key.ordinal()]=newState;
  if(newState){inputState.pressed[key.ordinal()]=true;}
  }

public void handleVanillaSynch(double x, double y, double z, float yaw, float pitch, int ticks)
  {
  this.x = x;
  this.y = y;
  this.z = z; 
  this.yaw = yaw;
  this.pitch = pitch;
  this.lerpTicks = ticks;
  }

/**
 * Handle an input response packet from server
 * @param pkt
 */
public void handleResponsePacket(PacketInputReply pkt)
  {
  
  }

public void handleInputChange(PacketInputChange pkt){}

public void handleInputState(PacketInputState pkt){}

/**
 * Should be called from owning entity on entity-update tick.
 */
public void onUpdate()
  {
  vehicle.worldObj.theProfiler.startSection("awVehicleMove");
  if(vehicle.worldObj.isRemote){clientUpdate();}
  else{serverUpdate();}
  vehicle.worldObj.theProfiler.endSection();
  }

private void serverUpdate()
  {
  boolean ridden = vehicle.riddenByEntity!=null;
  if(!ridden && wasRidden)//had a rider, does not now.
    {
    //TODO clear all input states, update all remote clients with full synch packet (position, motion, input state clear), return to vanilla synch stuff
    }
  updateMotion();
  }

private void clientUpdate()
  {
  if(vehicle.riddenByEntity==AncientWarfareCore.proxy.getClientPlayer())
    {
    incrementBufferIndex();
    updateMotion();
    /**
     * clear input pressed cache
     */
    clearInputCache();    
    }
  else//vanilla motion synch, merely re-interpret and lerp
    {
    if(lerpTicks>0)
      {
      //TODO lerp between current and intended position
      }
    else
      {
      vehicle.posX = x;
      vehicle.posY = y;
      vehicle.posZ = z;
      vehicle.rotationYaw = yaw;
      vehicle.rotationPitch = pitch;
      }
    }
  }

private void updateMotion()
  {
  //TODO will pass actual motion updating to a delegate class on a per-vehicle-type basis.
  //delegate classes will be responsible for the various and differing forms of movement possible for different vehicle types
  }

private void clearInputCache()
  {
  for(int i = 0; i < inputState.pressed.length; i++)
    {
    inputState.pressed[i] = false;
    }  
  }

private void incrementBufferIndex()
  {
  bufferIndex++;
  if(bufferIndex>=inputBuffer.length){bufferIndex=0;}
  }

/**
 * current input state, used by movement calculation
 * @author Shadowmage
 */
private class InputState
{
boolean[] state = new boolean[VehicleInputKey.values().length];
boolean[] pressed = new boolean[VehicleInputKey.values().length];
}

/**
 * snapshot of input state at any given time
 * @author Shadowmage
 *
 */
private class InputSnapshot
{
boolean[] state = new boolean[VehicleInputKey.values().length];
boolean[] pressed = new boolean[VehicleInputKey.values().length];
}

}
