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
import net.shadowmage.ancientwarfare.npc.entity.NpcPlayerOwned;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class NpcAIFleeHostiles extends NpcAI<NpcPlayerOwned> {

    private static int MAX_STAY_AWAY = 50, MAX_FLEE_RANGE = 16, HEIGHT_CHECK = 7, PURSUE_RANGE = 16 * 16;
    private final IEntitySelector selector;
    private final Comparator sorter;
    double distanceFromEntity = 16;
    private Vec3 fleeVector;
    private int stayOutOfSightTimer = 0;
    private int fearLevel = 200; // fear makes NPC's wait/flee for progressively longer periods
    boolean homeCompromised = false;

    public NpcAIFleeHostiles(NpcPlayerOwned npc) {
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
        if (!npc.getIsAIEnabled())
            return false;
        // assume not fleeing, reduce fear level early
        if (fearLevel > 0)
            fearLevel--;
        
        boolean flee = false;
        EntityLiving fleeTarget = getClosestVisibleHostile();
        if (fleeTarget != null) {
            if (npc.getTownHallPosition() != null || npc.hasHome())
                flee = true;
            else {
                fleeVector = RandomPositionGenerator.findRandomTargetBlockAwayFrom(this.npc, MAX_FLEE_RANGE, HEIGHT_CHECK, Vec3.createVectorHelper(fleeTarget.posX, fleeTarget.posY, fleeTarget.posZ));
                if (fleeVector == null || fleeTarget.getDistanceSq(fleeVector.xCoord, fleeVector.yCoord, fleeVector.zCoord) < fleeTarget.getDistanceSqToEntity(this.npc))
                    flee = false; //did not find random flee-towards target, perhaps retry next tick
                else
                    flee = true;
            }
        }
        if (flee)
            npc.setAttackTarget(fleeTarget);
        return flee;
    }
    
    private EntityLiving getClosestVisibleHostile() {
        List list = this.npc.worldObj.selectEntitiesWithinAABB(EntityLiving.class, this.npc.boundingBox.expand(this.distanceFromEntity, 3.0D, this.distanceFromEntity), this.selector);
        if (list.isEmpty())
            return null;
        Collections.sort(list, sorter);
        EntityLiving fleeTarget = (EntityLiving) list.get(0);
        return this.npc.canEntityBeSeen(fleeTarget) ? fleeTarget : null;
    }

    @Override
    public void startExecuting() {
        npc.addAITask(TASK_GO_HOME);
        npc.addAITask(TASK_FLEE);
        stayOutOfSightTimer = MAX_STAY_AWAY + fearLevel;
        fearLevel += MAX_STAY_AWAY;
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    @Override
    public boolean continueExecuting() {
        if (!npc.getIsAIEnabled() || npc.getAttackTarget() == null || npc.getAttackTarget().isDead)
            return false;
        return stayOutOfSightTimer != 0 || npc.getAttackTarget().getDistanceSqToEntity(this.npc) < distanceFromEntity * distanceFromEntity;
    }

    @Override
    public void updateTask() {
        if (npc.getAttackTarget() == null || npc.getAttackTarget().isDead) {
            npc.setAttackTarget(null);
            stayOutOfSightTimer = 0;
            return;
        }
        BlockPosition pos = null;
        double distSq = 0;
        
        if (npc.hasHome()) {
            distSq = npc.getDistanceSqFromHome();
            ChunkCoordinates cc = npc.getHomePosition();
            pos = new BlockPosition(cc.posX, cc.posY, cc.posZ);
            if (distSq < MIN_RANGE) {
                // NPC is home, check for visible hostiles
                EntityLiving fleeTarget = getClosestVisibleHostile();
                if (fleeTarget != null) {
                    homeCompromised = true;
                    npc.addAITask(TASK_ALARM);
                }
            }
        }
        
        if ((homeCompromised || !npc.hasHome()) && npc.getTownHallPosition() != null) {
            pos = npc.getTownHallPosition();
            distSq = npc.getDistanceSq(pos);
            // TODO: Check if within range of town hall and somehow defend it/themselves from present hostiles
        }

        if (pos == null) {
            if (fleeVector == null)
                return;
            //check distance to flee vector
            distSq = npc.getDistanceSq(fleeVector.xCoord, fleeVector.yCoord, fleeVector.zCoord);
            if (distSq > MIN_RANGE)
                moveToPosition(fleeVector.xCoord, fleeVector.yCoord, fleeVector.zCoord, distSq);
            else {
                if (npc.getDistanceSqToEntity(npc.getAttackTarget()) < PURSUE_RANGE) {//entity still chasing, find a new flee vector 
                    fleeVector = RandomPositionGenerator.findRandomTargetBlockAwayFrom(this.npc, MAX_FLEE_RANGE, HEIGHT_CHECK, Vec3.createVectorHelper(npc.getAttackTarget().posX, npc.getAttackTarget().posY, npc.getAttackTarget().posZ));
                    if (fleeVector == null)
                        npc.setAttackTarget(null);//retry next tick..perhaps...
                } else // entity too far to care, stop running
                    npc.setAttackTarget(null);
            }
        }
        
        // keep running home
        if (pos!=null && distSq > MIN_RANGE) {
            moveToPosition(pos, distSq);
        }
        if (stayOutOfSightTimer > 0)
            stayOutOfSightTimer--;
        else
            resetTask();
    }

    @Override
    public void resetTask() {
        fleeVector = null;
        npc.getNavigator().clearPathEntity();
        npc.setAttackTarget(null);
        npc.removeAITask(TASK_GO_HOME + TASK_FLEE + TASK_ALARM);
        homeCompromised = false;
    }

}
