package net.shadowmage.ancientwarfare.npc.ai;

import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.Vec3;
import net.shadowmage.ancientwarfare.core.util.BlockPosition;
import net.shadowmage.ancientwarfare.npc.AncientWarfareNPC;
import net.shadowmage.ancientwarfare.npc.entity.NpcBase;

import java.util.Comparator;
import java.util.List;

public class NpcAIFleeHostiles extends NpcAI {

    private static int MAX_STAY_HOME = 400, MAX_FLEE_RANGE = 16, HEIGHT_CHECK = 7;
    private final IEntitySelector selector;
    private final Comparator sorter;
    double distanceFromEntity = 16;
    private Vec3 fleeVector;
    private int stayAtHomeTimer = 0;

    public NpcAIFleeHostiles(NpcBase npc) {
        super(npc);
        selector = new IEntitySelector() {
            @Override
            public boolean isEntityApplicable(Entity var1) {
                if(var1.isEntityAlive()) {
                    if (var1 instanceof NpcBase) {
                        return ((NpcBase) var1).isHostileTowards(NpcAIFleeHostiles.this.npc);
                    }
                    return AncientWarfareNPC.statics.shouldEntityTargetNpcs(EntityList.getEntityString(var1));
                }
                return false;
            }
        };
        sorter = new EntityAINearestAttackableTarget.Sorter(npc);
        this.setMutexBits(ATTACK + MOVE);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public boolean shouldExecute() {
        if (!npc.getIsAIEnabled()) {
            return false;
        }
        List list = this.npc.worldObj.selectEntitiesWithinAABB(EntityLiving.class, this.npc.boundingBox.expand(this.distanceFromEntity, 3.0D, this.distanceFromEntity), this.selector);
        if (list.isEmpty()) {
            return false;
        }
        list.sort(sorter);
        EntityLiving fleeTarget = (EntityLiving) list.get(0);
        //TODO check if can-see the target?
        boolean flee = false;
        if (npc.getTownHallPosition() != null || npc.hasHome()) {
            flee = true;
        } else {
            fleeVector = RandomPositionGenerator.findRandomTargetBlockAwayFrom(this.npc, MAX_FLEE_RANGE, HEIGHT_CHECK, Vec3.createVectorHelper(fleeTarget.posX, fleeTarget.posY, fleeTarget.posZ));
            if (fleeVector == null || fleeTarget.getDistanceSq(fleeVector.xCoord, fleeVector.yCoord, fleeVector.zCoord) < fleeTarget.getDistanceSqToEntity(this.npc)) {
                flee = false;//did not find random flee-towards target, perhaps retry next tick
            } else {
                flee = true;
            }
        }
        if (flee) {
            npc.setAttackTarget(fleeTarget);
        }
        return flee;
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    @Override
    public boolean continueExecuting() {
        if (!npc.getIsAIEnabled() || npc.getAttackTarget() == null || npc.getAttackTarget().isDead) {
            return false;
        }
        return stayAtHomeTimer != 0 || npc.getAttackTarget().getDistanceSqToEntity(this.npc) < distanceFromEntity * distanceFromEntity;
    }

    @Override
    public void updateTask() {
        if (npc.getAttackTarget() == null || npc.getAttackTarget().isDead) {
            npc.setAttackTarget(null);
            stayAtHomeTimer = 0;
            return;
        }
        BlockPosition pos = null;
        double distSq;
        if (npc.getTownHallPosition() != null) {
            pos = npc.getTownHallPosition();
            distSq = npc.getDistanceSq(pos);
        } else if (npc.hasHome()) {
            distSq = npc.getDistanceSqFromHome();
            ChunkCoordinates cc = npc.getHomePosition();
            pos = new BlockPosition(cc.posX, cc.posY, cc.posZ);
        } else//check distance to flee vector
        {
            distSq = npc.getDistanceSq(fleeVector.xCoord, fleeVector.yCoord, fleeVector.zCoord);
            if (distSq > 3 * 3) {
                moveToPosition(fleeVector.xCoord, fleeVector.yCoord, fleeVector.zCoord, distSq);
            } else {
                if (npc.getDistanceSqToEntity(npc.getAttackTarget()) < 16 * 16)//entity still chasing, find a new flee vector
                {
                    fleeVector = RandomPositionGenerator.findRandomTargetBlockAwayFrom(this.npc, MAX_FLEE_RANGE, HEIGHT_CHECK, Vec3.createVectorHelper(npc.getAttackTarget().posX, npc.getAttackTarget().posY, npc.getAttackTarget().posZ));
                    if (fleeVector == null) {
                        npc.setAttackTarget(null);//retry next tick..perhaps...
                    }
                } else//entity too far to care, stop running
                {
                    npc.setAttackTarget(null);
                }
            }
        }
        if (pos!=null && distSq > 3 * 3) {
            moveToPosition(pos, distSq);
            stayAtHomeTimer = MAX_STAY_HOME;
        }else {
            if (stayAtHomeTimer > 0) {
                stayAtHomeTimer--;
            }
        }
    }

    @Override
    public void resetTask() {
        fleeVector = null;
        npc.getNavigator().clearPathEntity();
        npc.setAttackTarget(null);
    }

}
