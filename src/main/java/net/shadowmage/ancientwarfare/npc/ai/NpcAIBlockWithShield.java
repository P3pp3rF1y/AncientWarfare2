package net.shadowmage.ancientwarfare.npc.ai;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.shadowmage.ancientwarfare.npc.entity.NpcBase;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.Random;
import java.util.function.Predicate;

public class NpcAIBlockWithShield extends NpcAI<NpcBase> {
	private EntityLivingBase target;
	private static final float SAFE_MELEE_DISTANCE = 4.5F;
	private final int maxReactionDelay = 5; //TODO turn into setting
	private final int maxReactionDelayBow = 30; //TODO turn into setting
	private int reactionDelayTicks = 0;
	private static final int SHIELD_WITHDRAW_DELAY = 40;
	private int shieldWithdrawTicks = 0;

	public NpcAIBlockWithShield(NpcBase npc) {
		super(npc);
	}

	@Override
	public boolean shouldExecute() {
		return super.shouldExecute() && canExecute(e -> e.isEntityAlive() && shouldDefendFrom(e));
	}

	private boolean canExecute(Predicate<EntityLivingBase> defendFrom) {
		if (target == null) {
			return hasShieldInOffhand() && npc.getShieldDisabledTick() <= 0 && (shieldWithdrawTicks > 0 || getAttackTarget().map(defendFrom::test).orElse(false));
		}
		return hasShieldInOffhand() && npc.getShieldDisabledTick() <= 0 && target.isEntityAlive() && getAttackTarget().isPresent() && (shieldWithdrawTicks > 0 || getAttackTarget().map(t -> t.equals(target) && shouldDefendFrom(target)).orElse(false));
	}

	private boolean shouldDefendFrom(@Nullable EntityLivingBase e) {
		return e != null && isPlayerOrTargetsThisNpc(e) && ((e.isSwingInProgress && npc.getDistance(e) < 4 ) || isAimingWithBow(e));
	}

	private boolean isPlayerOrTargetsThisNpc(EntityLivingBase e) {
		if (e instanceof EntityPlayer) {
			return true;
		}
		if (e instanceof EntityLiving) {
			EntityLiving living = (EntityLiving) e;
			return living.getAttackTarget() != null && living.getAttackTarget().equals(npc);
		}
		return false;
	}

	@Override
	public void startExecuting() {
		if (!npc.isHandActive()) {
			init();
		}
	}

	@SuppressWarnings("java:S2259")
	private void init() {
		target = getAttackTarget().orElse(null);
		reactionDelayTicks = new Random(npc.world.getTotalWorldTime()).nextInt(target != null && isAimingWithBow(target) ? maxReactionDelayBow : maxReactionDelay);
		shieldWithdrawTicks = SHIELD_WITHDRAW_DELAY;
	}

	@Override
	public final void resetTask() {
		target = null;
		reactionDelayTicks = 0;
		npc.startAIControlFlag(ATTACK);
	}

	@Override
	public final void updateTask() {
		if (reactionDelayTicks > 0) {
			reactionDelayTicks--;
			return;
		}
		if (!shouldDefendFrom(target)) {
			shieldWithdrawTicks--;
			return;
		}
		npc.stopAIControlFlag(ATTACK);
		npc.setActiveHand(EnumHand.OFF_HAND);
		npc.activeItemStackUseCount = SHIELD_WITHDRAW_DELAY;
		npc.getNavigator().clearPath();

		if (target != null) {
			npc.getLookHelper().setLookPositionWithEntity(target, 30.f, 30.f);
			double distanceToEntity = npc.getDistanceSq(target.posX, target.getEntityBoundingBox().minY, target.posZ);

			if (!shouldCloseOnTarget(distanceToEntity) || isAimingWithBow(target)) {
				startBlocking();
				npc.getNavigator().tryMoveToXYZ(target.posX, target.posY, target.posZ, npc.getDefaultMoveSpeed() * 0.5f);
			}
		}
	}

	private void startBlocking() {
		npc.setActiveHand(EnumHand.OFF_HAND);
	}

	private Optional<EntityLivingBase> getAttackTarget() {
		return npc.getAttackTarget() != null ? Optional.of(npc.getAttackTarget()) : Optional.ofNullable(npc.getRevengeTarget());
	}

	protected boolean shouldCloseOnTarget(double distanceToEntity) {
		double attackDistance = (npc.width / 2D) + (getTarget().width / 2D) + SAFE_MELEE_DISTANCE;
		return (distanceToEntity > (attackDistance * attackDistance)) || !npc.getEntitySenses().canSee(getTarget());
	}

	public final EntityLivingBase getTarget() {
		return target;
	}

	private boolean hasShieldInOffhand() {
		return npc.getHeldItemOffhand().getItem().isShield(npc.getHeldItemOffhand(), npc);
	}

	private boolean isAimingWithBow(EntityLivingBase entity) {
		return (npc.isBow(entity.getHeldItemMainhand().getItem()) && entity.isHandActive() && entity.getActiveHand() == EnumHand.MAIN_HAND) ||
				(npc.isBow(entity.getHeldItemOffhand().getItem()) && entity.isHandActive() && entity.getActiveHand() == EnumHand.OFF_HAND);
	}

	public void onPreDamage(DamageSource source, float damage) {
		if (damage > 0 && !source.isUnblockable() && canExecute(EntityLivingBase::isEntityAlive)) {
			init();
			updateTask();
		}
	}
}
