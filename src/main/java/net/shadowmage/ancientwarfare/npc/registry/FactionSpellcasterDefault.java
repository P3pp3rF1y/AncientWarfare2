package net.shadowmage.ancientwarfare.npc.registry;

import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootTableList;
import net.shadowmage.ancientwarfare.npc.entity.faction.NpcFaction;
import net.shadowmage.ancientwarfare.npc.entity.faction.attributes.IAdditionalAttribute;
import org.apache.commons.lang3.Range;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

@Immutable
public class FactionSpellcasterDefault extends NpcDefault {
	private Map<IAdditionalAttribute<?>, Object> additionalAttributes;
	private boolean enabled;
	private ResourceLocation lootTable;
	private Range<Float> heightRange;
	private float thinness;

	public FactionSpellcasterDefault(Map<String, Double> attributes, int experienceDrop, boolean canSwim, boolean canBreakDoors, Map<Integer, Item> equipment,
			Map<IAdditionalAttribute<?>, Object> additionaAttributes, boolean enabled,
			@Nullable ResourceLocation lootTable, Range<Float> heightRange, float thinness) {
		super(attributes, experienceDrop, canSwim, canBreakDoors, equipment);
		this.additionalAttributes = additionaAttributes;
		this.enabled = enabled;
		this.lootTable = lootTable;
		this.heightRange = heightRange;
		this.thinness = thinness;
		if (lootTable != null && !LootTableList.getAll().contains(lootTable)) {
			LootTableList.register(lootTable);
		}
	}

	private FactionSpellcasterDefault copy() {
		return new FactionSpellcasterDefault(new HashMap<>(attributes), experienceDrop, canSwim, canBreakDoors, new HashMap<>(equipment), new HashMap<>(additionalAttributes), enabled, lootTable, heightRange, thinness);
	}

	private FactionSpellcasterDefault change(Consumer<FactionSpellcasterDefault> makeChange) {
		FactionSpellcasterDefault copy = copy();
		makeChange.accept(copy);
		return copy;
	}

	@Override
	public FactionSpellcasterDefault setExperienceDrop(int experienceDrop) {
		return change(def -> def.experienceDrop = experienceDrop);
	}

	@Override
	public FactionSpellcasterDefault setCanSwim(boolean canSwim) {
		return change(def -> def.canSwim = canSwim);
	}

	@Override
	public FactionSpellcasterDefault setCanBreakDoors(boolean canBreakDoors) {
		return change(def -> def.canBreakDoors = canBreakDoors);
	}

	public FactionSpellcasterDefault setEnabled(boolean enabled) {
		return change(def -> def.enabled = enabled);
	}

	@Override
	public FactionSpellcasterDefault setAttributes(Map<String, Double> additionalAttributes) {
		return change(def -> def.attributes.putAll(additionalAttributes));
	}

	@Override
	public FactionSpellcasterDefault setEquipment(Map<Integer, Item> additionalEquipment) {
		return change(def -> def.equipment.putAll(additionalEquipment));
	}

	public FactionSpellcasterDefault setAdditionalAttributes(Map<IAdditionalAttribute<?>, Object> overrides) {
		return change(def -> def.additionalAttributes.putAll(overrides));
	}

	public FactionSpellcasterDefault setLootTable(ResourceLocation lootTable) {
		return change(def -> def.lootTable = lootTable);
	}

	public FactionSpellcasterDefault setHeightRange(Range<Float> heightRange) {
		return change(def -> def.heightRange = heightRange);
	}

	public FactionSpellcasterDefault setThinness(float thinness) {
		return change(def -> def.thinness = thinness);
	}

	public void applyAdditionalAttributes(NpcFaction npc) {
		additionalAttributes.forEach(npc::setAdditionalAttribute);
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
