package net.shadowmage.ancientwarfare.npc.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.pathfinding.PathNavigate;

public class NpcNavHelper
{

NpcBase npc;
PathNavigate nav;

public NpcNavHelper(NpcBase npc)
  {
  this.npc = npc;
  this.nav = npc.getNavigator();
  }

public void tryMoveToEntityLiving(Entity e, double speed)
  {
  if(npc.ridingEntity instanceof EntityHorse)
    {
    EntityHorse horse = (EntityHorse)npc.ridingEntity;
    horse.getNavigator().tryMoveToEntityLiving(e, speed);//TODO adjust speed by horse-speed adjust factor?
    }
  else
    {
    npc.getNavigator().tryMoveToEntityLiving(e, speed);
    }
  }

public void tryMoveToXYZ(double x, double y, double z, double speed)
  {
  if(npc.ridingEntity instanceof EntityHorse)
    {
    EntityHorse horse = (EntityHorse)npc.ridingEntity;
    horse.getNavigator().tryMoveToXYZ(x, y, z, speed);
    }
  else
    {
    npc.getNavigator().tryMoveToXYZ(x, y, z, speed);
    }
  }

public void clearPath()
  {
  if(npc.ridingEntity instanceof EntityHorse)
    {
    EntityHorse horse = (EntityHorse)npc.ridingEntity;
    horse.getNavigator().clearPathEntity();
    }
  else
    {
    npc.getNavigator().clearPathEntity();
    }
  }

public PathEntity getPath()
  {
  if(npc.ridingEntity instanceof EntityHorse)
    {
    EntityHorse horse = (EntityHorse)npc.ridingEntity;
    return horse.getNavigator().getPath();
    }
  else
    {
    return npc.getNavigator().getPath();
    }
  }

}
