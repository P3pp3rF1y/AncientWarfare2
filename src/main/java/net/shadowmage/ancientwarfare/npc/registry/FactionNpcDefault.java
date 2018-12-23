package net.shadowmage.ancientwarfare.npc.registry;

import jdk.nashorn.internal.ir.annotations.Immutable;
import net.minecraft.item.Item;
import net.shadowmage.ancientwarfare.npc.entity.faction.NpcFaction;
import net.shadowmage.ancientwarfare.npc.entity.faction.attributes.IAdditionalAttribute;

import java.util.HashMap;
import java.util.Map;

@Immutable
public class FactionNpcDefault extends NpcDefault {
	private final Map<IAdditionalAttribute<?>, Object> additionaAttributes;
	private final boolean enabled;

	public FactionNpcDefault(Map<String, Double> attributes, int experienceDrop, boolean canSwim, boolean canBreakDoors, Map<Integer, Item> equipment, Map<IAdditionalAttribute<?>, Object> additionaAttributes, boolean enabled) {
		super(attributes, experienceDrop, canSwim, canBreakDoors, equipment);
		this.additionaAttributes = additionaAttributes;
		this.enabled = enabled;
	}

	@Override
	public FactionNpcDefault setExperienceDrop(int experienceDrop) {
		return new FactionNpcDefault(attributes, experienceDrop, canSwim, canBreakDoors, equipment, additionaAttributes, enabled);
	}

	@Override
	public FactionNpcDefault setCanSwim(boolean canSwim) {
		return new FactionNpcDefault(attributes, experienceDrop, canSwim, canBreakDoors, equipment, additionaAttributes, enabled);
	}

	@Override
	public FactionNpcDefault setCanBreakDoors(boolean canBreakDoors) {
		return new FactionNpcDefault(attributes, experienceDrop, canSwim, canBreakDoors, equipment, additionaAttributes, enabled);
	}

	public FactionNpcDefault setEnabled(boolean enabled) {
		return new FactionNpcDefault(attributes, experienceDrop, canSwim, canBreakDoors, equipment, additionaAttributes, enabled);
	}

	@Override
	public FactionNpcDefault setAttributes(Map<String, Double> additionalAttributes) {
		Map<String, Double> newAttributes = new HashMap<>();
		newAttributes.putAll(this.attributes);
		newAttributes.putAll(additionalAttributes);
		return new FactionNpcDefault(newAttributes, experienceDrop, canSwim, canBreakDoors, equipment, additionaAttributes, enabled);
	}

	@Override
	public FactionNpcDefault setEquipment(Map<Integer, Item> additionalEquipment) {
		Map<Integer, Item> newEquipment = new HashMap<>();
		newEquipment.putAll(equipment);
		newEquipment.putAll(additionalEquipment);
		return new FactionNpcDefault(attributes, experienceDrop, canSwim, canBreakDoors, newEquipment, additionaAttributes, enabled);
	}

	public FactionNpcDefault setAdditionalAttributes(Map<IAdditionalAttribute<?>, Object> overrides) {
		Map<IAdditionalAttribute<?>, Object> newAdditionalAttributes = new HashMap<>();
		newAdditionalAttributes.putAll(additionaAttributes);
		newAdditionalAttributes.putAll(overrides);
		return new FactionNpcDefault(attributes, experienceDrop, canSwim, canBreakDoors, equipment, newAdditionalAttributes, enabled);
	}

	public void applyAdditionalAttributes(NpcFaction npc) {
		additionaAttributes.forEach(npc::setAdditionalAttribute);
	}

	public boolean isEnabled() {
		return enabled;
	}
}
