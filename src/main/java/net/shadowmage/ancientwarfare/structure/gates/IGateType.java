/*
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

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.shadowmage.ancientwarfare.structure.entity.EntityGate;

public interface IGateType {

	/*
	 * return global ID -- used to link gate type to item
	 * determines render type and model used
	 */
	int getGlobalID();

	/*
	 * return the name to register for the spawning item
	 */
	String getDisplayName();

	/*
	 * return the tooltip to register for the spawning item
	 */
	String getTooltip();

	/*
	 * return the texture that should be used for rendering
	 */
	ResourceLocation getTexture();

	/*
	 * return the speed at which the gate opens/closes when activated
	 */
	float getMoveSpeed();

	/*
	 * return the max health of this gate
	 */
	int getMaxHealth();

	/*
	 * a callback from the entity to the gate-type to allow for
	 * gate-type specific checks during updates
	 */
	void onUpdate(EntityGate ent);

	/*
	 * called from setPosition to update gates bounding box
	 */
	void setCollisionBoundingBox(EntityGate gate);

	void onGateStartOpen(EntityGate gate);

	void onGateFinishOpen(EntityGate gate);

	void onGateStartClose(EntityGate gate);

	void onGateFinishClose(EntityGate gate);

	void setInitialBounds(EntityGate gate, BlockPos pos1, BlockPos pos2);

	/*
	 * a callback from the spawning item for validation of a chosen
	 * pair of spawning points.  This is where the gate can reject
	 * a starting position/setup if the points are not placed correctly.
	 */
	boolean arePointsValidPair(BlockPos pos1, BlockPos pos2);

	boolean canActivate(EntityGate gate, boolean open);

	boolean canSoldierActivate();

	int getModelType();

	ItemStack getConstructingItem();

	ItemStack getDisplayStack();

}
