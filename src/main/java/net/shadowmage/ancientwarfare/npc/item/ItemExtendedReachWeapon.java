package net.shadowmage.ancientwarfare.npc.item;

import com.google.common.collect.Multimap;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.shadowmage.ancientwarfare.core.proxy.IClientRegister;
import net.shadowmage.ancientwarfare.core.util.ModelLoaderHelper;
import net.shadowmage.ancientwarfare.npc.AncientWarfareNPC;

public class ItemExtendedReachWeapon extends ItemSword implements IClientRegister, IExtendedReachWeapon {
	private final double attackDamage;
	private double attackSpeed;
	private float reach;

	public ItemExtendedReachWeapon(ToolMaterial material, String registryName, double attackOffset, double attackSpeed, float reach) {
		super(material);
		this.reach = reach;
		setUnlocalizedName(registryName);
		setRegistryName(new ResourceLocation(AncientWarfareNPC.MOD_ID, registryName));
		setCreativeTab(AncientWarfareNPC.TAB);

		AncientWarfareNPC.proxy.addClientRegister(this);

		attackDamage = material.getAttackDamage() + attackOffset;
		this.attackSpeed = attackSpeed;
	}

	@Override
	public Multimap<String, AttributeModifier> getAttributeModifiers(EntityEquipmentSlot slot, ItemStack stack) {
		Multimap<String, AttributeModifier> multimap = super.getItemAttributeModifiers(slot);

		if (slot == EntityEquipmentSlot.MAINHAND) {
			multimap.removeAll(SharedMonsterAttributes.ATTACK_DAMAGE.getName());
			multimap.removeAll(SharedMonsterAttributes.ATTACK_SPEED.getName());
			multimap.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Weapon modifier", attackDamage, 0));
			multimap.put(SharedMonsterAttributes.ATTACK_SPEED.getName(), new AttributeModifier(ATTACK_SPEED_MODIFIER, "Weapon modifier", attackSpeed, 0));
		}

		return multimap;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerClient() {
		ModelLoaderHelper.registerItem(this, "npc");
	}

	@Override
	public float getReach() {
		return reach;
	}
}
