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
Entity fleeTarget;
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
  List list = this.npc.worldObj.selectEntitiesWithinAABB(EntityLiving.class, this.npc.boundingBox.expand((double)this.distanceFromEntity, 3.0D, (double)this.distanceFromEntity), this.selector);
  if (list.isEmpty())
    {
    return false;
    }
  this.fleeTarget = (Entity)list.get(0);
  
  Vec3 vec3 = RandomPositionGenerator.findRandomTargetBlockAwayFrom(this.npc, 16, 7, Vec3.createVectorHelper(this.fleeTarget.posX, this.fleeTarget.posY, this.fleeTarget.posZ));

  if (vec3 == null)
    {
    return false;
    }
  else if (this.fleeTarget.getDistanceSq(vec3.xCoord, vec3.yCoord, vec3.zCoord) < this.fleeTarget.getDistanceSqToEntity(this.npc))
    {
    return false;
    }
  else
    {
    PathEntity path = npc.getNavigator().getPathToXYZ(vec3.xCoord, vec3.yCoord, vec3.zCoord);
    if(path!=null){return true;}
    return false;
    }
  }

/**
 * Returns whether an in-progress EntityAIBase should continue executing
 */
@Override
public boolean continueExecuting()
  {
  return !this.npc.getNavigator().noPath();
  }

@Override
public void startExecuting()
  {
  if(path!=null)
    {
    npc.getNavigator().setPath(path, 1.d);    
    }
  else
    {
    npc.getNavigator().clearPathEntity();
    }
  }

@Override
public void resetTask()
  {
  fleeTarget = null;
  path = null;
  npc.getNavigator().clearPathEntity();
  }

}
