// TODO likely just delete when gate is converted over to block
// /*
// Copyright 2015 Olivier Sylvain (aka GotoLink)
// This software is distributed under the terms of the GNU General Public License.
// Please see COPYING for precise license information.
//
// This file is part of Ancient Warfare.
//
// Ancient Warfare is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// Ancient Warfare is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with Ancient Warfare.  If not, see <http://www.gnu.org/licenses/>.
// */
//package net.shadowmage.ancientwarfare.structure.entity;
//
//import net.minecraft.util.math.AxisAlignedBB;
//import net.minecraft.util.math.BlockPos;
//import net.minecraft.util.math.RayTraceResult;
//import net.minecraft.util.math.Vec3d;
//
//public class DualBoundingBox extends AxisAlignedBB{
//
//    private double yOffset;
//    public DualBoundingBox(BlockPos min, BlockPos max) {
//        super(min.getX(), min.getY(), min.getZ(), max.getX() + 1, max.getY() + 1, max.getZ() + 1);
//    }
//
//    public AxisAlignedBB getMin(){
//        return new AxisAlignedBB(minX, minY - yOffset, minZ, minX + 1, maxY, minZ + 1);
//    }
//
//    public AxisAlignedBB getMax(){
//        return new AxisAlignedBB(maxX - 1, minY - yOffset, maxZ - 1, maxX, maxY, maxZ);
//    }
//
//    public AxisAlignedBB getTop(){
//        return new AxisAlignedBB(minX, maxY - 1, minZ, maxX, maxY, maxZ);
//    }
//
//    private DualBoundingBox(double mX, double mY, double mZ, double MX, double MY, double MZ){
//        super(mX, mY, mZ, MX, MY, MZ);
//    }
//
//    @Override
//    public AxisAlignedBB setBounds(double pX, double pY, double pZ, double PX, double PY, double PZ) {
//        if(yOffset == 0 && pY>minY)
//            yOffset = pY - minY;
//        if(yOffset != 0 && pY<minY)
//            yOffset = 0;
//        return super.setBounds(pX, pY, pZ, PX, PY, PZ);
//    }
//
//    @Override
//    public void setBB(AxisAlignedBB bb) {
//        setBounds(bb.minX, bb.minY, bb.minZ, bb.maxX, bb.maxY, bb.maxZ);
//    }
//
//    @Override
//    public AxisAlignedBB contract(double varX, double varY, double varZ) {
//        DualBoundingBox box = (DualBoundingBox) copy();
//        box.setBB(super.contract(varX, varY, varZ));
//        return box;
//    }
//
//    @Override
//    public AxisAlignedBB getOffsetBoundingBox(double xOff, double yOff, double zOff) {
//        DualBoundingBox box = (DualBoundingBox) copy();
//        box.setBB(super.getOffsetBoundingBox(xOff, yOff, zOff));
//        return box;
//    }
//
//    @Override//Union
//    public AxisAlignedBB func_111270_a(AxisAlignedBB bb) {
//        DualBoundingBox box = (DualBoundingBox) copy();
//        box.setBB(super.func_111270_a(bb));
//        return box;
//    }
//
//    @Override
//    public AxisAlignedBB expand(double varX, double varY, double varZ) {
//        DualBoundingBox box = (DualBoundingBox) copy();
//        box.setBB(super.expand(varX, varY, varZ));
//        return box;
//    }
//
//    @Override
//    public AxisAlignedBB expand(double varX, double varY, double varZ) {
//        DualBoundingBox box = (DualBoundingBox) copy();
//        box.setBB(super.expand(varX, varY, varZ));
//        return box;
//    }
//
//    @Override
//    public boolean intersects(double x1, double y1, double z1, double x2, double y2, double z2) {
//        return intersects(getMin(), x1, y1, z1, x2, y2, z2) || intersects(getMax(), x1, y1, z1, x2, y2, z2) || intersects(getTop(), x1, y1, z1, x2, y2, z2);
//    }
//
//    private boolean intersects(AxisAlignedBB aabb, double x1, double y1, double z1, double x2, double y2, double z2) {
//        return aabb.minX < x2 && aabb.maxX > x1 && aabb.minY < y2 && aabb.maxY > y1 && aabb.minZ < z2 && aabb.maxZ > z1;
//    }
//
//    @Override
//    public boolean contains(Vec3d vec3) {
//        return getMin().contains(vec3) || getMax().contains(vec3) || getTop().contains(vec3);
//    }
//
//    @Override
//    public RayTraceResult calculateIntercept(Vec3d vec3_1, Vec3d vec3_2) {
//        return getBoundingBox(minX, minY - yOffset, minZ, maxX, maxY, maxZ).calculateIntercept(vec3_1, vec3_2);
//    }
//
//    @Override
//    public AxisAlignedBB copy() {
//        DualBoundingBox box = new DualBoundingBox(minX, minY, minZ, maxX, maxY, maxZ);
//        box.yOffset = this.yOffset;
//        return box;
//    }
//}
