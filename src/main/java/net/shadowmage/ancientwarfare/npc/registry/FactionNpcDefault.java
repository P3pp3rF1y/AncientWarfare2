package net.shadowmage.ancientwarfare.npc.registry;

import jdk.nashorn.internal.ir.annotations.Immutable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.shadowmage.ancientwarfare.core.util.parsing.ResourceLocationMatcher;
import net.shadowmage.ancientwarfare.npc.entity.NpcBase;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Immutable
public class FactionNpcDefault {
	private final String faction;
	private final String npcSubtype;
	private final Set<ResourceLocationMatcher> targetList;
	private final Map<String, Double> attributes;
	private final int experienceDrop;
	private final boolean canSwim;
	private final boolean canBreakDoors;
	private final Map<Integer, Item> equipment;

	public FactionNpcDefault(Set<ResourceLocationMatcher> targetList, Map<String, Double> attributes, int experienceDrop, boolean canSwim, boolean canBreakDoors, Map<Integer, Item> equipment) {
		this("", "", targetList, attributes, experienceDrop, canSwim, canBreakDoors, equipment);
	}

	@SuppressWarnings("squid:S00107")
	private FactionNpcDefault(String faction, String npcSubType, Set<ResourceLocationMatcher> targetList, Map<String, Double> attributes,
			int experienceDrop, boolean canSwim, boolean canBreakDoors, Map<Integer, Item> equipment) {
		this.targetList = targetList;
		this.attributes = attributes;
		this.experienceDrop = experienceDrop;
		this.canSwim = canSwim;
		this.canBreakDoors = canBreakDoors;
		this.faction = faction;
		this.npcSubtype = npcSubType;
		this.equipment = equipment;
	}

	public FactionNpcDefault setFaction(String faction) {
		return new FactionNpcDefault(faction, npcSubtype, targetList, attributes, experienceDrop, canSwim, canBreakDoors, equipment);
	}

	public FactionNpcDefault setNpcSubtype(String npcSubtype) {
		return new FactionNpcDefault(faction, npcSubtype, targetList, attributes, experienceDrop, canSwim, canBreakDoors, equipment);
	}

	public FactionNpcDefault setExperienceDrop(int experienceDrop) {
		return new FactionNpcDefault(faction, npcSubtype, targetList, attributes, experienceDrop, canSwim, canBreakDoors, equipment);
	}

	public FactionNpcDefault setCanSwim(boolean canSwim) {
		return new FactionNpcDefault(faction, npcSubtype, targetList, attributes, experienceDrop, canSwim, canBreakDoors, equipment);
	}

	public FactionNpcDefault setCanBreakDoors(boolean canBreakDoors) {
		return new FactionNpcDefault(faction, npcSubtype, targetList, attributes, experienceDrop, canSwim, canBreakDoors, equipment);
	}

	public FactionNpcDefault addTargets(Set<ResourceLocationMatcher> additionalTargetList) {
		Set<ResourceLocationMatcher> newTargetList = new HashSet<>();
		newTargetList.addAll(this.targetList);
		newTargetList.addAll(additionalTargetList);
		return new FactionNpcDefault(faction, npcSubtype, newTargetList, attributes, experienceDrop, canSwim, canBreakDoors, equipment);
	}

	public FactionNpcDefault setAttributes(Map<String, Double> additionalAttributes) {
		Map<String, Double> newAttributes = new HashMap<>();
		newAttributes.putAll(this.attributes);
		newAttributes.putAll(additionalAttributes);
		return new FactionNpcDefault(faction, npcSubtype, targetList, newAttributes, experienceDrop, canSwim, canBreakDoors, equipment);
	}

	public FactionNpcDefault setEquipment(Map<Integer, Item> additionalEquipment) {
		Map<Integer, Item> newEquipment = new HashMap<>();
		newEquipment.putAll(equipment);
		newEquipment.putAll(additionalEquipment);
		return new FactionNpcDefault(faction, npcSubtype, targetList, attributes, experienceDrop, canSwim, canBreakDoors, newEquipment);
	}

	public boolean isTarget(Entity entity) {
		//noinspection ConstantConditions
		return EntityRegistry.getEntry(entity.getClass()) != null
				&& targetList.stream().anyMatch(m -> m.test(EntityRegistry.getEntry(entity.getClass()).getRegistryName()));
	}

	public int getExperienceDrop() {
		return experienceDrop;
	}

	public double getBaseHealth() {
		String healthKey = SharedMonsterAttributes.MAX_HEALTH.getName();
		return (attributes.containsKey(healthKey)) ? attributes.get(healthKey) : 0;
	}

	public double getBaseAttack() {
		String attackKey = SharedMonsterAttributes.ATTACK_DAMAGE.getName();
		return (attributes.containsKey(attackKey)) ? attributes.get(attackKey) : 0;
	}

	public void applyAttributes(NpcBase npc) {
		attributes.forEach((name, value) -> applyAttribute(npc, name, value));
	}

	private void applyAttribute(NpcBase npc, String attributeName, double baseValue) {
		IAttributeInstance attribute = npc.getAttributeMap().getAttributeInstanceByName(attributeName);
		if (attribute != null) {
			attribute.setBaseValue(baseValue);
		}
	}

	public void applyPathSettings(PathNavigateGround navigator) {
		navigator.setCanSwim(canSwim);
		navigator.setBreakDoors(canBreakDoors);
	}

	public void applyEquipment(NpcBase npc) {
		equipment.forEach((slot, item) -> npc.setItemStackToSlot(slot, new ItemStack(item)));
	}
}
