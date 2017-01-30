package net.shadowmage.ancientwarfare.npc.ai;

import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.Vec3;
import net.shadowmage.ancientwarfare.core.AncientWarfareCore;
import net.shadowmage.ancientwarfare.core.util.BlockPosition;
import net.shadowmage.ancientwarfare.npc.entity.NpcBase;
import net.shadowmage.ancientwarfare.npc.entity.NpcCombat;
import net.shadowmage.ancientwarfare.npc.entity.NpcPlayerOwned;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;

public class NpcAIFleeHostiles extends NpcAI<NpcPlayerOwned> {

    private static int MAX_STAY_AWAY = 50, MAX_FLEE_RANGE = 16, HEIGHT_CHECK = 7, PURSUE_RANGE = 16 * 16;
    private final IEntitySelector hostileOrFriendlyCombatNpcSelector;
    private final Comparator sorter;
    double distanceFromEntity = 16;
    private Vec3 fleeVector;
    private int stayOutOfSightTimer = 0;
    private int fearLevel = 0; // fear makes NPC's wait/flee for progressively longer periods
    private boolean homeCompromised = false;
    private LinkedHashSet<NpcCombat> nearbySoldiers = new LinkedHashSet<NpcCombat>();
    
    private int ticker = 0;
    private int tickerMax = 5; // scan for hostiles every 5 ticks

    public NpcAIFleeHostiles(final NpcPlayerOwned npc) {
        super(npc);
        hostileOrFriendlyCombatNpcSelector = new IEntitySelector() {
            @Override
            public boolean isEntityApplicable(Entity selectedEntity) {
                if(selectedEntity.isEntityAlive()) {
                    if (selectedEntity instanceof NpcBase) {
                        if (((NpcBase)selectedEntity).getNpcSubType().equals("soldier"))
                            if (((NpcBase)selectedEntity).hasCommandPermissions(npc.getOwnerName()))
                                    return true; // is a friendly soldier
                        return ((NpcBase) selectedEntity).isHostileTowards(NpcAIFleeHostiles.this.npc);
                    } else
                        return NpcAI.isAlwaysHostileToNpcs(selectedEntity);
                }
                return false;
            }
        };
        
        sorter = new EntityAINearestAttackableTarget.Sorter(npc);
        this.setMutexBits(ATTACK + MOVE);
    }

    @Override
    public boolean shouldExecute() {
        if (!npc.getIsAIEnabled())
            return false;
        // assume not fleeing, reduce fear level early
        if (fearLevel > 0)
            fearLevel--;
        
        ticker++;
        if (ticker != tickerMax)
            return false;
        ticker = 0;
        
        boolean flee = false;
        findNearbyRelevantEntities();
        if (!npc.nearbyHostiles.isEmpty()) {
            Entity nearestHostile = npc.nearbyHostiles.iterator().next();
            if (npc.getTownHallPosition() != null || npc.hasHome())
                flee = true;
            else {
                fleeVector = RandomPositionGenerator.findRandomTargetBlockAwayFrom(this.npc, MAX_FLEE_RANGE, HEIGHT_CHECK, Vec3.createVectorHelper(nearestHostile.posX, nearestHostile.posY, nearestHostile.posZ));
                if (fleeVector == null || nearestHostile.getDistanceSq(fleeVector.xCoord, fleeVector.yCoord, fleeVector.zCoord) < nearestHostile.getDistanceSqToEntity(this.npc))
                    flee = false; //did not find random flee-towards target, perhaps retry next tick
                else
                    flee = true;
            }
            if (flee) {
                if (nearestHostile instanceof EntityLivingBase)
                    npc.setAttackTarget((EntityLivingBase) nearestHostile);
                else
                    AncientWarfareCore.log.error("Attempted to flee an entity that isn't EntityLiving: '" + EntityList.getEntityString(nearestHostile) + "', ignoring! Please report this error.");
            }
        }
        return flee;
    }
    
    @SuppressWarnings("unchecked")
    private void findNearbyRelevantEntities() {
        npc.nearbyHostiles.clear();
        nearbySoldiers.clear();
        List nearbyHostilesOrFriendlySoldiers = this.npc.worldObj.selectEntitiesWithinAABB(EntityLiving.class, this.npc.boundingBox.expand(this.distanceFromEntity, 3.0D, this.distanceFromEntity), this.hostileOrFriendlyCombatNpcSelector);
        if (nearbyHostilesOrFriendlySoldiers.isEmpty())
            return;
        
        Collections.sort(nearbyHostilesOrFriendlySoldiers, sorter);
        for (Object entity : nearbyHostilesOrFriendlySoldiers) {
            if (npc.canEntityBeSeen((Entity)entity)) {
                if (entity instanceof NpcBase) {
                        if (((NpcBase)entity).getNpcSubType().equals("soldier"))
                            if (((NpcBase)entity).hasCommandPermissions(npc.getOwnerName()))
                                nearbySoldiers.add((NpcCombat)entity);
                } else
                        npc.nearbyHostiles.add((Entity)entity);
            }
        }
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
        boolean shouldPanic = true;
        if (npc.getIsAIEnabled() && (npc.getAttackTarget() == null || npc.getAttackTarget().isDead))
            fearLevel = MAX_STAY_AWAY; // cap the fear level in the event of hostile's death
        
        if (stayOutOfSightTimer == 0) {
            findNearbyRelevantEntities(); // rescan for hostiles
            shouldPanic = false;
        }
        
        if (!shouldPanic)
            if (!npc.nearbyHostiles.isEmpty()) {
                stayOutOfSightTimer = MAX_STAY_AWAY + fearLevel;
                shouldPanic = true;
                announceDistress();
            }
        
        return shouldPanic;
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
                if (!npc.nearbyHostiles.isEmpty()) {
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
    
    
    private void announceDistress() {
        for (NpcCombat soldier : nearbySoldiers) {
            soldier.respondToDistress(npc);
        }
        /*
        List nearbyFriendlyCombatNpcs = this.npc.worldObj.selectEntitiesWithinAABB(EntityLiving.class, this.npc.boundingBox.expand(this.distanceFromEntity, 3.0D, this.distanceFromEntity), friendlyCombatNpcSelector);
        if (nearbyFriendlyCombatNpcs.isEmpty())
            return;
        for (Object defender : nearbyFriendlyCombatNpcs) {
            ((NpcCombat)defender).respondToDistress(npc);
        }
        */
    }
    

}
