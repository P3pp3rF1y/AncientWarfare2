package net.shadowmage.ancientwarfare.npc.registry;

import jdk.nashorn.internal.ir.annotations.Immutable;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootTableList;
import net.shadowmage.ancientwarfare.npc.entity.faction.NpcFaction;
import net.shadowmage.ancientwarfare.npc.entity.faction.attributes.IAdditionalAttribute;
import org.apache.commons.lang3.Range;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

@Immutable
public class FactionNpcDefault extends NpcDefault {
	private Map<IAdditionalAttribute<?>, Object> additionaAttributes;
	private boolean enabled;
	private ResourceLocation lootTable;
	private Range<Float> heightRange;
	private float thinness;

	public FactionNpcDefault(Map<String, Double> attributes, int experienceDrop, boolean canSwim, boolean canBreakDoors, Map<Integer, Item> equipment,
			Map<IAdditionalAttribute<?>, Object> additionaAttributes, boolean enabled,
			@Nullable ResourceLocation lootTable, Range<Float> heightRange, float thinness) {
		super(attributes, experienceDrop, canSwim, canBreakDoors, equipment);
		this.additionaAttributes = additionaAttributes;
		this.enabled = enabled;
		this.lootTable = lootTable;
		this.heightRange = heightRange;
		this.thinness = thinness;
		if (lootTable != null && !LootTableList.getAll().contains(lootTable)) {
			LootTableList.register(lootTable);
		}
	}

	private FactionNpcDefault copy() {
		return new FactionNpcDefault(new HashMap<>(attributes), experienceDrop, canSwim, canBreakDoors, new HashMap<>(equipment), new HashMap<>(additionaAttributes), enabled, lootTable, heightRange, thinness);
	}

	private FactionNpcDefault change(Consumer<FactionNpcDefault> makeChange) {
		FactionNpcDefault copy = copy();
		makeChange.accept(copy);
		return copy;
	}

	@Override
	public FactionNpcDefault setExperienceDrop(int experienceDrop) {
		return change(def -> def.experienceDrop = experienceDrop);
	}

	@Override
	public FactionNpcDefault setCanSwim(boolean canSwim) {
		return change(def -> def.canSwim = canSwim);
	}

	@Override
	public FactionNpcDefault setCanBreakDoors(boolean canBreakDoors) {
		return change(def -> def.canBreakDoors = canBreakDoors);
	}

	public FactionNpcDefault setEnabled(boolean enabled) {
		return change(def -> def.enabled = enabled);
	}

	@Override
	public FactionNpcDefault setAttributes(Map<String, Double> additionalAttributes) {
		return change(def -> def.attributes.putAll(additionalAttributes));
	}

	@Override
	public FactionNpcDefault setEquipment(Map<Integer, Item> additionalEquipment) {
		return change(def -> def.equipment.putAll(additionalEquipment));
	}

	public FactionNpcDefault setAdditionalAttributes(Map<IAdditionalAttribute<?>, Object> overrides) {
		return change(def -> def.additionaAttributes.putAll(overrides));
	}

	public FactionNpcDefault setLootTable(ResourceLocation lootTable) {
		return change(def -> def.lootTable = lootTable);
	}

	public FactionNpcDefault setHeightRange(Range<Float> heightRange) {
		return change(def -> def.heightRange = heightRange);
	}

	public FactionNpcDefault setThinness(float thinness) {
		return change(def -> def.thinness = thinness);
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

	public Range<Float> getHeightRange() {
		return heightRange;
	}

	public float getThinness() {
		return thinness;
	}
}
