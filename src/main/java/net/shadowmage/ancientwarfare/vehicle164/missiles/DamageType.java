/**
 * Copyright 2012 John Cummens (aka Shadowmage, Shadowmage4513)
 * This software is distributed under the terms of the GNU General Public License.
 * Please see COPYING for precise license information.
 * <p>
 * This file is part of Ancient Warfare.
 * <p>
 * Ancient Warfare is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * Ancient Warfare is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with Ancient Warfare.  If not, see <http://www.gnu.org/licenses/>.
 */

package shadowmage.ancient_warfare.common.vehicles.missiles;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ChatMessageComponent;
import net.minecraft.util.DamageSource;
import net.minecraft.util.StatCollector;

public class DamageType extends DamageSource {

	Entity ent;

	public static DamageType fireMissile = (DamageType) new DamageType("dmg.firemissile").setFireDamage().setProjectile();
	public static DamageType explosiveMissile = (DamageType) new DamageType("dmg.explosivemissile").setFireDamage().setProjectile();
	public static DamageType genericMissile = (DamageType) new DamageType("dmg.genericmissile").setProjectile();
	public static DamageType piercingMissile = (DamageType) new DamageType("dmg.piercingmissile").setDamageBypassesArmor().setProjectile();
	public static DamageType batteringDamage = (DamageType) new DamageType("dmg.battering");

	/**
	 * @param par1Str
	 */
	protected DamageType(String par1Str) {
		super(par1Str);
	}

	protected DamageType(String type, Entity source) {
		super(type);
		this.ent = source;
	}

	@Override
	public Entity getEntity() {
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
	public ChatMessageComponent getDeathMessage(EntityLivingBase par1EntityLivingBase) {
		EntityLivingBase entitylivingbase1 = par1EntityLivingBase.func_94060_bK();
		String s = "death.attack." + this.damageType;
		String s1 = s + ".player";
		return entitylivingbase1 != null && StatCollector.func_94522_b(s1) ? ChatMessageComponent.createFromTranslationWithSubstitutions(s1,
				new Object[] {par1EntityLivingBase.getTranslatedEntityName(), entitylivingbase1.getTranslatedEntityName()}) : ChatMessageComponent
				.createFromTranslationWithSubstitutions(s, new Object[] {par1EntityLivingBase.getTranslatedEntityName()});
	}

}
