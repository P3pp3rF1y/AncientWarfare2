package net.shadowmage.ancientwarfare.npc.entity.faction;

import net.minecraft.entity.passive.AbstractHorse;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.npc.AncientWarfareNPC;
import net.shadowmage.ancientwarfare.npc.ai.faction.NpcAIFactionRideHorse;
import net.shadowmage.ancientwarfare.npc.entity.faction.attributes.AdditionalAttributes;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public abstract class NpcFactionMounted extends NpcFaction implements IHorseMountedNpc {
	private boolean horseLives = true;
	private NpcAIFactionRideHorse horseAI = new NpcAIFactionRideHorse<>(this);

	@Override
	public boolean isHorseAlive() {
		return horseLives;
	}

	@Override
	public void setHorseKilled() {
		horseLives = false;
	}

	public NpcFactionMounted(World world) {
		super(world);
		tasks.addTask(0, horseAI);
	}

	public NpcFactionMounted(World world, String factionName) {
		super(world, factionName);
		tasks.addTask(0, horseAI);
	}

	@Override
	protected void despawnEntity() {
		super.despawnEntity();
		if (isDead && getRidingEntity() instanceof EntityHorse) {
			getRidingEntity().setDead();
			dismountRidingEntity();
		}
	}

	@Override
	protected void onRepack() {
		if (getRidingEntity() instanceof EntityHorse) {
			getRidingEntity().setDead();
			dismountRidingEntity();
		}
	}

	@Override
	public boolean worksInRain() {
		return true;
	}

	@Override
	public boolean isPassive() {
		return false;
	}

	@Override
	public boolean shouldSleep() {
		return false;
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound tag) {
		super.readEntityFromNBT(tag);
		horseLives = tag.getBoolean("horseLives");
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound tag) {
		super.writeEntityToNBT(tag);
		tag.setBoolean("horseLives", horseLives);
	}

	@Override
	public AbstractHorse instantiateHorseEntity() {
		//noinspection unchecked
		Class<AbstractHorse> clazz = (Class<AbstractHorse>) getAdditionalAttributeValue(AdditionalAttributes.HORSE_ENTITY).orElse(EntityHorse.class);
		try {
			Constructor<AbstractHorse> ctr = clazz.getConstructor(World.class);
			return ctr.newInstance(world);
		}
		catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
			AncientWarfareNPC.LOG.error("Error instantiating horse entity for class: " + clazz.toString(), e);
			e.printStackTrace();
		}
		return new EntityHorse(world);
	}

	@Override
	public void onDeath(DamageSource damageSource) {
		horseAI.onKilled();
		super.onDeath(damageSource);
	}
}
