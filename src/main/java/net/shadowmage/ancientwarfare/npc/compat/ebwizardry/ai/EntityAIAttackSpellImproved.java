package net.shadowmage.ancientwarfare.npc.compat.ebwizardry.ai;

import electroblob.wizardry.entity.living.ISpellCaster;
import electroblob.wizardry.event.SpellCastEvent;
import electroblob.wizardry.event.SpellCastEvent.Source;
import electroblob.wizardry.packet.PacketNPCCastSpell;
import electroblob.wizardry.packet.WizardryPacketHandler;
import electroblob.wizardry.registry.Spells;
import electroblob.wizardry.spell.Spell;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.util.EnumHand;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is an extended version of Electroblob's {@link electroblob.wizardry.entity.living.EntityAIAttackSpell} class
 * Author: Electroblob, WinDanesz
 */
public class EntityAIAttackSpellImproved<T extends EntityLiving & ISpellCaster> extends EntityAIBase {

	private final T entity;
	private final double moveSpeedAmp;
	private int attackCooldown;
	private final float maxAttackDistance;

	private int attackTime = -1;
	private int seeTime;
	private boolean strafingClockwise;
	private boolean strafingBackwards;
	private int strafingTime = -1;


	/**
	 * The entity the AI instance has been applied to. Thanks to type parameters, methods from both EntityLiving and
	 * ISummonedCreature may be invoked on this field.
	 */
	private final T attacker;
	private EntityLivingBase target;
	/**
	 * Decremented each tick while greater than 0. When a spell is cast, this is set to that spell's cooldown plus the
	 * base cooldown.
	 */
	private int cooldown;
	/**
	 * The number of ticks between the entity finding a new target and when it first starts attacking, and also the
	 * amount that is added to the spell's cooldown between casting spells.
	 */
	private final int baseCooldown;
	/**
	 * Decremented each tick while greater than 0. When a continuous spell is first cast, this is set to the value of
	 */
	// I think that in this case this is only necessary on the server side. If any inconsistent behaviour
	// occurs, look into syncing this as well.
	private int continuousSpellTimer;
	/**
	 * The number of ticks that continuous spells will be cast for before cooling down.
	 */
	private final int continuousSpellDuration;
	/**
	 * The speed that the entity should move when attacking. Only used when passed into the navigator.
	 */
	private final double speed;
	/**
	 * Creates a new spell attack AI with the given parameters.
	 *
	 * @param attacker                The entity that that uses this AI.
	 * @param speed                   The speed that the entity should move when attacking. Only used when passed into the navigator.
	 * @param maxDistance             The maximum distance the entity should be from its target.
	 * @param baseCooldown            The number of ticks between the entity finding a new target and when it first starts
	 *                                attacking, and also the amount that is added to the cooldown of the spell that has just been cast.
	 * @param continuousSpellDuration The number of ticks that continuous spells will be cast for before cooling down.
	 */
	public EntityAIAttackSpellImproved(T attacker, double speed, float maxDistance, int baseCooldown, int continuousSpellDuration) {
		this.cooldown = -1;
		this.attacker = attacker;
		this.baseCooldown = baseCooldown;
		this.continuousSpellDuration = continuousSpellDuration;
		this.speed = speed;
		this.setMutexBits(3);
		this.entity = attacker;
		this.moveSpeedAmp = speed;
		this.attackCooldown = continuousSpellDuration;
		this.maxAttackDistance = maxDistance * maxDistance;
	}

	@Override
	public boolean shouldExecute() {

		EntityLivingBase entitylivingbase = this.attacker.getAttackTarget();

		if (entitylivingbase == null) {
			return false;
		} else {
			this.target = entitylivingbase;
			return true;
		}
	}

	@Override
	public boolean shouldContinueExecuting() {
		return this.shouldExecute() || !this.attacker.getNavigator().noPath();
	}

	@Override
	public void resetTask() {
		this.target = null;
		this.seeTime = 0;
		this.cooldown = -1;
		this.setContinuousSpellAndNotify(Spells.none, new SpellModifiers());
		this.continuousSpellTimer = 0;
	}

	private void setContinuousSpellAndNotify(Spell spell, SpellModifiers modifiers) {
		attacker.setContinuousSpell(spell);
		WizardryPacketHandler.net.sendToAllAround(
				new PacketNPCCastSpell.Message(attacker.getEntityId(), target == null ? -1 : target.getEntityId(),
						EnumHand.MAIN_HAND, spell, modifiers),
				// Particles are usually only visible from 16 blocks away, so 128 is more than far enough.
				// TODO: Why is this one a 128 block radius, whilst the other one is all in dimension?
				new TargetPoint(attacker.dimension, attacker.posX, attacker.posY, attacker.posZ, 128));
	}

	@Override
	public void updateTask() {

		// Only executed server side.

		double distanceSq = this.attacker.getDistanceSq(this.target.posX, this.target.posY,
				this.target.posZ);
		boolean targetIsVisible = this.attacker.getEntitySenses().canSee(this.target);

		if (targetIsVisible) {
			++this.seeTime;
		} else {
			this.seeTime = 0;
		}

		if (distanceSq <= (double) this.maxAttackDistance && this.seeTime >= 20) {
			this.attacker.getNavigator().clearPath();
		} else {
			this.attacker.getNavigator().tryMoveToEntityLiving(this.target, this.speed);
		}

		this.attacker.getLookHelper().setLookPositionWithEntity(this.target, 30.0F, 30.0F);

		if (this.continuousSpellTimer > 0) {

			this.continuousSpellTimer--;

			// If the target goes out of range or out of sight...
			if (distanceSq > (double) this.maxAttackDistance || !targetIsVisible
					// ...or the spell is cancelled via events...
					|| MinecraftForge.EVENT_BUS
					.post(new SpellCastEvent.Tick(Source.NPC, attacker.getContinuousSpell(), attacker,
							attacker.getModifiers(), this.continuousSpellDuration - this.continuousSpellTimer))
					// ...or the spell no longer succeeds...
					|| !attacker.getContinuousSpell().cast(attacker.world, attacker, EnumHand.MAIN_HAND,
					this.continuousSpellDuration - this.continuousSpellTimer, target, attacker.getModifiers())
					// ...or the time has elapsed...
					|| this.continuousSpellTimer == 0) {

				// ...reset the continuous spell timer and start the cooldown.
				this.continuousSpellTimer = 0;
				this.cooldown = attacker.getContinuousSpell().getCooldown() + this.baseCooldown;
				setContinuousSpellAndNotify(Spells.none, new SpellModifiers());
				return;

			} else if (this.continuousSpellDuration - this.continuousSpellTimer == 1) {
				// On the first tick, if the spell did succeed, fire SpellCastEvent.Post.
				MinecraftForge.EVENT_BUS.post(new SpellCastEvent.Post(Source.NPC, attacker.getContinuousSpell(),
						attacker, attacker.getModifiers()));
			}

		} else if (--this.cooldown == 0) {

			if (distanceSq > (double) this.maxAttackDistance || !targetIsVisible) {
				return;
			}

			double dx = target.posX - attacker.posX;
			double dz = target.posZ - attacker.posZ;

			List<Spell> spells = new ArrayList<Spell>(attacker.getSpells());

			if (spells.size() > 0) {

				if (!attacker.world.isRemote) {

					// New way of choosing a spell; keeps trying until one works or all have been tried

					Spell spell;

					while (!spells.isEmpty()) {

						spell = spells.get(attacker.world.rand.nextInt(spells.size()));

						SpellModifiers modifiers = attacker.getModifiers();

						if (spell != null && attemptCastSpell(spell, modifiers)) {
							// The spell worked, so we're done!
							attacker.rotationYaw = (float) (Math.atan2(dz, dx) * 180.0D / Math.PI) - 90.0F;
							return;
						} else {
							spells.remove(spell);
						}
					}
				}
			}

		} else if (this.cooldown < 0) {
			// This should only be reached when the entity first starts attacking. Stops it attacking instantly.
			this.cooldown = this.baseCooldown;
		}

		// based on {@link net.minecraft.entity.ai.EntityAIAttackRangedBow}

		EntityLivingBase entitylivingbase = this.entity.getAttackTarget();

		if (entitylivingbase != null)
		{
			double d0 = this.entity.getDistanceSq(entitylivingbase.posX, entitylivingbase.getEntityBoundingBox().minY, entitylivingbase.posZ);
			boolean flag = this.entity.getEntitySenses().canSee(entitylivingbase);
			boolean flag1 = this.seeTime > 0;

			if (flag != flag1)
			{
				this.seeTime = 0;
			}

			if (flag)
			{
				++this.seeTime;
			}
			else
			{
				--this.seeTime;
			}

			if (d0 <= (double)this.maxAttackDistance && this.seeTime >= 20)
			{
				this.entity.getNavigator().clearPath();
				++this.strafingTime;
			}
			else
			{
				this.entity.getNavigator().tryMoveToEntityLiving(entitylivingbase, this.moveSpeedAmp);
				this.strafingTime = -1;
			}

			if (this.strafingTime >= 20)
			{
				if ((double)this.entity.getRNG().nextFloat() < 0.3D)
				{
					this.strafingClockwise = !this.strafingClockwise;
				}

				if ((double)this.entity.getRNG().nextFloat() < 0.3D)
				{
					this.strafingBackwards = !this.strafingBackwards;
				}

				this.strafingTime = 0;
			}

			if (this.strafingTime > -1)
			{
				if (d0 > (double)(this.maxAttackDistance * 0.75F))
				{
					this.strafingBackwards = false;
				}
				else if (d0 < (double)(this.maxAttackDistance * 0.25F))
				{
					this.strafingBackwards = true;
				}

				this.entity.getMoveHelper().strafe(this.strafingBackwards ? -0.5F : 0.5F, this.strafingClockwise ? 0.5F : -0.5F);
				this.entity.faceEntity(entitylivingbase, 30.0F, 30.0F);
			}
			else
			{
				this.entity.getLookHelper().setLookPositionWithEntity(entitylivingbase, 30.0F, 30.0F);
			}
		}
	}

	/**
	 * Attempts to cast the given spell (including event firing) and returns true if it succeeded.
	 */
	private boolean attemptCastSpell(Spell spell, SpellModifiers modifiers) {

		// If anything stops the spell working at this point, nothing else happens.
		if (MinecraftForge.EVENT_BUS.post(new SpellCastEvent.Pre(Source.NPC, spell, attacker, modifiers))) {
			return false;
		}

		// This is only called when spell casting starts so ticksInUse is always zero
		if (spell.cast(attacker.world, attacker, EnumHand.MAIN_HAND, 0, target, modifiers)) {

			if (spell.isContinuous) {
				// -1 because the spell has been cast once already!
				this.continuousSpellTimer = this.continuousSpellDuration - 1;
				setContinuousSpellAndNotify(spell, modifiers);

			} else {

				MinecraftForge.EVENT_BUS.post(new SpellCastEvent.Post(Source.NPC, spell, attacker, modifiers));

				// For now, the cooldown is just added to the constant base cooldown. I think this
				// is a reasonable way of doing things; it's certainly better than before.
				this.cooldown = this.baseCooldown + spell.getCooldown();

				if (spell.requiresPacket()) {
					// Sends a packet to all players in dimension to tell them to spawn particles.
					IMessage msg = new PacketNPCCastSpell.Message(attacker.getEntityId(), target.getEntityId(),
							EnumHand.MAIN_HAND, spell, modifiers);
					WizardryPacketHandler.net.sendToDimension(msg, attacker.world.provider.getDimension());
				}
			}

			return true;
		}

		return false;
	}
}
