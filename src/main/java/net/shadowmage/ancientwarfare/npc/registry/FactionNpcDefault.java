package net.shadowmage.ancientwarfare.npc.registry;

import jdk.nashorn.internal.ir.annotations.Immutable;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootTableList;
import net.shadowmage.ancientwarfare.npc.entity.faction.NpcFaction;
import net.shadowmage.ancientwarfare.npc.entity.faction.attributes.IAdditionalAttribute;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

@Immutable
public class FactionNpcDefault extends NpcDefault {
	private final Map<IAdditionalAttribute<?>, Object> additionaAttributes;
	private final boolean enabled;
	private ResourceLocation lootTable;

	public FactionNpcDefault(Map<String, Double> attributes, int experienceDrop, boolean canSwim, boolean canBreakDoors, Map<Integer, Item> equipment,
			Map<IAdditionalAttribute<?>, Object> additionaAttributes, boolean enabled, @Nullable ResourceLocation lootTable) {
		super(attributes, experienceDrop, canSwim, canBreakDoors, equipment);
		this.additionaAttributes = additionaAttributes;
		this.enabled = enabled;
		this.lootTable = lootTable;
		if (!LootTableList.getAll().contains(lootTable)) {
			LootTableList.register(lootTable);
		}
	}

	@Override
	public FactionNpcDefault setExperienceDrop(int experienceDrop) {
		return new FactionNpcDefault(attributes, experienceDrop, canSwim, canBreakDoors, equipment, additionaAttributes, enabled, lootTable);
	}

	@Override
	public FactionNpcDefault setCanSwim(boolean canSwim) {
		return new FactionNpcDefault(attributes, experienceDrop, canSwim, canBreakDoors, equipment, additionaAttributes, enabled, lootTable);
	}

	@Override
	public FactionNpcDefault setCanBreakDoors(boolean canBreakDoors) {
		return new FactionNpcDefault(attributes, experienceDrop, canSwim, canBreakDoors, equipment, additionaAttributes, enabled, lootTable);
	}

	public FactionNpcDefault setEnabled(boolean enabled) {
		return new FactionNpcDefault(attributes, experienceDrop, canSwim, canBreakDoors, equipment, additionaAttributes, enabled, lootTable);
	}

	@Override
	public FactionNpcDefault setAttributes(Map<String, Double> additionalAttributes) {
		Map<String, Double> newAttributes = new HashMap<>();
		newAttributes.putAll(this.attributes);
		newAttributes.putAll(additionalAttributes);
		return new FactionNpcDefault(newAttributes, experienceDrop, canSwim, canBreakDoors, equipment, additionaAttributes, enabled, lootTable);
	}

	@Override
	public FactionNpcDefault setEquipment(Map<Integer, Item> additionalEquipment) {
		Map<Integer, Item> newEquipment = new HashMap<>();
		newEquipment.putAll(equipment);
		newEquipment.putAll(additionalEquipment);
		return new FactionNpcDefault(attributes, experienceDrop, canSwim, canBreakDoors, newEquipment, additionaAttributes, enabled, lootTable);
	}

	public FactionNpcDefault setAdditionalAttributes(Map<IAdditionalAttribute<?>, Object> overrides) {
		Map<IAdditionalAttribute<?>, Object> newAdditionalAttributes = new HashMap<>();
		newAdditionalAttributes.putAll(additionaAttributes);
		newAdditionalAttributes.putAll(overrides);
		return new FactionNpcDefault(attributes, experienceDrop, canSwim, canBreakDoors, equipment, newAdditionalAttributes, enabled, lootTable);
	}

	public FactionNpcDefault setLootTable(ResourceLocation lootTable) {
		return new FactionNpcDefault(attributes, experienceDrop, canSwim, canBreakDoors, equipment, additionaAttributes, enabled, lootTable);
	}

	public void applyAdditionalAttributes(NpcFaction npc) {
		additionaAttributes.forEach(npc::setAdditionalAttribute);
	}

	public boolean isEnabled() {
		return enabled;
	}

	public ResourceLocation getLootTable() {
		return lootTable;
	}
}
