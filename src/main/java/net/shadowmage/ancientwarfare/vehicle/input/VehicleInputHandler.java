package net.shadowmage.ancientwarfare.vehicle.input;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.shadowmage.ancientwarfare.core.AncientWarfareCore;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.vehicle.entity.VehicleBase;
import net.shadowmage.ancientwarfare.vehicle.network.PacketInputReply;
import net.shadowmage.ancientwarfare.vehicle.network.PacketInputState;

public class VehicleInputHandler
{

private VehicleBase vehicle;
private boolean hadRider;

/**
 * Used client side to track input states<br>
 * Used server side as an empty input array for dummy movement processing
 */
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
List<PacketInputReply> replyPacketsToProcess = new ArrayList<PacketInputReply>();

public VehicleInputHandler(VehicleBase vehicle)
  {
  this.vehicle = vehicle;  
  }

public void onKeyChanged(VehicleInputKey key, boolean newState)
  {
  inputState.state[key.ordinal()]=newState;
  if(newState){inputState.pressed[key.ordinal()]=true;}
  }

public void handleVanillaSynch(double x, double y, double z, float yaw, float pitch, int ticks)
  {
  this.destX = x;
  this.destY = y;
  this.destZ = z; 
  this.destYaw = yaw;
  this.destPitch = pitch;
  this.lerpTicks = ticks * 2;
//  if(vehicle.riddenByEntity != AncientWarfareCore.proxy.getClientPlayer())
//    {
//    AWLog.logDebug("handling vanilla synch: "+x+","+y+","+z);    
//    }
  }

public void handleInputPacket(PacketInputState state)
  {
  packetsToProcess.add(state);
  }

public void handleReplyPacket(PacketInputReply reply)
  {
  replyPacketsToProcess.add(reply);
  }

private void processReplyPackets()
  {
  PacketInputReply reply;
  Iterator<PacketInputReply> it1 = replyPacketsToProcess.iterator();
  while(it1.hasNext() && (reply=it1.next())!=null)
    {
    it1.remove();
    int id = reply.commandID;
//    AWLog.logDebug("Client processing reply packet: "+id);
    
    vehicle.setPositionAndRotation(reply.x, reply.y, reply.z, reply.yaw, reply.pitch);
    
    Iterator<InputSnapshot> it = snapshotBuffer.iterator();
    InputSnapshot st = null;
    while(it.hasNext() && (st=it.next())!=null)
      {
      if(st.commandId<=id)//already processed on server, no longer interested
        {
        it.remove();
        continue;
        }
      vehicle.moveHandler.updateVehicleMotion(st.pressed);
      }
    }
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
  if(!packetsToProcess.isEmpty())//always process input packets, regardless of has rider or rider type
    {
    PacketInputState state;
    while(!packetsToProcess.isEmpty())
      {    
      state = packetsToProcess.remove(0);
      if(state.dummy)
        {
        vehicle.moveHandler.updateVehicleMotion(inputState.pressed);    
        continue;
        }      
//      AWLog.logDebug("Server processing Command id: "+state.commandID + "    ------------------");
//      AWLog.logDebug("server pos: "+vehicle.posX+","+vehicle.posY+","+vehicle.posZ);
      vehicle.moveHandler.updateVehicleMotion(state.keyStates);    
//      AWLog.logDebug("post   pos: "+vehicle.posX+","+vehicle.posY+","+vehicle.posZ);
//      AWLog.logDebug("pos from client: "+state.x+","+state.y+","+state.z);
//      AWLog.logDebug("End Server processing Command id: "+state.commandID + "------------------");
      if(vehicle.posX != state.x || vehicle.posY!=state.y || vehicle.posZ!=state.z || vehicle.rotationYaw!=state.yaw || vehicle.rotationPitch!=state.pitch)
        {
        double x = vehicle.posX - state.x;
        double y = vehicle.posY - state.y;
        double z = vehicle.posZ - state.z;
        float yaw = vehicle.rotationYaw - state.yaw;
        float pitch = vehicle.rotationPitch - state.pitch;
        if(Math.abs(x)>0.025 || Math.abs(y)>0.025 || Math.abs(z)>0.025 || Math.abs(yaw) > 0.1 || Math.abs(pitch) > 0.1)
          {        
//          AWLog.logDebug("Sending force pos packet");           
          PacketInputReply reply = new PacketInputReply();
          reply.setID(vehicle, state.commandID);
          reply.setPosition(vehicle.posX, vehicle.posY, vehicle.posZ, vehicle.rotationYaw, vehicle.rotationPitch);
          NetworkHandler.sendToPlayer((EntityPlayerMP)vehicle.riddenByEntity, reply);              
          }
        }
      }
    }
  else if(!(vehicle.riddenByEntity instanceof EntityPlayer))//has no packets && rider is not a player providing input packets from client
    {
    vehicle.moveHandler.updateVehicleMotion(inputState.pressed);//inputState should be a freshly initialized array (filled with false) on server
    }    
  }

private void clientUpdate()
  {
  if(vehicle.riddenByEntity!=null)
    {
    if(!hadRider)
      {
      clearInputState();
      }
    hadRider=true;
    }
  else if(hadRider)
    {
    hadRider=false;
    clearInputState();
    }
  
  if(vehicle.riddenByEntity==AncientWarfareCore.proxy.getClientPlayer())
    {
    processReplyPackets();
    collectInput();
    updateMotionClient();
    clearInputCache();   
    }
  else
    {
    lerpMotion();
    }
  }

/**
 * params are the original pre-input vehicle positions/rotations
 */
private void sendInputPacket()
  {  
  int len = inputState.pressed.length;
  boolean dummy = true;
  for(int  i = 0; i < len; i++)
    {
    if(inputState.pressed[i])
      {
      dummy=false;
      break;
      }
    }
  PacketInputState pkt = new PacketInputState();
  pkt.setID(vehicle, commandID);  
  pkt.setDummy(dummy);
  pkt.setInputStates(inputState.pressed);  
  pkt.setPosition(vehicle.posX, vehicle.posY, vehicle.posZ, vehicle.rotationYaw, vehicle.rotationPitch);
  NetworkHandler.sendToServer(pkt); 
  writeToBuffer();
  incrementBufferIndex(); 
  }

private void updateMotionClient()
  {
  destX = vehicle.posX;
  destY = vehicle.posY;
  destZ = vehicle.posZ;
  destYaw = vehicle.rotationYaw;
  destPitch = vehicle.rotationPitch;
  lerpTicks = 1;
  
  //have to send packet every tick or server-side vehicle will no update at all...
  //perhaps make an empty input packet that is processed for an empty-command pass?
//  AWLog.logDebug("Client sending commandID: "+commandID + " --------------------------");
//  AWLog.logDebug("Pre  pos: "+vehicle.posX+","+vehicle.posY+","+vehicle.posZ);
  vehicle.moveHandler.updateVehicleMotion(inputState.pressed);//pass the pressed state array into vehicles motion handler (type depends upon vehicle)
//  AWLog.logDebug("Sent pos: "+vehicle.posX+","+vehicle.posY+","+vehicle.posZ);
//  AWLog.logDebug("End sending commandID: "+commandID + "    --------------------------");
  sendInputPacket();//send input to server
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

private void clearInputState()
  {
  for(int i = 0; i < inputState.pressed.length; i++)
    {
    inputState.pressed[i] = false;
    inputState.state[i] = false;
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
    
    /**
     * lerp x,y,z motion
     */
    vehicle.motionX = dx/t;
    vehicle.motionY = dy/t;
    vehicle.motionZ = dz/t;
    vehicle.moveEntity(vehicle.motionX, vehicle.motionY, vehicle.motionZ);
    
    /**
     * obtain and normalize yaw
     */    
    float dyaw = destYaw - vehicle.rotationYaw;
    while(dyaw < -180.f)
      {
      dyaw += 360.f;
      vehicle.rotationYaw += 360.f;
      }
    while(dyaw >= 180.f)
      {
      dyaw -= 360.f;
      vehicle.rotationYaw -= 360.f;
      }      
    /**
     * lerp yaw
     */
    vehicle.prevRotationYaw = vehicle.rotationYaw;
    vehicle.rotationYaw += dyaw / (float)t;

    //TODO normalize pitch to -180<->180 range as with yaw
    float dpitch = destPitch - vehicle.rotationPitch;
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
