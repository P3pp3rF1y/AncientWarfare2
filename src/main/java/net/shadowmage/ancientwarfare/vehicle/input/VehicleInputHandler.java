package net.shadowmage.ancientwarfare.vehicle.input;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.minecraft.entity.player.EntityPlayerMP;
import net.shadowmage.ancientwarfare.core.AncientWarfareCore;
import net.shadowmage.ancientwarfare.core.config.AWLog;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.vehicle.entity.VehicleBase;
import net.shadowmage.ancientwarfare.vehicle.network.PacketInputReply;
import net.shadowmage.ancientwarfare.vehicle.network.PacketInputState;

public class VehicleInputHandler
{

private VehicleBase vehicle;
private InputState inputState = new InputState();

List<InputSnapshot> snapshotBuffer = new ArrayList<InputSnapshot>();

/**
 * server position and rotation values and length of update interval (lerpTicks)
 */
double destX, destY, destZ;
float destYaw, destPitch;
int lerpTicks;

int commandID = 0;
List<PacketInputState> packetsToProcess = new ArrayList<PacketInputState>();

public VehicleInputHandler(VehicleBase vehicle)
  {
  this.vehicle = vehicle;  
  }

public void onKeyChanged(VehicleInputKey key, boolean newState)
  {
  inputState.state[key.ordinal()]=newState;
  if(newState){inputState.pressed[key.ordinal()]=true;}
  AWLog.logDebug("rec input state for vehicle: "+key+" :: "+newState);
  }

public void handleVanillaSynch(double x, double y, double z, float yaw, float pitch, int ticks)
  {
  this.destX = x;
  this.destY = y;
  this.destZ = z; 
  this.destYaw = yaw;
  this.destPitch = pitch;
  this.lerpTicks = ticks + 2;
  }

public void handleInputPacket(PacketInputState state)
  {
  packetsToProcess.add(state);
  }

public void handleReplyPacket(PacketInputReply reply)
  {
  AWLog.logDebug("rec reply packet!");
  int id = reply.commandID;
  vehicle.setPositionAndRotation(reply.x, reply.y, reply.z, reply.yaw, reply.pitch);
  Iterator<InputSnapshot> it = snapshotBuffer.iterator();
  InputSnapshot st = null;
  while(it.hasNext() && (st=it.next())!=null)
    {
    if(st.commandId<=id)
      {
      it.remove();
      continue;
      }
    AWLog.logDebug("replaying command: "+st.commandId);
    vehicle.moveHandler.updateVehicleMotion(st.pressed);
    }
  }

/**
 * params are the original pre-input vehicle positions/rotations
 */
private void sendInputPacket()
  {
  PacketInputState pkt = new PacketInputState();
  pkt.setID(vehicle, commandID);
  pkt.setInputStates(inputState.pressed);
  pkt.setPosition(vehicle.posX, vehicle.posY, vehicle.posZ, vehicle.rotationYaw, vehicle.rotationPitch);
  NetworkHandler.sendToServer(pkt);  
  
  destX = vehicle.posX;
  destY = vehicle.posY;
  destZ = vehicle.posZ;
  destYaw = vehicle.rotationYaw;
  destPitch = vehicle.rotationPitch;
  lerpTicks = 1;
  }

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
  PacketInputState state;
  while(!packetsToProcess.isEmpty() && vehicle.riddenByEntity!=null)
    {    
    AWLog.logDebug("processing input command on server");
    state = packetsToProcess.remove(0);    
    vehicle.moveHandler.updateVehicleMotion(state.keyStates);    
    AWLog.logDebug(String.format("server move for command: %s. moved position: %.5f, %.5f, %.5f", state.commandID, vehicle.posX, vehicle.posY, vehicle.posZ));
    if(vehicle.posX != state.x || vehicle.posY!=state.y || vehicle.posZ!=state.z || vehicle.rotationYaw!=state.yaw || vehicle.rotationPitch!=state.pitch)
      {
      AWLog.logDebug("sending server correction reply");
      PacketInputReply reply = new PacketInputReply();
      reply.setID(vehicle, state.commandID);
      reply.setPosition(vehicle.posX, vehicle.posY, vehicle.posZ, vehicle.rotationYaw, vehicle.rotationPitch);
      NetworkHandler.sendToPlayer((EntityPlayerMP)vehicle.riddenByEntity, reply);      
      }
    }
  }

private void clientUpdate()
  {
  if(vehicle.riddenByEntity==AncientWarfareCore.proxy.getClientPlayer())
    {
    collectInput();
    vehicle.moveHandler.updateVehicleMotion(inputState.pressed);
    AWLog.logDebug(String.format("client move for command: %s. moved position: %.5f, %.5f, %.5f", commandID, vehicle.posX, vehicle.posY, vehicle.posZ));
    sendInputPacket();
    clearInputCache();   
    writeToBuffer();
    clientLerp();
    incrementBufferIndex();
    }
  else//vanilla motion synch, merely re-interpret and lerp
    {
    lerpMotion();
    }
  }

/**
 * lerp a controlled client for server-side corrections
 */
private void clientLerp()
  {
  
  }

private void writeToBuffer()
  {
  InputSnapshot shot = new InputSnapshot();
  for(int i = 0; i < inputState.pressed.length; i++){shot.pressed[i]=inputState.pressed[i];}
  shot.commandId = commandID;
  snapshotBuffer.add(shot);
  }

private void incrementBufferIndex()
  {
  commandID++;
  //TODO how to roll this back?
  }

private void clearInputCache()
  {
  for(int i = 0; i < inputState.pressed.length; i++)
    {
    inputState.pressed[i] = false;
    }  
  }

private void collectInput()
  {
  for(int i = 0; i < inputState.state.length; i++)
    {
    if(inputState.state[i]){inputState.pressed[i]=true;}
    }
  }

/**
 * motion update for client-side entities -not- ridden by that clients' player
 */
private void lerpMotion()
  {

  if(lerpTicks>0)
    {
    /**
     * floating point value of ticks, for easier floating-point division
     */
    double t = (double)lerpTicks;
    /**
     * determine deltas for lerping
     */
    double dx = destX - vehicle.posX;
    double dy = destY - vehicle.posY;
    double dz = destZ - vehicle.posZ;
    float dyaw = destYaw - vehicle.rotationYaw;
    while(dyaw<=-180)
      {
      dyaw+=360.f;
      vehicle.rotationYaw+=360.f;
      vehicle.prevRotationYaw+=360.f;
      }
    while(dyaw>180)
      {
      dyaw-=360.f;
      vehicle.rotationYaw-=360.f;
      vehicle.prevRotationYaw-=360.f;
      }      
    float dpitch = destPitch - vehicle.rotationPitch;
    //TODO normalize pitch to -180<->180 range as with yaw
    
    /**
     * do the actual lerping.  Need to examine how to properly set the lerp of vehicle motion
     */
    vehicle.motionX = dx;
    vehicle.motionY = dy;
    vehicle.motionZ = dz;
    vehicle.prevRotationYaw = vehicle.rotationYaw;
    vehicle.rotationYaw += dyaw/(float)t;
    vehicle.prevRotationPitch = vehicle.rotationPitch;
    vehicle.rotationPitch += dpitch / (float)t;
    
    lerpTicks--;
    }
  else
    {
    /**
     * no lerp ticks, set to server-position
     */
    vehicle.posX = destX;
    vehicle.posY = destY;
    vehicle.posZ = destZ;
    vehicle.motionX = 0;
    vehicle.motionY = 0;
    vehicle.motionZ = 0;
    vehicle.prevRotationYaw = vehicle.rotationYaw;
    vehicle.rotationYaw = destYaw;
    vehicle.prevRotationPitch = vehicle.rotationPitch;
    vehicle.rotationPitch = destPitch;
    vehicle.setPositionAndRotation(destX, destY, destZ, destYaw, destPitch);
    }
  }

/**
 * current input state, used by movement calculation
 * @author Shadowmage
 */
public final class InputState
{
/**
 * the current -actual- state of the keys, set from input whenever a key is pressed and the client is riding a vehicle
 */
boolean[] state = new boolean[VehicleInputKey.values().length];

/**
 * the state of if a key was down for any portion of this/preciding tick<br>
 * these are the states that should be acted upon every tick for input handling and synched to the server
 */
boolean[] pressed = new boolean[VehicleInputKey.values().length];
}

/**
 * snapshot of input state at any given time
 * @author Shadowmage
 *
 */
private class InputSnapshot
{
int commandId;
boolean[] pressed = new boolean[VehicleInputKey.values().length];
}

}
