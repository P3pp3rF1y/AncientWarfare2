package net.shadowmage.ancientwarfare.core.entity;

import java.util.HashMap;

import net.minecraft.entity.Entity;
import net.minecraft.world.World;

public class AWEntityRegistry
{

public static final String NPC_TEST = "aw_npc_test";

private static HashMap<String, EntityDeclaration> entityRegistrations = new HashMap<String, EntityDeclaration>();

public static void registerEntity(EntityDeclaration reg)
  {
  entityRegistrations.put(reg.entityName, reg);
  cpw.mods.fml.common.registry.EntityRegistry.registerModEntity(reg.entityClass, reg.entityName, reg.id, reg.mod, reg.trackingRange, reg.updateFrequency, reg.sendsVelocityUpdates);
  }

public static Entity createEntity(String type, World world)
  {
  if(entityRegistrations.containsKey(type))
    {
    return entityRegistrations.get(type).createEntity(world);
    }
  return null;
  }

public static abstract class EntityDeclaration
{

Class<? extends Entity> entityClass;
String entityName;
int id;
Object mod;
int trackingRange;
int updateFrequency;
boolean sendsVelocityUpdates;

public EntityDeclaration(Class<? extends Entity> entityClass, String entityName, int id, Object mod, int trackingRange, int updateFrequency, boolean sendsVelocityUpdates)
  {
  this.entityClass = entityClass;
  this.entityName = entityName;
  this.id = id;
  this.mod = mod;
  this.trackingRange = trackingRange;
  this.updateFrequency = updateFrequency;
  this.sendsVelocityUpdates = sendsVelocityUpdates;
  }

public abstract Entity createEntity(World world);

}

}
