package net.shadowmage.ancientwarfare.npc.entity;

import net.minecraft.entity.ai.EntityAIAvoidEntity;
import net.minecraft.entity.ai.EntityAIMoveIndoors;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.scoreboard.Team;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.interfaces.IWorkSite.WorkType;
import net.shadowmage.ancientwarfare.core.interfaces.IWorker;

public class NpcWorker extends NpcPlayerOwned implements IWorker
{

public NpcWorker(World par1World)
  {
  super(par1World);  
  //this should be set to a generic 'flee' AI for civilians
  this.tasks.addTask(3, new EntityAIAvoidEntity(this, EntityZombie.class, 8.0F, 0.6D, 0.6D));

  this.tasks.addTask(6, new EntityAIMoveIndoors(this));
  }

@Override
public float getWorkEffectiveness()
  {
  return 0;//TODO base this off of worker level?
  }

@Override
public boolean canWorkAt(WorkType type)
  {
  return false;
  }

@Override
public Team getWorkerTeam()
  {
  return getTeam();
  }

}
