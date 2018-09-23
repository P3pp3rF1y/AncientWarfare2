package net.shadowmage.ancientwarfare.npc.registry;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathNavigateGround;
import net.shadowmage.ancientwarfare.npc.entity.NpcBase;

import javax.annotation.concurrent.Immutable;
import java.util.HashMap;
import java.util.Map;

@Immutable
public class NpcDefault {
	protected final Map<String, Double> attributes;
	protected final int experienceDrop;
	protected final boolean canSwim;
	protected final boolean canBreakDoors;
	protected final Map<Integer, Item> equipment;

	public NpcDefault(Map<String, Double> attributes, int experienceDrop, boolean canSwim, boolean canBreakDoors, Map<Integer, Item> equipment) {
		this.attributes = attributes;
		this.experienceDrop = experienceDrop;
		this.canSwim = canSwim;
		this.canBreakDoors = canBreakDoors;
		this.equipment = equipment;
	}

	public NpcDefault setExperienceDrop(int experienceDrop) {
		return new NpcDefault(attributes, experienceDrop, canSwim, canBreakDoors, equipment);
	}

	public NpcDefault setCanSwim(boolean canSwim) {
		return new NpcDefault(attributes, experienceDrop, canSwim, canBreakDoors, equipment);
	}

	public NpcDefault setCanBreakDoors(boolean canBreakDoors) {
		return new NpcDefault(attributes, experienceDrop, canSwim, canBreakDoors, equipment);
	}

	public NpcDefault setAttributes(Map<String, Double> additionalAttributes) {
		Map<String, Double> newAttributes = new HashMap<>();
		newAttributes.putAll(this.attributes);
		newAttributes.putAll(additionalAttributes);
		return new NpcDefault(newAttributes, experienceDrop, canSwim, canBreakDoors, equipment);
	}

	public NpcDefault setEquipment(Map<Integer, Item> additionalEquipment) {
		Map<Integer, Item> newEquipment = new HashMap<>();
		newEquipment.putAll(equipment);
		newEquipment.putAll(additionalEquipment);
		return new NpcDefault(attributes, experienceDrop, canSwim, canBreakDoors, newEquipment);
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

			if (attribute.getAttribute() == SharedMonsterAttributes.MAX_HEALTH) {
				npc.setHealth((float) baseValue);
			}
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
