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

package net.shadowmage.ancientwarfare.vehicle.missiles;

import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.vehicle.entity.VehicleBase;

/**
 * interfaced used by ammo types, implemented for possible future API use and
 * ease of future expansion without necessitating extension/inheritance
 *
 * @author Shadowmage
 */
public interface IAmmo {

	ResourceLocation getRegistryName();

	int getEntityDamage();

	int getVehicleDamage();

	String getConfigName();

	boolean isEnabled();

	void setEnabled(boolean val);

	void setEntityDamage(int damage);

	void setVehicleDamage(int damage);

	ResourceLocation getModelTexture();//get the display texture

	IAmmo getSecondaryAmmoType();//if this is just a 'container' ammo, get the contained type

	int getSecondaryAmmoTypeCount();//get the contained qty of what this ammo represents (used by cluster/grapeshot)

	boolean hasSecondaryAmmo();

	boolean isFlaming();//used by client-side rendering to render the missile on-fire, does nothing else

	boolean isAmmoValidFor(VehicleBase vehicle);//can be used for per-upgrade compatibility.  vehicle will check this before firing or adding ammo to the vehicle

	boolean updateAsArrow();//should update pitch like an arrow (relative to flight direction)

	boolean isRocket();//determines flight characteristics

	boolean isPersistent();//should die on impact, or stay on ground(arrows)

	boolean isPenetrating();//if persistent, and penetrating==true, will not bounce off of stuff, but instead go through it (heavy projectiles)

	boolean isProximityAmmo();//should detonate when coming CLOSE to something? (range for entity/ground set below)

	boolean isAvailableAsItem();

	float entityProximity();

	float groundProximity();

	float getGravityFactor();//statically set..should techincally be depricated in favor of a const

	float getAmmoWeight();//weight of the missile in KG

	float getRenderScale();//get relative render scale of the ammo compared to the model default scale...(varies per ammo/model)

	void onImpactWorld(World world, float x, float y, float z, MissileBase missile, RayTraceResult hit);//called when the entity impacts a world block

	void onImpactEntity(World world, Entity ent, float x, float y, float z, MissileBase missile);//called when the entity impacts another entity
}
