package net.shadowmage.ancientwarfare.npc.ai;

import java.util.List;

import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.util.Vec3;
import net.shadowmage.ancientwarfare.npc.AncientWarfareNPC;
import net.shadowmage.ancientwarfare.npc.entity.NpcBase;

public class NpcAIFleeHostiles extends NpcAI
{

IEntitySelector selector;
double distanceFromEntity = 16;
EntityLiving fleeTarget;
PathEntity path;

public NpcAIFleeHostiles(NpcBase npc)
  {
  super(npc);
  selector = new IEntitySelector()
    {
    @Override
    public boolean isEntityApplicable(Entity var1)
      {
      if(var1 instanceof NpcBase)
        {
        return ((NpcBase)var1).isHostileTowards(NpcAIFleeHostiles.this.npc);
        }
      return AncientWarfareNPC.statics.shouldEntityTargetNpcs(EntityList.getEntityString(var1));
      }
    };
  this.setMutexBits(ATTACK+MOVE);
  }

@SuppressWarnings("rawtypes")
@Override
public boolean shouldExecute()
  {
  if(!npc.getIsAIEnabled()){return false;}
  List list = this.npc.worldObj.selectEntitiesWithinAABB(EntityLiving.class, this.npc.boundingBox.expand((double)this.distanceFromEntity, 3.0D, (double)this.distanceFromEntity), this.selector);
  if (list.isEmpty())
    {
    return false;
    }
  this.fleeTarget = (EntityLiving)list.get(0);
  //TODO find closest target to flee from?
  
  Vec3 vec3 = RandomPositionGenerator.findRandomTargetBlockAwayFrom(this.npc, 16, 7, Vec3.createVectorHelper(this.fleeTarget.posX, this.fleeTarget.posY, this.fleeTarget.posZ));

  if (vec3 == null)
    {
    return false;//did not find random flee-towards target
    }
  else if (this.fleeTarget.getDistanceSq(vec3.xCoord, vec3.yCoord, vec3.zCoord) < this.fleeTarget.getDistanceSqToEntity(this.npc))
    {
    return false;//do not flee towards a position that is closer to the flee-target than you are
    }
  else
    {
    path = npc.getNavigator().getPathToXYZ(vec3.xCoord, vec3.yCoord, vec3.zCoord);
    if(path!=null)
      {
      return true;
      }
    return false;
    }
  }

/**
 * Returns whether an in-progress EntityAIBase should continue executing
 */
@Override
public boolean continueExecuting()
  {
  if(!npc.getIsAIEnabled()){return false;}
  return this.fleeTarget!=null && !this.fleeTarget.isDead && this.fleeTarget.getDistanceSqToEntity(this.npc) < distanceFromEntity*distanceFromEntity && npc.getAttackTarget()==this.fleeTarget && !this.npc.getNavigator().noPath();
  }

@Override
public void startExecuting()
  {
  npc.getNavigator().setPath(path, 1.d);
  npc.setTarget(fleeTarget);
  npc.setAttackTarget(fleeTarget);
  }

@Override
public void resetTask()
  {
  fleeTarget = null;
  path = null;
  npc.getNavigator().clearPathEntity();
  npc.setAttackTarget(null);
  npc.setTarget(null);
  }

}
