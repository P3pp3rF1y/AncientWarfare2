package net.shadowmage.ancientwarfare.npc.item;

import com.google.common.collect.Multimap;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.shadowmage.ancientwarfare.npc.AncientWarfareNPC;

public class ItemSickle extends ItemBaseNPC {
	private final Item.ToolMaterial material;

	private final float attackDamage;
	protected double attackSpeed;


	public ItemSickle(ToolMaterial material, double attackSpeed) {
		super("sickle");
		this.material = material;
		this.maxStackSize = 1;
		this.setMaxDamage(material.getMaxUses());
		this.attackDamage = material.getAttackDamage();
		this.attackSpeed = attackSpeed;
		setUnlocalizedName("sickle");
		setCreativeTab(AncientWarfareNPC.TAB);
	}

	public float getAttackDamage()
	{
		return this.material.getAttackDamage();
	}

	public int getItemEnchantability()
	{
		return this.material.getEnchantability();
	}

	@Override
	public Multimap<String, AttributeModifier> getItemAttributeModifiers(EntityEquipmentSlot equipmentSlot)
	{
		Multimap<String, AttributeModifier> multimap = super.getItemAttributeModifiers(equipmentSlot);

		if (equipmentSlot == EntityEquipmentSlot.MAINHAND)
		{
			multimap.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Weapon modifier", (double)this.attackDamage, 0));
			multimap.put(SharedMonsterAttributes.ATTACK_SPEED.getName(), new AttributeModifier(ATTACK_SPEED_MODIFIER, "Weapon modifier", attackSpeed, 0));
		}
		return multimap;
	}
}
