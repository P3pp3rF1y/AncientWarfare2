package net.shadowmage.ancientwarfare.npc.ai;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.npc.entity.NpcBase;

public class NpcAIFollowPlayer extends NpcAI
{
private EntityLivingBase owner;
World theWorld;
private double moveSpeed;
private PathNavigate npcPathfinder;
private int executionTimer;
float maxDist;
float minDist;
private boolean avoidWaterCache;

public NpcAIFollowPlayer(NpcBase npc, double par2, float par4, float par5)
  {
  super(npc);
  this.theWorld = npc.worldObj;
  this.moveSpeed = par2;
  this.npcPathfinder = npc.getNavigator();
  this.minDist = par4;
  this.maxDist = par5;
  this.setMutexBits(MOVE+ATTACK);
  }

/**
 * Returns whether the EntityAIBase should begin execution.
 */
public boolean shouldExecute()
  {
  EntityLivingBase owner = this.npc.getFollowingEntity();  
  if(owner == null)
    {
    return false;
    }
  else if (this.npc.getDistanceSqToEntity(owner) < (double)(this.minDist * this.minDist))
    {
    return false;
    }
  else
    {
    this.owner = owner;
    return true;
    }
  }

/**
 * Returns whether an in-progress EntityAIBase should continue executing
 */
public boolean continueExecuting()
  {
  return !this.npcPathfinder.noPath() && this.npc.getDistanceSqToEntity(this.owner) > (double)(this.maxDist * this.maxDist);
  }

/**
 * Execute a one shot task or start executing a continuous task
 */
public void startExecuting()
  {
  this.executionTimer = 0;
  this.avoidWaterCache = this.npc.getNavigator().getAvoidsWater();
  this.npc.getNavigator().setAvoidsWater(false);
  }

/**
 * Resets the task
 */
public void resetTask()
  {
  this.owner = null;
  this.npcPathfinder.clearPathEntity();
  this.npc.getNavigator().setAvoidsWater(this.avoidWaterCache);
  }

/**
 * Updates the task
 */
public void updateTask()
  {
  this.npc.getLookHelper().setLookPositionWithEntity(this.owner, 10.0F, (float)this.npc.getVerticalFaceSpeed());

  if (--this.executionTimer <= 0)
    {
    this.executionTimer = 10;

    if (!this.npcPathfinder.tryMoveToEntityLiving(this.owner, this.moveSpeed))
      {
      if (this.npc.getDistanceSqToEntity(this.owner) >= 144.0D)
        {
        int i = MathHelper.floor_double(this.owner.posX) - 2;
        int j = MathHelper.floor_double(this.owner.posZ) - 2;
        int k = MathHelper.floor_double(this.owner.boundingBox.minY);

        for (int l = 0; l <= 4; ++l)
          {
          for (int i1 = 0; i1 <= 4; ++i1)
            {
            if ((l < 1 || i1 < 1 || l > 3 || i1 > 3) && World.doesBlockHaveSolidTopSurface(this.theWorld, i + l, k - 1, j + i1) && !this.theWorld.getBlock(i + l, k, j + i1).isNormalCube() && !this.theWorld.getBlock(i + l, k + 1, j + i1).isNormalCube())
              {
              this.npc.setLocationAndAngles((double)((float)(i + l) + 0.5F), (double)k, (double)((float)(j + i1) + 0.5F), this.npc.rotationYaw, this.npc.rotationPitch);
              this.npcPathfinder.clearPathEntity();
              return;
              }
            }
          }
        }
      }
    }
  }

}
