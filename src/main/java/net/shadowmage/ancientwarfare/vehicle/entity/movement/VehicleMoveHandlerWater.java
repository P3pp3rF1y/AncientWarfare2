package net.shadowmage.ancientwarfare.vehicle.entity.movement;

import net.minecraft.block.material.Material;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.shadowmage.ancientwarfare.core.config.AWLog;
import net.shadowmage.ancientwarfare.vehicle.entity.VehicleBase;
import net.shadowmage.ancientwarfare.vehicle.input.VehicleInputKey;

public class VehicleMoveHandlerWater extends VehicleMoveHandler
{

public VehicleMoveHandlerWater(VehicleBase vehicle)
  {
  super(vehicle);
  // TODO Auto-generated constructor stub
  }

@Override
public void updateVehicleMotion(boolean[] inputStates)
  {
  float rotation = 0;
  double forward = 0;
  if(inputStates[VehicleInputKey.FORWARD.ordinal()]){forward+=0.05d;}
  if(inputStates[VehicleInputKey.REVERSE.ordinal()]){forward-=0.05d;}
  if(inputStates[VehicleInputKey.LEFT.ordinal()]){rotation+=1.f;}
  if(inputStates[VehicleInputKey.RIGHT.ordinal()]){rotation-=1.f;}  
  /**
   * first move the vehicle forward along its current move vector
   */
  Vec3 forwardAxis = vehicle.getLookVec();
  double mx = forwardAxis.xCoord * forward;
  double mz = forwardAxis.zCoord * forward;
  double my = 0;

  //check in-water depth, for brevity sake, only check the block(s) under the center of the entity and the obb corners (5 points)
  //the rest of the entity should be in either water or air due to the other collision handling
  //for test case, aim for 50% submerged
  int height = MathHelper.ceiling_double_int(vehicle.vehicleHeight);//height to check, in blocks
  float submerged = 0;
  
  int y = MathHelper.floor_double(vehicle.posY);  

  int x = MathHelper.floor_double(vehicle.orientedBoundingBox.getCorner(0).xCoord);
  int z = MathHelper.floor_double(vehicle.orientedBoundingBox.getCorner(0).zCoord);
  for(int by = y; by<= y+height; by++)
    {
    if(vehicle.worldObj.getBlock(x, by, z).getMaterial()==Material.water)
      {
      submerged += (float)(16 - vehicle.worldObj.getBlockMetadata(x, by, z)) * 0.0625f;
      }
    }
  
  x = MathHelper.floor_double(vehicle.orientedBoundingBox.getCorner(1).xCoord);
  z = MathHelper.floor_double(vehicle.orientedBoundingBox.getCorner(1).zCoord);
  for(int by = y; by<= y+height; by++)
    {
    if(vehicle.worldObj.getBlock(x, by, z).getMaterial()==Material.water)
      {
      submerged += (float)(16 - vehicle.worldObj.getBlockMetadata(x, by, z)) * 0.0625f;
      }
    }
  
  x = MathHelper.floor_double(vehicle.orientedBoundingBox.getCorner(2).xCoord);
  z = MathHelper.floor_double(vehicle.orientedBoundingBox.getCorner(2).zCoord);
  for(int by = y; by<= y+height; by++)
    {
    if(vehicle.worldObj.getBlock(x, by, z).getMaterial()==Material.water)
      {
      submerged += (float)(16 - vehicle.worldObj.getBlockMetadata(x, by, z)) * 0.0625f;
      }
    }
  
  x = MathHelper.floor_double(vehicle.orientedBoundingBox.getCorner(3).xCoord);
  z = MathHelper.floor_double(vehicle.orientedBoundingBox.getCorner(3).zCoord);
  for(int by = y; by<= y+height; by++)
    {
    if(vehicle.worldObj.getBlock(x, by, z).getMaterial()==Material.water)
      {
      submerged += (float)(16 - vehicle.worldObj.getBlockMetadata(x, by, z)) * 0.0625f;
      }
    }
  
  x = MathHelper.floor_double(vehicle.posX);
  z = MathHelper.floor_double(vehicle.posZ);
  for(int by = y; by<= y+height; by++)
    {
    if(vehicle.worldObj.getBlock(x, by, z).getMaterial()==Material.water)
      {
      submerged += (float)(16 - vehicle.worldObj.getBlockMetadata(x, by, z)) * 0.0625f;
      }
    }
  
  submerged *= 0.2f; //e.g. x/5.f
  AWLog.logDebug("submerged: "+submerged);    
  if(submerged < (float)height / 2.f)
    {
    mx*=0.5d;
    mz*=0.5d;
    float target = height / 2.f;
    target -= submerged;
    my = -target;
    if(my <- 0.5d) {my=-0.5d;}
    AWLog.logDebug("my: "+my);
    //
    
    }
  vehicle.moveEntity(mx, my, mz);
  /**
   * then rotate the vehicle towards its new orientation
   */
  if(rotation!=0)
    {
    vehicle.rotateEntity(rotation);    
    }
  }

}
