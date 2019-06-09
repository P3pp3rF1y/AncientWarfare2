package net.shadowmage.ancientwarfare.npc.registry;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathNavigateGround;
import net.shadowmage.ancientwarfare.npc.entity.NpcBase;

import javax.annotation.concurrent.Immutable;
import java.util.Map;

@Immutable
public abstract class NpcDefault {
	protected Map<String, Double> attributes;
	protected int experienceDrop;
	protected boolean canSwim;
	protected boolean canBreakDoors;
	protected Map<Integer, Item> equipment;

	public NpcDefault(Map<String, Double> attributes, int experienceDrop, boolean canSwim, boolean canBreakDoors, Map<Integer, Item> equipment) {
		this.attributes = attributes;
		this.experienceDrop = experienceDrop;
		this.canSwim = canSwim;
		this.canBreakDoors = canBreakDoors;
		this.equipment = equipment;
	}

	public abstract NpcDefault setExperienceDrop(int experienceDrop);

	public abstract NpcDefault setCanSwim(boolean canSwim);

	public abstract NpcDefault setCanBreakDoors(boolean canBreakDoors);

	public abstract NpcDefault setAttributes(Map<String, Double> additionalAttributes);

	public abstract NpcDefault setEquipment(Map<Integer, Item> additionalEquipment);

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
