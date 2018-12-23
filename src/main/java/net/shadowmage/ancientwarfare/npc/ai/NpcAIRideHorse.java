package net.shadowmage.ancientwarfare.npc.ai;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAITasks;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.passive.AbstractHorse;
import net.minecraft.entity.passive.EntityHorse;
import net.shadowmage.ancientwarfare.npc.entity.NpcBase;

import java.util.ArrayList;
import java.util.List;

public class NpcAIRideHorse<T extends NpcBase> extends NpcAI<T> {
	private final AttributeModifier followRangeModifier;
	private final AttributeModifier moveSpeedModifier;

	protected AbstractHorse horse;
	private List<EntityAITasks.EntityAITaskEntry> horseAI = new ArrayList<>();

	public NpcAIRideHorse(T npc, double speedFactor) {
		super(npc);
		this.moveSpeedModifier = new AttributeModifier("modifier.npc_ride_speed", speedFactor, 1);
		this.moveSpeedModifier.setSaved(false);
		this.followRangeModifier = new AttributeModifier("modifier.npc_horse_path_extension", 24.d, 0);
		this.followRangeModifier.setSaved(false);
	}

	@Override
	public boolean shouldExecute() {
		if (horse == null && npc.getRidingEntity() instanceof EntityHorse) {
			horse = (EntityHorse) npc.getRidingEntity();
			onMountHorse();
			return true;
		}
		return false;
	}

	protected void onMountHorse() {
		removeHorseAI();
		horse.setHorseSaddled(true);
		horse.setEatingHaystack(false);
		horse.setRearing(false);
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
		horse.setHorseSaddled(true);
		removeModifiers();
	}

	private void applyModifiers() {
		removeModifiers();
		horse.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).applyModifier(moveSpeedModifier);
		horse.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).applyModifier(followRangeModifier);
	}

	private void removeModifiers() {
		horse.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).removeModifier(moveSpeedModifier);
		horse.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).removeModifier(followRangeModifier);
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
