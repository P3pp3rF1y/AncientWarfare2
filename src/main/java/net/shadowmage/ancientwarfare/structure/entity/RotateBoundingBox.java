/*
 Copyright 2015 Olivier Sylvain (aka GotoLink)
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

package net.shadowmage.ancientwarfare.structure.entity;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

public class RotateBoundingBox extends AxisAlignedBB {
	private final static float TO_RAD = (float) Math.PI / 180F;
	private final EnumFacing facing;
	//From vertical axis
	private float angle;

	public RotateBoundingBox(EnumFacing face, BlockPos min, BlockPos max) {
		this(face, min.getX(), min.getY(), min.getZ(), max.getX(), max.getY(), max.getZ());
	}

	private RotateBoundingBox(EnumFacing face, double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
		super(minX, minY, minZ, maxX, maxY, maxZ);
		this.facing = face;
	}

	@Override
	public boolean intersects(AxisAlignedBB mask) {
		if (mask.maxY > minY) {
			double height = MathHelper.cos(angle * TO_RAD) * (maxY - minY);
			if (mask.minY < height + minY) {
				if (facing.getAxis() == EnumFacing.Axis.Z) {//z
					if (!(mask.minX < maxX && mask.maxX > minX))
						return false;
				} else {//x
					if (!(mask.minZ < maxZ && mask.maxZ > minZ))
						return false;
				}
				double length = MathHelper.sin(angle * TO_RAD) * (maxY - minY + 1);
				switch (facing.getHorizontalIndex()) {
					case 0:
						return mask.minZ < length + minZ && mask.maxZ > minZ;
					case 1:
						return mask.maxX > maxX - length && mask.minX < maxX;
					case 2:
						return mask.maxZ > maxZ - length && mask.minZ < maxZ;
					case 3:
						return mask.minX < length + minX && mask.maxX > minX;
				}
			}
		}
		return false;
	}

	public void rotate(float increment) {
		angle += increment;
	}
}
