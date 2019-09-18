package net.shadowmage.ancientwarfare.npc.item;

import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.text.TextFormatting;

public class ItemIceSpear extends ItemExtendedReachWeapon {
	public ItemIceSpear(ToolMaterial material, String registryName, double attackOffset, double attackSpeed, float reach) {
		super(material, registryName, attackOffset, attackSpeed, reach);
	}

	@Override
	public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) {
		target.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 50));
		target.addPotionEffect(new PotionEffect(MobEffects.MINING_FATIGUE, 150));
		return true;
	}

	@Override
	public String getItemStackDisplayName(ItemStack stack) {
		return TextFormatting.AQUA + super.getItemStackDisplayName(stack) + TextFormatting.RESET;
	}
}
