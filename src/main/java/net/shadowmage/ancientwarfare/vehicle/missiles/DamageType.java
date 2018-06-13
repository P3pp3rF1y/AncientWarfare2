package net.shadowmage.ancientwarfare.vehicle.missiles;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.DamageSource;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.translation.I18n;

import javax.annotation.Nullable;

public class DamageType extends DamageSource {

	Entity ent;

	public static final DamageSource fireMissile = new DamageType("dmg.firemissile").setFireDamage().setProjectile();
	public static final DamageSource explosiveMissile = new DamageType("dmg.explosivemissile").setFireDamage().setProjectile();
	public static final DamageSource genericMissile = new DamageType("dmg.genericmissile").setProjectile();
	public static final DamageSource piercingMissile = new DamageType("dmg.piercingmissile").setDamageBypassesArmor().setProjectile();
	public static final DamageSource batteringDamage = new DamageType("dmg.battering");

	protected DamageType(String par1Str) {
		super(par1Str);
	}

	protected DamageType(String type, Entity source) {
		super(type);
		this.ent = source;
	}

	@Nullable
	@Override
	public Entity getTrueSource() {
		return ent;
	}

	public static DamageSource causeEntityMissileDamage(Entity attacker, boolean fire, boolean expl) {
		DamageType t = new DamageType("AWMissile", attacker);
		t.setProjectile();
		if (fire) {
			t.setFireDamage();
		}
		if (expl) {
			t.setExplosion();
		}
		return t;
	}

	@Override
	public ITextComponent getDeathMessage(EntityLivingBase par1EntityLivingBase) {
		EntityLivingBase entitylivingbase1 = par1EntityLivingBase.getAttackingEntity();
		String s = "death.attack." + this.damageType;
		String s1 = s + ".player";
		return entitylivingbase1 != null && I18n.canTranslate(s1) ? new TextComponentTranslation(s1, par1EntityLivingBase.getDisplayName(),
				entitylivingbase1.getDisplayName()) : new TextComponentTranslation(s, par1EntityLivingBase.getDisplayName());
	}
}
