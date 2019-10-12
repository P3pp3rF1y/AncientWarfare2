package net.shadowmage.ancientwarfare.npc.item;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;

public class ItemClub extends ItemExtendedReachWeapon {
	public ItemClub(ToolMaterial material, String registryName, double attackOffset, double attackSpeed, float reach) {
		super(material, registryName, attackOffset, attackSpeed, reach);
	}

	@Override
	public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) {
		int knockback = 1;
		float f1 = MathHelper.sqrt(target.motionX * target.motionX + target.motionZ * target.motionZ);
		target.addVelocity(target.motionX * (double) knockback * 0.6000000238418579D / (double) f1, 0.1D, target.motionZ * (double) knockback * 0.6000000238418579D / (double) f1);
		return true;
	}

	@Override
	public boolean isBookEnchantable(ItemStack stack, ItemStack book) {
		return !EnchantmentHelper.getEnchantments(book).containsKey(Enchantments.KNOCKBACK);
	}

	@Override
	public boolean canApplyAtEnchantingTable(ItemStack stack, net.minecraft.enchantment.Enchantment enchantment) {
		return enchantment != Enchantments.KNOCKBACK;
	}

	@Override
	public boolean canDisableShield(ItemStack stack, ItemStack shield, EntityLivingBase entity, EntityLivingBase attacker) {
		return true;
	}

}
