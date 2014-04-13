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
package net.shadowmage.ancientwarfare.structure.gates;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.shadowmage.ancientwarfare.core.util.BlockPosition;
import net.shadowmage.ancientwarfare.structure.entity.EntityGate;

public interface IGateType
{

/**
 * return global ID -- used to link gate type to item
 * determines render type and model used
 * @return
 */
public int getGlobalID();

/**
 * return the name to register for the spawning item
 * @return
 */
public String getDisplayName();

/**
 * return the tooltip to register for the spawning item
 * @return
 */
public String getTooltip();

/**
 * return the texture that should be used for rendering
 * @return
 */
public ResourceLocation getTexture();

public IIcon getIconTexture();

public void registerIcons(IIconRegister reg);

/**
 * return the speed at which the gate opens/closes when activated
 * @return
 */
public float getMoveSpeed();

/**
 * return the max health of this gate
 * @return
 */
public int getMaxHealth();

/**
 * a callback from the entity to the gate-type to allow for
 * gate-type specific checks during updates
 * @param ent
 */
public void onUpdate(EntityGate ent);

/**
 * called from setPosition to update gates bounding box
 * @param gate
 */
public void setCollisionBoundingBox(EntityGate gate);

public void onGateStartOpen(EntityGate gate);
public void onGateFinishOpen(EntityGate gate);
public void onGateStartClose(EntityGate gate);
public void onGateFinishClose(EntityGate gate);
  
public void setInitialBounds(EntityGate gate, BlockPosition pos1, BlockPosition pos2);
/**
 * a callback from the spawning item for validation of a chosen
 * pair of spawning points.  This is where the gate can reject
 * a starting position/setup if the points are not placed correctly.
 * @param pos1
 * @param pos2
 * @return
 */
public boolean arePointsValidPair(BlockPosition pos1, BlockPosition pos2);

public boolean canActivate(EntityGate gate, boolean open);

public boolean canSoldierActivate();

public int getModelType();

public ItemStack getConstructingItem();

public ItemStack getDisplayStack();

}
