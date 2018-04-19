//TODO likely just delete when gate is converted over to block
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

import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;

public class DualBoundingBox extends AxisAlignedBB {

	private double yOffset;

	public DualBoundingBox(BlockPos min, BlockPos max) {
		super(min.getX(), min.getY(), min.getZ(), max.getX() + 1, max.getY() + 1, max.getZ() + 1);
	}

	public AxisAlignedBB getMin() {
		return new AxisAlignedBB(minX, minY - yOffset, minZ, minX + 1, maxY, minZ + 1);
	}

	public AxisAlignedBB getMax() {
		return new AxisAlignedBB(maxX - 1, minY - yOffset, maxZ - 1, maxX, maxY, maxZ);
	}

	public AxisAlignedBB getTop() {
		return new AxisAlignedBB(minX, maxY - 1, minZ, maxX, maxY, maxZ);
	}

	private DualBoundingBox(double mX, double mY, double mZ, double MX, double MY, double MZ) {
		super(mX, mY, mZ, MX, MY, MZ);
	}

	public DualBoundingBox setBB(AxisAlignedBB bb) {
		DualBoundingBox box = new DualBoundingBox(bb.minX, bb.minY, bb.minZ, bb.maxX, bb.maxY, bb.maxZ);
		box.yOffset = this.yOffset;
		return box;
	}

	@Override
	public AxisAlignedBB contract(double varX, double varY, double varZ) {
		return setBB(super.contract(varX, varY, varZ));
	}

	@Override
	public AxisAlignedBB offset(double xOff, double yOff, double zOff) {
		return setBB(super.offset(xOff, yOff, zOff));
	}

	@Override
	public AxisAlignedBB union(AxisAlignedBB other) {
		return setBB(super.union(other));
	}

	@Override
	public AxisAlignedBB grow(double varX, double varY, double varZ) {
		return setBB(super.grow(varX, varY, varZ));
	}

	@Override
	public AxisAlignedBB expand(double varX, double varY, double varZ) {
		return setBB(super.expand(varX, varY, varZ));
	}

	@Override
	public boolean intersects(double x1, double y1, double z1, double x2, double y2, double z2) {
		return intersects(getMin(), x1, y1, z1, x2, y2, z2) || intersects(getMax(), x1, y1, z1, x2, y2, z2) || intersects(getTop(), x1, y1, z1, x2, y2, z2);
	}

	private boolean intersects(AxisAlignedBB aabb, double x1, double y1, double z1, double x2, double y2, double z2) {
		return aabb.minX < x2 && aabb.maxX > x1 && aabb.minY < y2 && aabb.maxY > y1 && aabb.minZ < z2 && aabb.maxZ > z1;
	}

	@Override
	public boolean contains(Vec3d vec3) {
		return getMin().contains(vec3) || getMax().contains(vec3) || getTop().contains(vec3);
	}

	@Override
	public RayTraceResult calculateIntercept(Vec3d vec3_1, Vec3d vec3_2) {
		return new AxisAlignedBB(minX, minY - yOffset, minZ, maxX, maxY, maxZ).calculateIntercept(vec3_1, vec3_2);
	}
}
