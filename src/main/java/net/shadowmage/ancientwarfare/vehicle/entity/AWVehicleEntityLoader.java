package net.shadowmage.ancientwarfare.vehicle.entity;

import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.entity.AWEntityRegistry;
import net.shadowmage.ancientwarfare.core.entity.AWEntityRegistry.EntityDeclaration;
import net.shadowmage.ancientwarfare.vehicle.AncientWarfareVehicles;

public class AWVehicleEntityLoader
{

private static int nextID = 0;

public static void load()
  {
  EntityDeclaration reg = new EntityDeclaration(VehicleBase.class, AWEntityRegistry.VEHICLE_TEST, nextID++, AncientWarfareVehicles.instance, 120, 3, true)
    {  
    @Override
    public Entity createEntity(World world)
      {
      return new VehicleBase(world);
      }
    };
  AWEntityRegistry.registerEntity(reg);
  }

}
