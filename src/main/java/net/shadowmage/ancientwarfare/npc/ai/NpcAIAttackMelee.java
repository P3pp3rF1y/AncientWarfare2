package net.shadowmage.ancientwarfare.npc.ai;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.npc.entity.NpcBase;

public class NpcAIAttackMelee extends NpcAI
{

World worldObj;

/**
 * An amount of decrementing ticks that allows the entity to attack once the tick reaches 0.
 */
int attackTick;

/** The speed with which the mob will approach the target */
double speedTowardsTarget;

/**
 * When true, the mob will continue chasing its target, even if it can't find a path to them right now.
 */
boolean longMemory;

/** The PathEntity of our entity. */
PathEntity entityPathEntity;

private int recheckDelay;

private double targetPosX;
private double targetPosY;
private double targetPosZ;

private int failedPathFindingPenalty;

public NpcAIAttackMelee(NpcBase npc)
  {
  super(npc);
  }

public NpcAIAttackMelee(NpcBase npc, double par2, boolean par4)
  {
  super(npc);
  this.worldObj = npc.worldObj;
  this.speedTowardsTarget = par2;
  this.longMemory = par4;
  this.setMutexBits(3);
  }

@Override
public boolean shouldExecute()
  {
  EntityLivingBase entitylivingbase = this.npc.getAttackTarget();
  if (entitylivingbase == null)
    {
    return false;
    }
  else if (!entitylivingbase.isEntityAlive())
    {
    return false;
    }
  else
    {
    if (-- this.recheckDelay <= 0)
      {
      this.entityPathEntity = this.npc.getNavigator().getPathToEntityLiving(entitylivingbase);
      this.recheckDelay = 4 + this.npc.getRNG().nextInt(7);
      return this.entityPathEntity != null;
      }
    else
      {
      return true;
      }
    }
  }

/**
 * Returns whether an in-progress EntityAIBase should continue executing
 */
@Override
public boolean continueExecuting()
  {
  EntityLivingBase entitylivingbase = this.npc.getAttackTarget();
  return entitylivingbase == null ? false : (!entitylivingbase.isEntityAlive() ? false : (!this.longMemory ? !this.npc.getNavigator().noPath() : this.npc.isWithinHomeDistance(MathHelper.floor_double(entitylivingbase.posX), MathHelper.floor_double(entitylivingbase.posY), MathHelper.floor_double(entitylivingbase.posZ))));
  }

/**
 * Execute a one shot task or start executing a continuous task
 */
@Override
public void startExecuting()
  {
  npc.addAITask(TASK_ATTACK);
  this.npc.getNavigator().setPath(this.entityPathEntity, this.speedTowardsTarget);
  this.recheckDelay = 0;
  }

/**
 * Resets the task
 */
@Override
public void resetTask()
  {
  npc.removeAITask(TASK_ATTACK);
  this.npc.getNavigator().clearPathEntity();
  }

/**
 * Updates the task
 */
@Override
public void updateTask()
  {
  EntityLivingBase entitylivingbase = this.npc.getAttackTarget();
  this.npc.getLookHelper().setLookPositionWithEntity(entitylivingbase, 30.0F, 30.0F);
  double distanceToEntity = this.npc.getDistanceSq(entitylivingbase.posX, entitylivingbase.boundingBox.minY, entitylivingbase.posZ);
  double attackDistance = (double)(this.npc.width * 2.0F * this.npc.width * 2.0F + entitylivingbase.width);
  --this.recheckDelay;

  if ((this.longMemory || this.npc.getEntitySenses().canSee(entitylivingbase)) && this.recheckDelay <= 0 && (this.targetPosX == 0.0D && this.targetPosY == 0.0D && this.targetPosZ == 0.0D || entitylivingbase.getDistanceSq(this.targetPosX, this.targetPosY, this.targetPosZ) >= 1.0D || this.npc.getRNG().nextFloat() < 0.05F))
    {
    this.targetPosX = entitylivingbase.posX;
    this.targetPosY = entitylivingbase.boundingBox.minY;
    this.targetPosZ = entitylivingbase.posZ;
    this.recheckDelay = failedPathFindingPenalty + 4 + this.npc.getRNG().nextInt(7);

    if (this.npc.getNavigator().getPath() != null)
      {
      PathPoint finalPathPoint = this.npc.getNavigator().getPath().getFinalPathPoint();
      if (finalPathPoint != null && entitylivingbase.getDistanceSq(finalPathPoint.xCoord, finalPathPoint.yCoord, finalPathPoint.zCoord) < 1)
        {
        failedPathFindingPenalty = 0;
        }
      else
        {
        failedPathFindingPenalty += 10;
        }
      }
    else
      {
      failedPathFindingPenalty += 10;
      }

    if (distanceToEntity > 1024.0D)
      {
      this.recheckDelay += 10;
      }
    else if (distanceToEntity > 256.0D)
      {
      this.recheckDelay += 5;
      }

    if (!this.npc.getNavigator().tryMoveToEntityLiving(entitylivingbase, this.speedTowardsTarget))
      {
      this.recheckDelay += 15;
      }
    }

  this.attackTick = Math.max(this.attackTick - 1, 0);

  if (distanceToEntity <= attackDistance && this.attackTick <= 20)
    {
    this.attackTick = 20;

    if (this.npc.getHeldItem() != null)
      {
      this.npc.swingItem();
      }

    this.npc.attackEntityAsMob(entitylivingbase);
    }
  }

}
