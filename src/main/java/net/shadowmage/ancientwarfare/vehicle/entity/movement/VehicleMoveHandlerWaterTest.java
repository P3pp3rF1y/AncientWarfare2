package net.shadowmage.ancientwarfare.vehicle.entity.movement;

import net.shadowmage.ancientwarfare.vehicle.entity.VehicleBase;

public class VehicleMoveHandlerWaterTest extends VehicleInputHandler {

    private float targetSubmergedDepth = 1.f;

    public VehicleMoveHandlerWaterTest(VehicleBase vehicle) {
        super(vehicle);
        // TODO Auto-generated constructor stub
    }

    @Override
    public void updateVehicleMotion(boolean[] inputStates) {
//  float rotation = 0;
//  double forward = 0;
//  if(inputStates[VehicleInputKey.FORWARD.ordinal()]){forward+=0.25d;}
//  if(inputStates[VehicleInputKey.REVERSE.ordinal()]){forward-=0.25d;}
//  if(inputStates[VehicleInputKey.LEFT.ordinal()]){rotation+=1.f;}
//  if(inputStates[VehicleInputKey.RIGHT.ordinal()]){rotation-=1.f;}  
//  /*
//   * first move the vehicle forward along its current move vector
//   */
//  Vec3d forwardAxis = vehicle.getLookVec();
//  double mx = forwardAxis.x * forward;
//  double mz = forwardAxis.z * forward;
//  double my = 0.d;
//
//  //check in-water depth, for brevity sake, only check the block(s) under the center of the entity and the obb corners (5 points)
//  //the rest of the entity should be in either water or air due to the other collision handling
//  //for test case, aim for 50% submerged
//  int height = MathHelper.ceiling_double_int(vehicle.vehicleHeight);//height to check, in blocks
//  float submerged = 0;
//  
//  int y = MathHelper.floor(vehicle.posY);
//
//  int x = MathHelper.floor(vehicle.orientedBoundingBox.getCorner(0).x);
//  int z = MathHelper.floor(vehicle.orientedBoundingBox.getCorner(0).z);
//  for(int by = y; by<= y+height; by++)
//    {
//    if(vehicle.world.getBlock(x, by, z).getMaterial()==Material.WATER)
//      {
//      submerged += (float)(16 - vehicle.world.getBlockMetadata(x, by, z)) * 0.0625f;
//      }
//    }
//  
//  x = MathHelper.floor(vehicle.orientedBoundingBox.getCorner(1).x);
//  z = MathHelper.floor(vehicle.orientedBoundingBox.getCorner(1).z);
//  for(int by = y; by<= y+height; by++)
//    {
//    if(vehicle.world.getBlock(x, by, z).getMaterial()==Material.WATER)
//      {
//      submerged += (float)(16 - vehicle.world.getBlockMetadata(x, by, z)) * 0.0625f;
//      }
//    }
//  
//  x = MathHelper.floor(vehicle.orientedBoundingBox.getCorner(2).x);
//  z = MathHelper.floor(vehicle.orientedBoundingBox.getCorner(2).z);
//  for(int by = y; by<= y+height; by++)
//    {
//    if(vehicle.world.getBlock(x, by, z).getMaterial()==Material.WATER)
//      {
//      submerged += (float)(16 - vehicle.world.getBlockMetadata(x, by, z)) * 0.0625f;
//      }
//    }
//  
//  x = MathHelper.floor(vehicle.orientedBoundingBox.getCorner(3).x);
//  z = MathHelper.floor(vehicle.orientedBoundingBox.getCorner(3).z);
//  for(int by = y; by<= y+height; by++)
//    {
//    if(vehicle.world.getBlock(x, by, z).getMaterial()==Material.WATER)
//      {
//      submerged += (float)(16 - vehicle.world.getBlockMetadata(x, by, z)) * 0.0625f;
//      }
//    }
//  
//  x = MathHelper.floor(vehicle.posX);
//  z = MathHelper.floor(vehicle.posZ);
//  for(int by = y; by<= y+height; by++)
//    {
//    if(vehicle.world.getBlock(x, by, z).getMaterial()==Material.WATER)
//      {
//      submerged += (float)(16 - vehicle.world.getBlockMetadata(x, by, z)) * 0.0625f;
//      }
//    }
//  
//  submerged *= 0.2f; //i.e. submerged/=5.f
//  AWLog.logDebug("submerged: "+submerged);   
//    
//  if(submerged==0)
//    {
//    mx*=0.25d;
//    mz*=0.25d;    
//    }
//  if(submerged < targetSubmergedDepth)
//    {
//    mx*=0.85d;
//    mz*=0.85d;
//    float move = submerged - targetSubmergedDepth;
//    my = move;
//    if(my < -0.5d){my=-0.5d;}//TODO gravity sim stuff... 
//    }
//  else if(submerged > targetSubmergedDepth)
//    {
//    my = submerged - targetSubmergedDepth;
//    if(my > 0.15d){my=0.15d;}//TODO gravity sim stuff... 
//    }
//  AWLog.logDebug("my: "+my);   
//  
//  vehicle.moveEntity(mx, my, mz);
//  /*
//   * then rotate the vehicle towards its new orientation
//   */
//  if(rotation!=0)
//    {
//    vehicle.moveHelper.rotateVehicle(rotation);     
//    }
    }

}
