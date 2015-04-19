package net.shadowmage.ancientwarfare.structure.entity;

import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.shadowmage.ancientwarfare.core.util.BlockPosition;

/**
 * Created by Olivier on 17/04/2015.
 */
public class DualBoundingBox extends AxisAlignedBB{

    private double yOffset;
    public DualBoundingBox(BlockPosition min, BlockPosition max) {
        super(min.x, min.y, min.z, max.x + 1, max.y + 1, max.z + 1);
    }

    private AxisAlignedBB getMin(){
        return getBoundingBox(minX, minY - yOffset, minZ, minX + 1, maxY, minZ + 1);
    }

    private AxisAlignedBB getMax(){
        return getBoundingBox(maxX - 1, minY - yOffset, maxZ - 1, maxX, maxY, maxZ);
    }

    private AxisAlignedBB getTop(){
        return getBoundingBox(minX, maxY - 1, minZ, maxX, maxY, maxZ);
    }

    private DualBoundingBox(double mX, double mY, double mZ, double MX, double MY, double MZ){
        super(mX, mY, mZ, MX, MY, MZ);
    }

    @Override
    public AxisAlignedBB setBounds(double pX, double pY, double pZ, double PX, double PY, double PZ) {
        if(yOffset == 0 && pY>minY)
            yOffset = pY - minY;
        if(yOffset != 0 && pY<minY)
            yOffset = 0;
        return super.setBounds(pX, pY, pZ, PX, PY, PZ);
    }

    @Override
    public void setBB(AxisAlignedBB bb) {
        setBounds(bb.minX, bb.minY, bb.minZ, bb.maxX, bb.maxY, bb.maxZ);
    }

    @Override
    public AxisAlignedBB contract(double varX, double varY, double varZ) {
        DualBoundingBox box = (DualBoundingBox) copy();
        box.setBB(super.contract(varX, varY, varZ));
        return box;
    }

    @Override
    public AxisAlignedBB getOffsetBoundingBox(double xOff, double yOff, double zOff) {
        DualBoundingBox box = (DualBoundingBox) copy();
        box.setBB(super.getOffsetBoundingBox(xOff, yOff, zOff));
        return box;
    }

    @Override//Union
    public AxisAlignedBB func_111270_a(AxisAlignedBB bb) {
        DualBoundingBox box = (DualBoundingBox) copy();
        box.setBB(super.func_111270_a(bb));
        return box;
    }

    @Override
    public AxisAlignedBB expand(double varX, double varY, double varZ) {
        DualBoundingBox box = (DualBoundingBox) copy();
        box.setBB(super.expand(varX, varY, varZ));
        return box;
    }

    @Override
    public AxisAlignedBB addCoord(double varX, double varY, double varZ) {
        DualBoundingBox box = (DualBoundingBox) copy();
        box.setBB(super.addCoord(varX, varY, varZ));
        return box;
    }

    @Override
    public boolean intersectsWith(AxisAlignedBB mask) {
        return getMin().intersectsWith(mask) || getMax().intersectsWith(mask) || getTop().intersectsWith(mask);
    }

    @Override
    public boolean isVecInside(Vec3 vec3) {
        return getMin().isVecInside(vec3) || getMax().isVecInside(vec3) || getTop().isVecInside(vec3);
    }

    @Override
    public MovingObjectPosition calculateIntercept(Vec3 vec3_1, Vec3 vec3_2) {
        return getBoundingBox(minX, minY - yOffset, minZ, maxX, maxY, maxZ).calculateIntercept(vec3_1, vec3_2);
    }

    @Override
    public AxisAlignedBB copy() {
        DualBoundingBox box = new DualBoundingBox(minX, minY, minZ, maxX, maxY, maxZ);
        box.yOffset = this.yOffset;
        return box;
    }
}
