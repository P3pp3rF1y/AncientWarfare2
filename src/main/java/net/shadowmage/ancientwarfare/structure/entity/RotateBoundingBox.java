package net.shadowmage.ancientwarfare.structure.entity;

import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.shadowmage.ancientwarfare.core.util.BlockPosition;

/**
 * Created by Olivier on 02/07/2015.
 */
public class RotateBoundingBox extends AxisAlignedBB{
    private final static float TO_RAD = (float) Math.PI / 180F;
    private final int facing;
    //From vertical axis
    private float angle;
    public RotateBoundingBox(int face, BlockPosition min, BlockPosition max) {
        this(face, min.x, min.y, min.z, max.x, max.y, max.z);
    }

    private RotateBoundingBox(int face, double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
        super(minX, minY, minZ, maxX, maxY, maxZ);
        this.facing = face;
    }

    @Override
    public boolean intersectsWith(AxisAlignedBB mask) {
        if(mask.maxY > minY) {
            double height = MathHelper.cos(angle * TO_RAD) * (maxY - minY);
            if(mask.minY < height + minY) {
                if(facing%2==0){//z
                    if(!(mask.minX < maxX && mask.maxX > minX))
                        return false;
                }else{//x
                    if(!(mask.minZ < maxZ && mask.maxZ > minZ))
                        return false;
                }
                double length = MathHelper.sin(angle * TO_RAD) * (maxY - minY + 1);
                switch (facing) {
                    case 0://z++
                        return mask.minZ < length + minZ && mask.maxZ > minZ;
                    case 1://x--
                        return mask.maxX > maxX - length && mask.minX < maxX;
                    case 2://z--
                        return mask.maxZ > maxZ - length && mask.minZ < maxZ;
                    case 3://x++
                        return mask.minX < length + minX && mask.maxX > minX;
                }
            }
        }
        return false;
    }

    @Override
    public AxisAlignedBB copy() {
        RotateBoundingBox box = new RotateBoundingBox(facing, minX, minY, minZ, maxX, maxY, maxZ);
        box.angle = this.angle;
        return box;
    }

    public void rotate(float increment){
        angle += increment;
    }
}
