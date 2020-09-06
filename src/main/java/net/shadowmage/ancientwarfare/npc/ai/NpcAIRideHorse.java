package net.shadowmage.ancientwarfare.npc.ai;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAITasks;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.passive.AbstractHorse;
import net.minecraft.entity.passive.EntityHorse;
import net.shadowmage.ancientwarfare.npc.entity.NpcBase;

import java.util.ArrayList;
import java.util.List;

public class NpcAIRideHorse<T extends NpcBase> extends NpcAI<T> {
	private static final AttributeModifier FOLLOW_RANGE_MODIFIER = new AttributeModifier("modifier.npc_horse_path_extension", 24.d, 0).setSaved(false);
	private final AttributeModifier moveSpeedModifier;

	protected EntityLiving horse;
	private final List<EntityAITasks.EntityAITaskEntry> horseAI = new ArrayList<>();

	public NpcAIRideHorse(T npc, double speedFactor) {
		super(npc);
		moveSpeedModifier = new AttributeModifier("modifier.npc_ride_speed", speedFactor, 1).setSaved(false);
	}

	@Override
	public boolean shouldExecute() {
		return super.shouldExecute() && shouldRideHorse();
	}

	protected boolean shouldRideHorse() {
		return horse == null && npc.getRidingEntity() instanceof EntityHorse;
	}

	@Override
	public void startExecuting() {
		horse = (EntityLiving) npc.getRidingEntity();
		onMountHorse();
	}

	protected void onMountHorse() {
		removeHorseAI();
		if (horse instanceof AbstractHorse) {
			AbstractHorse h = (AbstractHorse) horse;
			h.setHorseSaddled(true);
			h.setEatingHaystack(false);
			h.setRearing(false);
		}
		applyModifiers();
	}

	public void onKilled() {
		if (horse != null) {
			onDismountHorse();
			horse = null;
		}
	}

	protected void onDismountHorse() {
		addHorseAI();
		if (horse instanceof AbstractHorse) {
			((AbstractHorse) horse).setHorseSaddled(true);
			removeModifiers();
		}
	}

	private void applyModifiers() {
		if (horse instanceof AbstractHorse) {
			removeModifiers();
			horse.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).applyModifier(moveSpeedModifier);
			horse.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).applyModifier(FOLLOW_RANGE_MODIFIER);
		}
	}

	private void removeModifiers() {
		horse.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).removeModifier(moveSpeedModifier);
		horse.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).removeModifier(FOLLOW_RANGE_MODIFIER);
	}

	private void removeHorseAI() {
		horseAI.clear();
		horseAI.addAll(horse.tasks.taskEntries);
		for (EntityAITasks.EntityAITaskEntry task : horseAI) {
			horse.tasks.removeTask(task.action);
		}
	}

	private void addHorseAI() {
		if (horse.tasks.taskEntries.isEmpty()) {
			horse.tasks.taskEntries.addAll(horseAI);
		}
		horseAI.clear();
	}
}
