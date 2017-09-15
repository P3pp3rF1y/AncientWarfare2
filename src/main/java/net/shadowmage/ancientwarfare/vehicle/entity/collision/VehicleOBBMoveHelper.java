package net.shadowmage.ancientwarfare.vehicle.entity.collision;

import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.vehicle.collision.OBB;
import net.shadowmage.ancientwarfare.vehicle.entity.VehicleBase;

import java.util.List;

/*
 * Base OBB Movement helper.<br>
 * Responsible for updating position of the vehicle.<br>
 * Has a single OBB to represent the main collision box of the vehicle<br>
 *
 * @author John
 */
public class VehicleOBBMoveHelper {

    private VehicleBase vehicle;
    private OBB orientedBoundingBox;

    public VehicleOBBMoveHelper(VehicleBase vehicle) {
        this.vehicle = vehicle;
        orientedBoundingBox = new OBB(vehicle.vehicleWidth, vehicle.vehicleHeight, vehicle.vehicleLength);
        orientedBoundingBox.setRotation(-0);
        orientedBoundingBox.setAABBToOBBExtents(vehicle);
    }

    public void update() {
        orientedBoundingBox.updateForPositionAndRotation(vehicle.posX, vehicle.posY, vehicle.posZ, vehicle.rotationYaw);
    }


    public void moveVehicle(double x, double y, double z) {
        AxisAlignedBB boundingBox = vehicle.getEntityBoundingBox();
        World world = vehicle.world;
        float rotationYaw = vehicle.rotationYaw;
        float stepHeight = vehicle.stepHeight;
        if (Math.abs(x) < 0.001d) {
            x = 0.d;
        }
        if (Math.abs(z) < 0.001d) {
            z = 0.d;
        }
        orientedBoundingBox.updateForPositionAndRotation(vehicle.posX, vehicle.posY, vehicle.posZ, rotationYaw);
        orientedBoundingBox.setAABBToOBBExtents(vehicle);
        List<AxisAlignedBB> aabbs = world.getCollisionBoxes(vehicle, boundingBox.expand(Math.abs(x) + 0.2d, Math.abs(y) + stepHeight + 0.2d, Math.abs(z) + 0.2d));
        //first do Y movement test, use basic OBB vs bbs test, move downard if not collided
        double xMove, yMove, zMove;

        yMove = y < 0 ? getYNegativeMove(y, aabbs) : y > 0 ? getYPositiveMove(y, aabbs) : 0;
        if (yMove != 0) {
            vehicle.setPosition(vehicle.posX, vehicle.posY + yMove, vehicle.posZ);
            orientedBoundingBox.updateForPositionAndRotation(vehicle.posX, vehicle.posY, vehicle.posZ, rotationYaw);
            orientedBoundingBox.setAABBToOBBExtents(vehicle);
        }
        xMove = getXmove(x, aabbs);
        if (xMove != 0) {
            vehicle.setPosition(vehicle.posX + xMove, vehicle.posY, vehicle.posZ);
            orientedBoundingBox.updateForPositionAndRotation(vehicle.posX, vehicle.posY, vehicle.posZ, rotationYaw);
            orientedBoundingBox.setAABBToOBBExtents(vehicle);
        }
        zMove = getZMove(z, aabbs);
        if (zMove != 0) {
            vehicle.setPosition(vehicle.posX, vehicle.posY, vehicle.posZ + zMove);
            orientedBoundingBox.updateForPositionAndRotation(vehicle.posX, vehicle.posY, vehicle.posZ, rotationYaw);
            orientedBoundingBox.setAABBToOBBExtents(vehicle);
        }

        if (stepHeight > 0 && yMove <= 0 && (x != xMove || z != zMove))//attempt to step upwards by step-height
        {
            //remainder of movement for x and z axes
            double mx = x - xMove;
            double mz = z - zMove;
            orientedBoundingBox.updateForPositionAndRotation(vehicle.posX + mx, vehicle.posY, vehicle.posZ + mz, rotationYaw);
            orientedBoundingBox.setAABBToOBBExtents(vehicle);
            double my = getYStepHeight(aabbs);
            if (my > 0) {
                vehicle.setPosition(vehicle.posX + mx, vehicle.posY + my, vehicle.posZ + mz);
            }
            orientedBoundingBox.updateForPositionAndRotation(vehicle.posX, vehicle.posY, vehicle.posZ, rotationYaw);
            orientedBoundingBox.setAABBToOBBExtents(vehicle);
        }
    }


    public void rotateVehicle(float rotationDelta) {
        World world = vehicle.world;
        orientedBoundingBox.updateForPositionAndRotation(vehicle.posX, vehicle.posY, vehicle.posZ, vehicle.rotationYaw + rotationDelta);
        orientedBoundingBox.setAABBToOBBExtents(vehicle);

        Vec3d mtvTempBase = new Vec3d(0, 0, 0);
        Vec3d mtvTemp = null;
        Vec3d mtv = null;

        List<AxisAlignedBB> aabbs = world.getCollisionBoxes(vehicle, vehicle.getEntityBoundingBox().expand(0.2d, 0, 0.2d));

        AxisAlignedBB bb = null;
        int len = aabbs.size();
        for (int i = 0; i < len; i++) {
            bb = aabbs.get(i);
            mtvTemp = orientedBoundingBox.getMinCollisionVector(bb, mtvTempBase);
            if (mtvTemp != null) {
                if (mtv == null) {
                    mtv = new Vec3d(mtvTemp.x, 0, mtvTemp.z);
                } else {
                    if (Math.abs(mtvTemp.x) > Math.abs(mtv.x)) {
                        mtv = new Vec3d(mtvTemp.x, mtv.y, mtv.z);
                    }
                    if (Math.abs(mtvTemp.z) > Math.abs(mtv.z)) {
                        mtv = new Vec3d(mtv.x, mtv.y, mtvTemp.z);
                    }
                }
            }
        }

        if (mtv == null)//set position from move as there is no collision
        {
            vehicle.rotationYaw += rotationDelta;
        } else {
            mtv = new Vec3d(mtv.x * 1.1d, mtv.y, mtv.z * 1.1d);
            vehicle.setPosition(vehicle.posX + mtv.x, vehicle.posY, vehicle.posZ + mtv.z);
            orientedBoundingBox.updateForPositionAndRotation(vehicle.posX, vehicle.posY, vehicle.posZ, vehicle.rotationYaw + rotationDelta);
            orientedBoundingBox.setAABBToOBBExtents(vehicle);
            aabbs = world.getCollisionBoxes(vehicle, vehicle.getEntityBoundingBox().expand(0.2d, 0, 0.2d));
            bb = null;
            len = aabbs.size();
            mtvTemp = null;
            for (int i = 0; i < len; i++) {
                bb = aabbs.get(i);
                mtvTemp = orientedBoundingBox.getMinCollisionVector(bb, mtvTempBase);
                if (mtvTemp != null) {
                    orientedBoundingBox.updateForRotation(vehicle.rotationYaw);
                    orientedBoundingBox.updateForPosition(vehicle.posX, vehicle.posY, vehicle.posZ);
                    orientedBoundingBox.setAABBToOBBExtents(vehicle);
                    break;
                }
            }
            if (mtvTemp == null)//slide was good
            {
                vehicle.rotationYaw += rotationDelta;
                vehicle.setPosition(vehicle.posX, vehicle.posY, vehicle.posZ);
                orientedBoundingBox.updateForPositionAndRotation(vehicle.posX, vehicle.posY, vehicle.posZ, vehicle.rotationYaw);
                orientedBoundingBox.setAABBToOBBExtents(vehicle);
            } else//slide was no good, revert (do not rotate at all)
            {
                vehicle.setPosition(vehicle.posX - mtv.x, vehicle.posY, vehicle.posZ - mtv.z);
                orientedBoundingBox.updateForPositionAndRotation(vehicle.posX, vehicle.posY, vehicle.posZ, vehicle.rotationYaw);
                orientedBoundingBox.setAABBToOBBExtents(vehicle);
            }
        }
    }

    /*
     * locate the closest AABB top that the vehicle is resting on or will rest on given the input yMotion<br>
     * return adjusted y motion to rest on top of the highest colliding bounding-box.
     *
     * @param yMotion must be a negative value
     * @param aabbs   a list of -potentially- colliding AABBs
     */
    private double getYNegativeMove(double yMotion, List<AxisAlignedBB> aabbs) {
        Vec3d mtvTempBase = new Vec3d(0, 0, 0);
        Vec3d mtvTemp = null;
        AxisAlignedBB bb = null;
        int len = aabbs.size();
        double maxFoundY = vehicle.posY + yMotion;
        for (int i = 0; i < len; i++) {
            bb = aabbs.get(i);
            if (bb.maxY <= maxFoundY) {
                continue;
            }//to far below to care
            if (bb.minY >= vehicle.getEntityBoundingBox().maxY) {
                continue;
            }//to far above to care
            mtvTemp = orientedBoundingBox.getMinCollisionVector(bb, mtvTempBase);//check each bb vs the OBB for x/z collision
            if (mtvTemp != null)//it collides, check for top height
            {
                if (bb.maxY > maxFoundY) {
                    maxFoundY = bb.maxY;
                }
            }
        }
        return maxFoundY - vehicle.posY;
    }

    /*
     * Locate the maximum amount this entity can move in the positive Y direction before encountering a colliding object.
     */
    private double getYPositiveMove(double yMotion, List<AxisAlignedBB> aabbs) {
        Vec3d mtvTempBase = new Vec3d(0, 0, 0);
        Vec3d mtvTemp = null;
        AxisAlignedBB bb = null;
        int len = aabbs.size();
        double minFoundY = vehicle.posY + vehicle.vehicleHeight + yMotion;//the max to check
        for (int i = 0; i < len; i++) {
            bb = aabbs.get(i);
            if (bb.maxY < vehicle.posY + vehicle.height) {
                continue;
            }//dont care about stuff already colliding or below the vehicle
            if (bb.minY >= minFoundY) {
                continue;
            }//also dont care about stuff too high to reach from input movement
            mtvTemp = orientedBoundingBox.getMinCollisionVector(bb, mtvTempBase);//check each bb vs the OBB for x/z collision
            if (mtvTemp != null)//it collides, check for top height
            {
                if (bb.minY < minFoundY) {
                    minFoundY = bb.minY;
                }
            }
        }
        return minFoundY - (vehicle.posY + vehicle.vehicleHeight);
    }

    /*
     * vehicle OBB should alread be moved into the position to step upwards from before this method is called.<br>
     * (test both axes simultaneously, do not step if -any- collision would be found)<br>
     * does not move vehicle, only returns the move that -should- be made.
     *
     * @return < 0 for invalid step (no fit found), >=0 for valid step-up operation (position should be valid and uncollided post step)
     */
    private double getYStepHeight(List<AxisAlignedBB> aabbs) {
        double maxYCheck = vehicle.posY + vehicle.stepHeight + vehicle.vehicleHeight;
        double maxYPosition = vehicle.posY + vehicle.stepHeight;
        double minYPosition = vehicle.posY;

        double maxRestingY = vehicle.posY;
        double minHeadHight = maxYCheck;

        Vec3d mtvTempBase = new Vec3d(0, 0, 0);
        Vec3d mtvTemp = null;
        AxisAlignedBB bb = null;
        int len = aabbs.size();
        for (int i = 0; i < len; i++) {
            bb = aabbs.get(i);
            if (bb.maxY <= minYPosition) {
                continue;
            }//too low to care about, already on top of or below existing y position
            if (bb.minY >= maxYCheck) {
                continue;
            }//too high to check, would be above max step position collision range
            mtvTemp = orientedBoundingBox.getMinCollisionVector(bb, mtvTempBase);//check each bb vs the OBB for x/z collision
            if (mtvTemp != null)//it collides, check for min/max values
            {
                if (bb.maxY <= maxYPosition) {
                    if (bb.maxY > maxRestingY) {
                        maxRestingY = bb.maxY;
                    }
                } else {
                    if (bb.minY < minHeadHight) {
                        minHeadHight = bb.minY;
                    }
                }
            }
            if (minHeadHight - maxRestingY < vehicle.vehicleHeight) {
                return -1;
            }//early out if at any phase we have detected the vehicle cannot fit into the space specified
        }
        return maxRestingY - vehicle.posY;
    }

    /*
     * Return the maximum amount (up to input) that the vehicle can move on the X axis in the input direction before collision.
     */
    private double getXmove(double xMotion, List<AxisAlignedBB> aabbs) {
        AxisAlignedBB bb;
        int len = aabbs.size();
        double xMove = xMotion;
        Vec3d vec;
        for (int i = 0; i < len; i++) {
            bb = aabbs.get(i);
            if (bb.maxY <= vehicle.getEntityBoundingBox().minY || bb.minY >= vehicle.getEntityBoundingBox().maxY) {
                continue;
            }//no collision at all, skip
            vec = orientedBoundingBox.getCollisionVectorXMovement(bb, xMotion);
            if (vec != null) {
                if (Math.abs(vec.x) < Math.abs(xMove)) {
                    xMove = vec.x;
                }
            }
        }
        if (Math.abs(xMove) < 0.001d) {
            xMove = 0;
        }
        return xMove;
    }

    /*
     * Return the maximum amount (up to input) that the vehicle can move on the Z axis in the input direction before collision.
     */
    private double getZMove(double zMotion, List<AxisAlignedBB> aabbs) {
        AxisAlignedBB bb;
        int len = aabbs.size();

        double zMove = zMotion;
        Vec3d vec;
        for (int i = 0; i < len; i++) {
            bb = aabbs.get(i);
            if (bb.maxY <= vehicle.getEntityBoundingBox().minY || bb.minY >= vehicle.getEntityBoundingBox().maxY) {
                continue;
            }//no collision at all, skip
            vec = orientedBoundingBox.getCollisionVectorZMovement(bb, zMotion);
            if (vec != null) {
                if (Math.abs(vec.z) < Math.abs(zMove)) {
                    zMove = vec.z;
                }
            }
        }
        if (Math.abs(zMove) < 0.001d) {
            zMove = 0;
        }
        return zMove;
    }
}
