package net.shadowmage.ancientwarfare.npc.ai;

import com.google.common.base.Predicate;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.shadowmage.ancientwarfare.core.AncientWarfareCore;
import net.shadowmage.ancientwarfare.npc.entity.NpcBase;
import net.shadowmage.ancientwarfare.npc.entity.NpcCombat;
import net.shadowmage.ancientwarfare.npc.entity.NpcPlayerOwned;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;

public class NpcAIFleeHostiles extends NpcAI<NpcPlayerOwned> {

	private static int MAX_STAY_AWAY = 50, MAX_FLEE_RANGE = 16, HEIGHT_CHECK = 7, PURSUE_RANGE = 16 * 16;
	private final Predicate<EntityLiving> hostileOrFriendlyCombatNpcSelector;
	private final Comparator sorter;
	double distanceFromEntity = 16;
	private Vec3d fleeVector;
	private int stayOutOfSightTimer = 0;
	private int fearLevel = 0; // fear makes NPC's wait/flee for progressively longer periods
	private boolean homeCompromised = false;
	private LinkedHashSet<NpcCombat> nearbySoldiers = new LinkedHashSet<NpcCombat>();

	private int ticker = 0;
	private int tickerMax = 5; // scan for hostiles every 5 ticks

	public NpcAIFleeHostiles(final NpcPlayerOwned npc) {
		super(npc);
		hostileOrFriendlyCombatNpcSelector = selectedEntity -> {
			if (selectedEntity.isEntityAlive()) {
				if (selectedEntity instanceof NpcBase) {
					if (((NpcBase) selectedEntity).getNpcSubType().equals("soldier"))
						if (((NpcBase) selectedEntity).hasCommandPermissions(npc.getOwner()))
							return true; // is a friendly soldier
					return ((NpcBase) selectedEntity).isHostileTowards(NpcAIFleeHostiles.this.npc);
				} else
					return NpcAI.isAlwaysHostileToNpcs(selectedEntity);
			}
			return false;
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
				fleeVector = RandomPositionGenerator.findRandomTargetBlockAwayFrom(this.npc, MAX_FLEE_RANGE, HEIGHT_CHECK, new Vec3d(nearestHostile.posX, nearestHostile.posY, nearestHostile.posZ));
				if (fleeVector == null || nearestHostile.getDistanceSq(fleeVector.x, fleeVector.y, fleeVector.z) < nearestHostile.getDistanceSq(this.npc))
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

	private void findNearbyRelevantEntities() {
		npc.nearbyHostiles.clear();
		nearbySoldiers.clear();
		List nearbyHostilesOrFriendlySoldiers = this.npc.world.getEntitiesWithinAABB(EntityLiving.class, this.npc.getEntityBoundingBox().expand(this.distanceFromEntity, 3.0D, this.distanceFromEntity), this.hostileOrFriendlyCombatNpcSelector);
		if (nearbyHostilesOrFriendlySoldiers.isEmpty())
			return;

		Collections.sort(nearbyHostilesOrFriendlySoldiers, sorter);
		for (Object entity : nearbyHostilesOrFriendlySoldiers) {
			if (npc.canEntityBeSeen((Entity) entity)) {
				if (entity instanceof NpcBase) {
					if (((NpcBase) entity).getNpcSubType().equals("soldier"))
						if (((NpcBase) entity).hasCommandPermissions(npc.getOwner()))
							nearbySoldiers.add((NpcCombat) entity);
				} else
					npc.nearbyHostiles.add((Entity) entity);
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

	/*
	 * Returns whether an in-progress EntityAIBase should continue executing
	 */
	@Override
	public boolean shouldContinueExecuting() {
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
		BlockPos pos = null;
		double distSq = 0;

		if (npc.hasHome()) {
			distSq = npc.getDistanceSqFromHome();
			pos = npc.getHomePosition();
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
			distSq = npc.getDistanceSq(fleeVector.x, fleeVector.y, fleeVector.z);
			if (distSq > MIN_RANGE)
				moveToPosition(fleeVector.x, fleeVector.y, fleeVector.z, distSq);
			else {
				if (npc.getDistanceSq(npc.getAttackTarget()) < PURSUE_RANGE) {//entity still chasing, find a new flee vector
					fleeVector = RandomPositionGenerator.findRandomTargetBlockAwayFrom(this.npc, MAX_FLEE_RANGE, HEIGHT_CHECK, new Vec3d(npc.getAttackTarget().posX, npc.getAttackTarget().posY, npc.getAttackTarget().posZ));
					if (fleeVector == null)
						npc.setAttackTarget(null);//retry next tick..perhaps...
				} else // entity too far to care, stop running
					npc.setAttackTarget(null);
			}
		}

		// keep running home
		if (pos != null && distSq > MIN_RANGE) {
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
		npc.getNavigator().clearPath();
		npc.setAttackTarget(null);
		npc.removeAITask(TASK_GO_HOME + TASK_FLEE + TASK_ALARM);
		homeCompromised = false;
	}

	private void announceDistress() {
		for (NpcCombat soldier : nearbySoldiers) {
			soldier.respondToDistress(npc);
		}
		/*
		List nearbyFriendlyCombatNpcs = this.npc.world.selectEntitiesWithinAABB(EntityLiving.class, this.npc.getEntityBoundingBox().expand(this.distanceFromEntity, 3.0D, this.distanceFromEntity), friendlyCombatNpcSelector);
        if (nearbyFriendlyCombatNpcs.isEmpty())
            return;
        for (Object defender : nearbyFriendlyCombatNpcs) {
            ((NpcCombat)defender).respondToDistress(npc);
        }
        */
	}

}
