package net.shadowmage.ancientwarfare.vehicle.entity.movement;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.shadowmage.ancientwarfare.core.config.AWLog;
import net.shadowmage.ancientwarfare.core.util.RayTraceUtils;
import net.shadowmage.ancientwarfare.vehicle.entity.VehicleBase;
import net.shadowmage.ancientwarfare.vehicle.input.VehicleInputKey;

public class VehicleMoveHandlerAirshipTest extends VehicleMoveHandler
{

public VehicleMoveHandlerAirshipTest(VehicleBase vehicle)
  {
  super(vehicle);
  }

@Override
public void updateVehicleMotion(boolean[] inputStates)
  {
  float rotation = 0;
  double forward = 0;
  double ascent = 0;
  if(inputStates[VehicleInputKey.FORWARD.ordinal()]){forward+=0.25d;}
  if(inputStates[VehicleInputKey.REVERSE.ordinal()]){forward-=0.25d;}
  if(inputStates[VehicleInputKey.LEFT.ordinal()]){rotation+=1.f;}
  if(inputStates[VehicleInputKey.RIGHT.ordinal()]){rotation-=1.f;}
  if(inputStates[VehicleInputKey.ASCEND.ordinal()]){ascent+=0.25d;}
  if(inputStates[VehicleInputKey.DESCEND.ordinal()]){ascent-=0.25d;}
  /**
   * first move the vehicle forward along its current move vector
   */
  Vec3 forwardAxis = vehicle.getLookVec();
  double mx = forwardAxis.xCoord * forward;
  double mz = forwardAxis.zCoord * forward;
  double my = ascent;
  vehicle.moveEntity(mx, my, mz);
  /**
   * then rotate the vehicle towards its new orientation
   */
  if(rotation!=0)
    {
    vehicle.rotateEntity(rotation);    
    }
  if(!vehicle.worldObj.isRemote && inputStates[VehicleInputKey.FIRE.ordinal()])
    {
    MovingObjectPosition pos = RayTraceUtils.getPlayerTarget((EntityPlayer) vehicle.riddenByEntity, 140, 1);
    if(pos!=null && pos.hitVec!=null)
      {
      AWLog.logDebug("fire pressed...target: "+pos+" :: "+pos.hitVec);
      vehicle.onFirePressedPilot(pos.hitVec);     
      }
    }
  }
}
