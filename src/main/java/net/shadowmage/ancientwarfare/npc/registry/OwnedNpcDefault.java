package net.shadowmage.ancientwarfare.npc.registry;

import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.registry.EntityRegistry;

import javax.annotation.concurrent.Immutable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Immutable
public class OwnedNpcDefault extends NpcDefault {
	private final Set<String> targetList;

	public OwnedNpcDefault(Set<String> targetList, Map<String, Double> attributes, int experienceDrop, boolean canSwim, boolean canBreakDoors, Map<Integer, Item> equipment) {
		super(attributes, experienceDrop, canSwim, canBreakDoors, equipment);
		this.targetList = targetList;
	}

	public OwnedNpcDefault overrideTargets(Set<String> newTargetList) {
		return new OwnedNpcDefault(newTargetList, attributes, experienceDrop, canSwim, canBreakDoors, equipment);
	}

	public boolean isTarget(Entity entity) {
		//noinspection ConstantConditions
		return EntityRegistry.getEntry(entity.getClass()) != null
				&& targetList.contains(EntityRegistry.getEntry(entity.getClass()).getRegistryName().toString());
	}

	@Override
	public OwnedNpcDefault setExperienceDrop(int experienceDrop) {
		return new OwnedNpcDefault(targetList, attributes, experienceDrop, canSwim, canBreakDoors, equipment);
	}

	@Override
	public OwnedNpcDefault setCanSwim(boolean canSwim) {
		return new OwnedNpcDefault(targetList, attributes, experienceDrop, canSwim, canBreakDoors, equipment);
	}

	@Override
	public OwnedNpcDefault setCanBreakDoors(boolean canBreakDoors) {
		return new OwnedNpcDefault(targetList, attributes, experienceDrop, canSwim, canBreakDoors, equipment);
	}

	@Override
	public OwnedNpcDefault setAttributes(Map<String, Double> additionalAttributes) {
		Map<String, Double> newAttributes = new HashMap<>();
		newAttributes.putAll(this.attributes);
		newAttributes.putAll(additionalAttributes);
		return new OwnedNpcDefault(targetList, newAttributes, experienceDrop, canSwim, canBreakDoors, equipment);
	}

	@Override
	public OwnedNpcDefault setEquipment(Map<Integer, Item> additionalEquipment) {
		Map<Integer, Item> newEquipment = new HashMap<>();
		newEquipment.putAll(equipment);
		newEquipment.putAll(additionalEquipment);
		return new OwnedNpcDefault(targetList, attributes, experienceDrop, canSwim, canBreakDoors, newEquipment);
	}
}
