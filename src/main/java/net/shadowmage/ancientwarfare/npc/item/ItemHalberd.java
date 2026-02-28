package net.shadowmage.ancientwarfare.npc.item;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;

public class ItemHalberd extends ItemExtendedReachWeapon {
	public ItemHalberd(ToolMaterial material, String registryName, double attackOffset, double attackSpeed, float reach) {
		super(material, registryName, attackOffset, attackSpeed, reach);
	}

	/**
	 * Can this Item disable a shield
	 * @param stack The ItemStack
	 * @param shield The shield in question
	 * @param entity The EntityLivingBase holding the shield
	 * @param attacker The EntityLivingBase holding the ItemStack
	 * @retrun True if this ItemStack can disable the shield in question.
	 */
	@Override
	public boolean canDisableShield(ItemStack stack, ItemStack shield, EntityLivingBase entity, EntityLivingBase attacker) {
		return true;
	}
}
