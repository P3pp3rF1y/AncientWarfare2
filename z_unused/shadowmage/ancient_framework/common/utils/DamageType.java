/**
   Copyright 2012 John Cummens (aka Shadowmage, Shadowmage4513)
   This software is distributed under the terms of the GNU General Public License.
   Please see COPYING for precise license information.

   This file is part of Ancient Warfare.

   Ancient Warfare is free software: you can redistribute it and/or modify
   it under the terms of the GNU General Public License as published by
   the Free Software Foundation, either version 3 of the License, or
   (at your option) any later version.

   Ancient Warfare is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   GNU General Public License for more details.

   You should have received a copy of the GNU General Public License
   along with Ancient Warfare.  If not, see <http://www.gnu.org/licenses/>.
 */
package shadowmage.ancient_framework.common.utils;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ChatMessageComponent;
import net.minecraft.util.DamageSource;

public class DamageType extends DamageSource
{

Entity ent;

public static DamageType fireMissile = (DamageType) new DamageType("dmg.firemissile").setFireDamage().setProjectile();
public static DamageType explosiveMissile = (DamageType) new DamageType("dmg.explosivemissile").setFireDamage().setProjectile();
public static DamageType genericMissile = (DamageType) new DamageType("dmg.genericmissile").setProjectile();
public static DamageType piercingMissile = (DamageType) new DamageType("dmg.piercingmissile").setDamageBypassesArmor().setProjectile();
public static DamageType batteringDamage = (DamageType) new DamageType("dmg.battering");

/**
 * @param par1Str
 */
protected DamageType(String par1Str)
  {
  super(par1Str);
  }

protected DamageType(String type, Entity source)
  {
  super(type);
  this.ent = source;
  }

@Override
public Entity getEntity()
  {
  return ent;
  }

public static DamageSource causeEntityMissileDamage(Entity attacker , boolean fire, boolean expl)
  {
  DamageType t = new DamageType("AWMissile", attacker);
  t.setProjectile();
  if(fire)
    {    
    t.setFireDamage();
    }
  if(expl)
    {
    t.setExplosion();
    }
  return t;
  }

@Override
public ChatMessageComponent getDeathMessage(EntityLivingBase par1EntityLiving)
  {
  EntityLivingBase entityliving1 = par1EntityLiving.func_94060_bK();
  String name = entityliving1==null? "No Entity" : entityliving1.getEntityName();
  ChatMessageComponent chat = ChatMessageComponent.createFromText(name + " was killed by Missile Damage");
  return chat;
  }


}
