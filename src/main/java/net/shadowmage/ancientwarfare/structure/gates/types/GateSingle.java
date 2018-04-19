/*
 Copyright 2015 John Cummens (aka Shadowmage, Shadowmage4513)
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

package net.shadowmage.ancientwarfare.structure.gates.types;

import net.minecraft.util.math.BlockPos;
import net.shadowmage.ancientwarfare.core.util.BlockTools;
import net.shadowmage.ancientwarfare.structure.entity.EntityGate;

public class GateSingle extends Gate {
	/*
	 * @param id
	 * @param textureLocation
	 */
	public GateSingle(int id, String textureLocation) {
		super(id, textureLocation);
	}

	@Override
	public void setInitialBounds(EntityGate gate, BlockPos pos1, BlockPos pos2) {
		BlockPos min = BlockTools.getMin(pos1, pos2);
		BlockPos max = BlockTools.getMax(pos1, pos2);
		boolean wideOnXAxis = min.getX() != max.getX();
		float width = wideOnXAxis ? max.getX() - min.getX() + 1 : max.getZ() - min.getZ() + 1;
		float xOffset = wideOnXAxis ? width * 0.5f : 0.5f;
		float zOffset = wideOnXAxis ? 0.5f : width * 0.5f;
		gate.pos1 = pos1;
		gate.pos2 = pos2;
		gate.edgeMax = width;
		gate.setPosition(min.getX() + xOffset, min.getY(), min.getZ() + zOffset);
	}
}
