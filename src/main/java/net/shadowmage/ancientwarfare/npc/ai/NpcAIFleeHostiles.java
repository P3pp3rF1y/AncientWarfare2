package net.shadowmage.ancientwarfare.npc.ai;

import com.google.common.base.Predicate;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityOwnable;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.owner.TeamViewerRegistry;
import net.shadowmage.ancientwarfare.npc.AncientWarfareNPC;
import net.shadowmage.ancientwarfare.npc.entity.NpcBase;
import net.shadowmage.ancientwarfare.npc.entity.NpcCombat;
import net.shadowmage.ancientwarfare.npc.entity.NpcPlayerOwned;

import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.UUID;

public class NpcAIFleeHostiles extends NpcAI<NpcPlayerOwned> {

	private static final int MAX_STAY_AWAY = 50;
	private static final int MAX_FLEE_RANGE = 16;
	private static final int HEIGHT_CHECK = 7;
	private static final int PURSUE_RANGE = 16 * 16;
	private static final double DISTANCE_FROM_ENTITY = 16;

	@SuppressWarnings({"java:S4738", "Guava"}) //need to use Guava here becuase vanilla code uses it
	private final Predicate<EntityLiving> hostileOrFriendlyCombatNpcSelector;
	private final Comparator<Entity> sorter;
	private Vec3d fleeVector;
	private int stayOutOfSightTimer = 0;
	private int fearLevel = 0; // fear makes NPC's wait/flee for progressively longer periods
	private boolean homeCompromised = false;
	private final LinkedHashSet<NpcCombat> nearbySoldiers = new LinkedHashSet<>();

	private int ticker = 0;
	private static final int TICKER_MAX = 5; // scan for hostiles every 5 ticks

	public NpcAIFleeHostiles(final NpcPlayerOwned npc) {
		super(npc);
		hostileOrFriendlyCombatNpcSelector = selectedEntity -> {
			if (selectedEntity != null && selectedEntity.isEntityAlive()) {
				if (selectedEntity instanceof NpcBase) {
					return isFriendlySoldier(npc, (NpcBase) selectedEntity);
				} else {
					return NpcAIFleeHostiles.this.npc.isHostileTowards(selectedEntity);
				}
			}
			return false;
		};

		sorter = new EntityAINearestAttackableTarget.Sorter(npc);
		setMutexBits(MOVE);
	}

	private boolean isFriendlySoldier(NpcPlayerOwned npc, NpcBase selectedEntity) {
		return selectedEntity.getNpcSubType().equals("soldier") && selectedEntity.hasCommandPermissions(npc.getOwner())
				|| selectedEntity.isHostileTowards(NpcAIFleeHostiles.this.npc);
	}

	@Override
	public boolean shouldExecute() {
		if (!super.shouldExecute()) {
			return false;
		}

		if (stayOutOfSightTimer > 0) {
			return true;
		}

		ticker++;
		if (ticker != TICKER_MAX) {
			return false;
		}
		ticker = 0;

		stayOutOfSightTimer = MAX_STAY_AWAY + fearLevel;

		boolean flee = false;
		findNearbyRelevantEntities();
		if (!npc.nearbyHostiles.isEmpty()) {
			Entity nearestHostile = npc.nearbyHostiles.iterator().next();
			if (npc.getTownHallPosition().isPresent() || npc.hasHome()) {
				flee = true;
			} else {
				fleeVector = RandomPositionGenerator.findRandomTargetBlockAwayFrom(npc, MAX_FLEE_RANGE, HEIGHT_CHECK, new Vec3d(nearestHostile.posX, nearestHostile.posY, nearestHostile.posZ));
				flee = fleeVector != null && nearestHostile.getDistanceSq(fleeVector.x, fleeVector.y, fleeVector.z) >= nearestHostile.getDistanceSq(npc);
			}
			if (flee) {
				if (nearestHostile instanceof EntityLivingBase) {
					npc.setAttackTarget((EntityLivingBase) nearestHostile);
				} else {
					AncientWarfareNPC.LOG.error("Attempted to flee an entity that isn't EntityLiving: '{}', ignoring! Please report this error.", EntityList.getEntityString(nearestHostile));
				}
			}
		}
		return flee;
	}

	private void findNearbyRelevantEntities() {
		npc.nearbyHostiles.clear();
		nearbySoldiers.clear();
		List<EntityLiving> nearbyHostilesOrFriendlySoldiers = npc.world.getEntitiesWithinAABB(EntityLiving.class,
				npc.getEntityBoundingBox().expand(DISTANCE_FROM_ENTITY, 3.0D, DISTANCE_FROM_ENTITY), hostileOrFriendlyCombatNpcSelector);
		if (nearbyHostilesOrFriendlySoldiers.isEmpty()) {
			return;
		}

		nearbyHostilesOrFriendlySoldiers.sort(sorter);
		for (EntityLiving entity : nearbyHostilesOrFriendlySoldiers) {
			if (npc.canEntityBeSeen(entity)) {
				if (entity instanceof NpcCombat) {
					if (((NpcCombat) entity).getNpcSubType().equals("soldier") && ((NpcCombat) entity).hasCommandPermissions(npc.getOwner())) {
						nearbySoldiers.add((NpcCombat) entity);
					}
				} else if (entity instanceof IEntityOwnable && ((IEntityOwnable) entity).getOwner() != null) {
					Entity owner = ((IEntityOwnable) entity).getOwner();
					if (owner != null && isFriendly(entity.world, owner.getUniqueID(), owner.getName())) {
						return;
					}
				} else {
					npc.nearbyHostiles.add(entity);
				}
			}
		}
	}
	// check if  the owner is same or owned by a team member or owned by a friend

	public boolean isFriendly(World world, @Nullable UUID ownerId, String ownerName) {
		return TeamViewerRegistry.areFriendly(world, npc.getOwner().getUUID(), ownerId, npc.getOwner().getName(), ownerName);
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
	public void updateTask() {
		if (fearLevel > 0) {
			fearLevel--;
		}

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
			if (distSq < MIN_RANGE && !npc.nearbyHostiles.isEmpty()) {
				homeCompromised = true;
				npc.addAITask(TASK_ALARM);
			}
		}

		if (homeCompromised || !npc.hasHome()) {
			distSq = npc.getTownHallPosition().map(townHallPos -> npc.getDistanceSq(townHallPos)).orElse(distSq);
			// TODO: Check if within range of town hall and somehow defend it/themselves from present hostiles
		}

		if (pos == null) {
			if (fleeVector == null) {
				return;
			}
			//check distance to flee vector
			distSq = npc.getDistanceSq(fleeVector.x, fleeVector.y, fleeVector.z);
			if (distSq > MIN_RANGE) {
				moveToPosition(fleeVector.x, fleeVector.y, fleeVector.z, distSq);
			} else {
				if (npc.getDistanceSq(npc.getAttackTarget()) < PURSUE_RANGE) {//entity still chasing, find a new flee vector
					fleeVector = RandomPositionGenerator.findRandomTargetBlockAwayFrom(npc, MAX_FLEE_RANGE, HEIGHT_CHECK, new Vec3d(npc.getAttackTarget().posX, npc.getAttackTarget().posY, npc.getAttackTarget().posZ));
					if (fleeVector == null) {
						npc.setAttackTarget(null);//retry next tick..perhaps...
					}
				} else // entity too far to care, stop running
				{
					npc.setAttackTarget(null);
				}
			}
		}

		// keep running home
		if (pos != null && distSq > MIN_RANGE) {
			moveToPosition(pos, distSq);
		}
		if (stayOutOfSightTimer > 0) {
			stayOutOfSightTimer--;
		} else {
			announceDistress();
		}
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
	}
}
