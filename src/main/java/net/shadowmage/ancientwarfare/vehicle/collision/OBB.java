package net.shadowmage.ancientwarfare.vehicle.collision;

import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.shadowmage.ancientwarfare.core.util.Trig;

/*
 * Entity Origin-local OBB for entity collision detection between other OBB and vanilla minecraft AABBs<br>
 * OBB keeps vectors in local space (relative to entity).<br>
 * Need to call updateForRotation(float) whenever rotation changes for the entity.  This will update the internal
 * coordinates of the OBB.<br>
 * After that the entities vanilla AABB will need to be updated by calling setAABBToOBBExtents(AxisAlignedBB), passing in the entities bounding box,
 * this will set the extents of the bounding box to encompass the entire OBB.
 *
 * @author Shadowmage
 */
public final class OBB {

    /*
     * Cached static axis variable to use for interaction with AABBs (x-axis)
     */
    private static Axis aabbAxis1 = new Axis(1, 0, 0);

    /*
     * Cached static axis variable to use for interaction with AABBs (z-axis)
     */
    private static Axis aabbAxis2 = new Axis(0, 0, 1);

    /*
     * Cached array of Vec3d to use for AABB corners
     */
    private Vec3d[] aaBBCorners = new Vec3d[]
            {
                    new Vec3d(0, 0, 0),
                    new Vec3d(0, 0, 0),
                    new Vec3d(0, 0, 0),
                    new Vec3d(0, 0, 0)
            };

    /*
     * cached projections to use for overlap testing
     */
    private Projection p1 = new Projection(0, 0), p2 = new Projection(0, 0);

    /*
     * cached line segments for use in intersection testing
     */
    private LineSegment obbEdge1, obbEdge2, obbEdge3, obbEdge4;
    private LineSegment obbLine1, obbLine2, obbLine3, obbLine4;

    /*
     * W,L,H values for the OBB
     */
    public final float width, height, length;

    /*
     * cached halfWidth, halfLength values for the OBB
     */
    public final float halfWidth, halfLength;

    /*
     * cached values for max width/length extents, used to set an input AABB to cover the extents of this OBB
     */
    private float widthExtent, lengthExtent;

    /*
     * lower corners of an entity-origin OBB
     */
    private Vec3d[] corners = new Vec3d[4];//upper corners would be the same thing, with y=height...so too boring to implement

    /*
     * lower corners of world-origin OBB
     */
    private Vec3d[] cornerPos = new Vec3d[4];//actual world-position corners for the BB;

    /*
     * world-origin center of the OBB.  Updated when updateForPosition() is called.
     */
    private double x, y, z;

    /*
     * cached axis vectors for this OBB, updated when yaw/rotation changes
     */
    private Axis axis1, axis2;

    /*
     * last calcd yaw value.  Used to determine if new yaw calculations are needed
     */
    private float yaw = 0;

    public OBB(float width, float height, float length) {
        this.width = width;
        this.height = height;
        this.length = length;
        this.halfWidth = width / 2.f;
        this.halfLength = length / 2.f;
        corners[0] = new Vec3d(-halfWidth, 0, -halfLength);//front left
        corners[1] = new Vec3d(halfWidth, 0, -halfLength);//front right
        corners[2] = new Vec3d(halfWidth, 0, halfLength);//rear right
        corners[3] = new Vec3d(-halfWidth, 0, halfLength);//rear left
        cornerPos[0] = copyVec(corners[0]);
        cornerPos[1] = copyVec(corners[1]);
        cornerPos[2] = copyVec(corners[2]);
        cornerPos[3] = copyVec(corners[3]);
        obbEdge1 = new LineSegment(cornerPos[0], cornerPos[1]);
        obbEdge2 = new LineSegment(cornerPos[1], cornerPos[2]);
        obbEdge3 = new LineSegment(cornerPos[2], cornerPos[3]);
        obbEdge4 = new LineSegment(cornerPos[3], cornerPos[0]);
        obbLine1 = new LineSegment(cornerPos[0], copyVec(cornerPos[0]));
        obbLine2 = new LineSegment(cornerPos[1], copyVec(cornerPos[1]));
        obbLine3 = new LineSegment(cornerPos[2], copyVec(cornerPos[2]));
        obbLine4 = new LineSegment(cornerPos[3], copyVec(cornerPos[3]));
        axis1 = new Axis(1, 0, 0);
        axis2 = new Axis(0, 0, 1);
        updateForRotation(0);
        updateAxis();
    }

    /*
     * Update the world-position for this OBB.<br>
     * Must be called whenever the entities position is updated<br>
     * Internally it updates the cached world-position corner vectors to reflect the new input entity center position.
     */
    public final void updateForPosition(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
        for (int i = 0; i < 4; i++) {
            updateCornerVector(i);
        }
    }

    public final void updateForPositionAndRotation(double x, double y, double z, float yaw) {
        updateForRotation(yaw);
        updateForPosition(x, y, z);
    }

    /*
     * updates the input world-position corner to be the origin-corner + last known position
     */
    private void updateCornerVector(int index) {
        cornerPos[index].x = corners[index].x + x;
        cornerPos[index].y = corners[index].y + y;
        cornerPos[index].z = corners[index].z + z;
    }

    private static void setVector(Vec3d toSet, double x, double y, double z) {
        toSet.x = x;
        toSet.y = y;
        toSet.z = z;
    }

    /*
     * Vector helper function to copy a vector
     */
    private Vec3d copyVec(Vec3d in) {
        return new Vec3d(in.x, in.y, in.z);
    }

    public final void setRotation(float yaw) {
        this.yaw = yaw;
        float yawRad = Trig.TORADIANS * yaw;
        float cos = MathHelper.cos(yawRad);
        float sin = MathHelper.sin(yawRad);

        float x1 = -halfWidth;
        float z1 = -halfLength;
        float x2 = halfWidth;
        float z2 = -halfLength;

        float tx1 = x1 * cos - z1 * sin;
        float tz1 = x1 * sin + z1 * cos;
        float tx2 = x2 * cos - z2 * sin;
        float tz2 = x2 * sin + z2 * cos;

        widthExtent = Math.max(Math.abs(tx1), Math.abs(tx2));
        lengthExtent = Math.max(Math.abs(tz1), Math.abs(tz2));

        //front-left corner
        corners[0].x = tx1;
        corners[0].z = tz1;

        //front-right corner
        corners[1].x = tx2;
        corners[1].z = tz2;

        //rear-right corner
        corners[2].x = -corners[0].x;
        corners[2].z = -corners[0].z;

        //rear-left corner
        corners[3].x = -corners[1].x;
        corners[3].z = -corners[1].z;
        updateAxis();
        updateForPosition(x, y, z);
    }

    /*
     * Update this OBB for the input yaw rotation. Updates local-origin corners and axis only, does not update world-position corners -- call updateForPosition() for that.<br>
     * Will only update if the input yaw is different than the previous yaw
     */
    public final void updateForRotation(float yaw) {
        yaw = -yaw;//TODO figure out why yaw is inverted for OBB, figure out where else it might be inverted for other calculations
        if (yaw == this.yaw) {
            return;
        }//do not recalc if yaw has not changed
        setRotation(yaw);
    }

    private void updateAxis() {
        axis1.axisX = corners[1].x - corners[0].x;
        axis1.axisZ = corners[1].z - corners[0].z;
        axis1.normalize();
        axis2.axisX = corners[2].x - corners[1].x;
        axis2.axisZ = corners[2].z - corners[1].z;
        axis2.normalize();
    }

    /*
     * does not check y-coordinates. 2-d only (as there is no rotation for pitch, y-tests are a simple line overlap test)
     */
    public final Vec3d getMinCollisionVector(OBB bb, Vec3d mtvOut) {
        return getMinCollisionVector(bb.cornerPos, bb.axis1, bb.axis2, mtvOut);
    }

    public final Vec3d getMinCollisionVector(AxisAlignedBB bb, Vec3d mtvOut) {
        setVector(aaBBCorners[0], bb.minX, 0, bb.minZ);
        setVector(aaBBCorners[1], bb.maxX, 0, bb.minZ);
        setVector(aaBBCorners[2], bb.maxX, 0, bb.maxZ);
        setVector(aaBBCorners[3], bb.minX, 0, bb.maxZ);
        return getMinCollisionVector(aaBBCorners, aabbAxis1, aabbAxis2, mtvOut);
    }

    private Vec3d getMinCollisionVector(Vec3d[] inCorners, Axis axis3, Axis axis4, Vec3d mtvOut) {
        double minOverlap = Double.MAX_VALUE;
        double overlap = 0;
        Axis overlapAxis = null;

        p1 = axis1.projectShape(cornerPos, p1);
        p2 = axis1.projectShape(inCorners, p2);
        if (!p1.doesOverlap(p2)) {
            return null;
        }//no collision on that axis
        overlap = p1.getOverlap(p2);
        if (overlap == 0) {
            return null;
        }
        if (Math.abs(overlap) < Math.abs(minOverlap)) {
            minOverlap = overlap;
            overlapAxis = axis1;
        }

        p1 = axis2.projectShape(cornerPos, p1);
        p2 = axis2.projectShape(inCorners, p2);
        if (!p1.doesOverlap(p2)) {
            return null;
        }//no collision on that axis
        overlap = p1.getOverlap(p2);
        if (overlap == 0) {
            return null;
        }
        if (Math.abs(overlap) < Math.abs(minOverlap)) {
            minOverlap = overlap;
            overlapAxis = axis2;
        }

        p1 = axis3.projectShape(cornerPos, p1);
        p2 = axis3.projectShape(inCorners, p2);
        if (!p1.doesOverlap(p2)) {
            return null;
        }//no collision on that axis
        overlap = p1.getOverlap(p2);
        if (overlap == 0) {
            return null;
        }
        if (Math.abs(overlap) < Math.abs(minOverlap)) {
            minOverlap = overlap;
            overlapAxis = axis3;
        }

        p1 = axis4.projectShape(cornerPos, p1);
        p2 = axis4.projectShape(inCorners, p2);
        if (!p1.doesOverlap(p2)) {
            return null;
        }//no collision on that axis
        overlap = p1.getOverlap(p2);
        if (overlap == 0) {
            return null;
        }
        if (Math.abs(overlap) < Math.abs(minOverlap)) {
            minOverlap = overlap;
            overlapAxis = axis4;
        }

        return new Vec3d(overlapAxis.axisX * minOverlap, 0, overlapAxis.axisZ * minOverlap);
    }


    public Vec3d getCollisionVectorXMovement(AxisAlignedBB bb, double xMove) {
        setVector(aaBBCorners[0], bb.minX, cornerPos[0].y, bb.minZ);//front left
        setVector(aaBBCorners[1], bb.maxX, cornerPos[0].y, bb.minZ);//front right
        setVector(aaBBCorners[2], bb.maxX, cornerPos[0].y, bb.maxZ);//rear right
        setVector(aaBBCorners[3], bb.minX, cornerPos[0].y, bb.maxZ);//rear left
        setupOBBCollisionLines(xMove, 0);
        Vec3d interceptBase = new Vec3d(0, 0, 0);
        Vec3d intercept;

        double adjustedXMove = xMove;

        /*
         * test vectors from the corners of the OBB into the BB
         */
        LineSegment bbEdge = xMove > 0 ? new LineSegment(aaBBCorners[0], aaBBCorners[3]) : new LineSegment(aaBBCorners[1], aaBBCorners[2]);
        if (cornerPos[0].z >= bb.minZ && cornerPos[0].z <= bb.maxZ) {
            intercept = bbEdge.getIntersect(obbLine1, interceptBase);
            if (intercept != null) {
                double x = intercept.x - cornerPos[0].x;
                if (Math.abs(x) < Math.abs(adjustedXMove)) {
                    adjustedXMove = x;
                }
            }
        }
        if (cornerPos[1].z >= bb.minZ && cornerPos[1].z <= bb.maxZ) {
            intercept = bbEdge.getIntersect(obbLine2, interceptBase);
            if (intercept != null) {
                double x = intercept.x - cornerPos[1].x;
                if (Math.abs(x) < Math.abs(adjustedXMove)) {
                    adjustedXMove = x;
                }
            }
        }
        if (cornerPos[2].z >= bb.minZ && cornerPos[2].z <= bb.maxZ) {
            intercept = bbEdge.getIntersect(obbLine3, interceptBase);
            if (intercept != null) {
                double x = intercept.x - cornerPos[2].x;
                if (Math.abs(x) < Math.abs(adjustedXMove)) {
                    adjustedXMove = x;
                }
            }
        }
        if (cornerPos[3].z >= bb.minZ && cornerPos[3].z <= bb.maxZ) {
            intercept = bbEdge.getIntersect(obbLine4, interceptBase);
            if (intercept != null) {
                double x = intercept.x - cornerPos[3].x;
                if (Math.abs(x) < Math.abs(adjustedXMove)) {
                    adjustedXMove = x;
                }
            }
        }

        /*
         * need to figure out if should test left or right side of BB, by testing vector from x,y,z to bb center
         */
        Vec3d bbVec1 = xMove > 0 ? aaBBCorners[0] : aaBBCorners[1];
        Vec3d bbVec2 = xMove > 0 ? aaBBCorners[3] : aaBBCorners[2];
        LineSegment bbLine = new LineSegment(bbVec1, bbVec1.addVector(-xMove, 0, 0));
        LineSegment bbLine2 = new LineSegment(bbVec2, bbVec2.addVector(-xMove, 0, 0));
        //TODO find which obb lines I should be testing against...

        //test OBBEdge1
        if (bbVec1.z >= Trig.min(cornerPos[0].z, cornerPos[1].z) && bbVec1.z <= Trig.max(cornerPos[0].z, cornerPos[1].z)) {
            intercept = obbEdge1.getIntersect(bbLine, interceptBase);
            if (intercept != null) {
                double x = bbVec1.x - intercept.x;
                if (Math.abs(x) < Math.abs(adjustedXMove)) {
                    adjustedXMove = x;
                }
            }
        }
        if (bbVec2.z >= Trig.min(cornerPos[0].z, cornerPos[1].z) && bbVec2.z <= Trig.max(cornerPos[0].z, cornerPos[1].z)) {
            intercept = obbEdge1.getIntersect(bbLine2, interceptBase);
            if (intercept != null) {
                double x = bbVec2.x - intercept.x;
                if (Math.abs(x) < Math.abs(adjustedXMove)) {
                    adjustedXMove = x;
                }
            }
        }

        //test OBBEdge2
        if (bbVec1.z >= Trig.min(cornerPos[1].z, cornerPos[2].z) && bbVec1.z <= Trig.max(cornerPos[1].z, cornerPos[2].z)) {
            intercept = obbEdge2.getIntersect(bbLine, interceptBase);
            if (intercept != null) {
                double x = bbVec1.x - intercept.x;
                if (Math.abs(x) < Math.abs(adjustedXMove)) {
                    adjustedXMove = x;
                }
            }
        }
        if (bbVec2.z >= Trig.min(cornerPos[1].z, cornerPos[2].z) && bbVec2.z <= Trig.max(cornerPos[1].z, cornerPos[2].z)) {
            intercept = obbEdge2.getIntersect(bbLine2, interceptBase);
            if (intercept != null) {
                double x = bbVec2.x - intercept.x;
                if (Math.abs(x) < Math.abs(adjustedXMove)) {
                    adjustedXMove = x;
                }
            }
        }

        //test OBBEdge3
        if (bbVec1.z >= Trig.min(cornerPos[2].z, cornerPos[3].z) && bbVec1.z <= Trig.max(cornerPos[2].z, cornerPos[3].z)) {
            intercept = obbEdge3.getIntersect(bbLine, interceptBase);
            if (intercept != null) {
                double x = bbVec1.x - intercept.x;
                if (Math.abs(x) < Math.abs(adjustedXMove)) {
                    adjustedXMove = x;
                }
            }
        }
        if (bbVec2.z >= Trig.min(cornerPos[2].z, cornerPos[3].z) && bbVec2.z <= Trig.max(cornerPos[2].z, cornerPos[3].z)) {
            intercept = obbEdge3.getIntersect(bbLine2, interceptBase);
            if (intercept != null) {
                double x = bbVec2.x - intercept.x;
                if (Math.abs(x) < Math.abs(adjustedXMove)) {
                    adjustedXMove = x;
                }
            }
        }

        //test OBBEdge3
        if (bbVec1.z >= Trig.min(cornerPos[3].z, cornerPos[0].z) && bbVec1.z <= Trig.max(cornerPos[3].z, cornerPos[0].z)) {
            intercept = obbEdge4.getIntersect(bbLine, interceptBase);
            if (intercept != null) {
                double x = bbVec1.x - intercept.x;
                if (Math.abs(x) < Math.abs(adjustedXMove)) {
                    adjustedXMove = x;
                }
            }
        }
        if (bbVec2.z >= Trig.min(cornerPos[3].z, cornerPos[0].z) && bbVec2.z <= Trig.max(cornerPos[3].z, cornerPos[0].z)) {
            intercept = obbEdge4.getIntersect(bbLine2, interceptBase);
            if (intercept != null) {
                double x = bbVec2.x - intercept.x;
                if (Math.abs(x) < Math.abs(adjustedXMove)) {
                    adjustedXMove = x;
                }
            }
        }

        //TODO find proper epsilon value
        if (adjustedXMove != x) {
            adjustedXMove *= 0.995d;
        }//minor correction so that entity should never clip into geometry, even with rounding errors...

        Vec3d closestIntercept = new Vec3d(adjustedXMove, 0, 0);
        return closestIntercept;
    }

    private void setupOBBCollisionLines(double x, double z) {
        obbLine1.end.x = obbLine1.start.x + x;
        obbLine1.end.y = obbLine1.start.y;
        obbLine1.end.z = obbLine1.start.z + z;

        obbLine2.end.x = obbLine2.start.x + x;
        obbLine2.end.y = obbLine2.start.y;
        obbLine2.end.z = obbLine2.start.z + z;

        obbLine3.end.x = obbLine3.start.x + x;
        obbLine3.end.y = obbLine3.start.y;
        obbLine3.end.z = obbLine3.start.z + z;

        obbLine4.end.x = obbLine4.start.x + x;
        obbLine4.end.y = obbLine4.start.y;
        obbLine4.end.z = obbLine4.start.z + z;
    }

    public final Vec3d getCollisionVectorZMovement(AxisAlignedBB bb, double zMove) {
        setVector(aaBBCorners[0], bb.minX, cornerPos[0].y, bb.minZ);//front left
        setVector(aaBBCorners[1], bb.maxX, cornerPos[0].y, bb.minZ);//front right
        setVector(aaBBCorners[2], bb.maxX, cornerPos[0].y, bb.maxZ);//rear right
        setVector(aaBBCorners[3], bb.minX, cornerPos[0].y, bb.maxZ);//rear left
        setupOBBCollisionLines(0, zMove);
        Vec3d interceptBase = new Vec3d(0, 0, 0);
        Vec3d intercept;

        double adjustedZMove = zMove;

        //TODO find what OBB lines I should be testing against
        /*
         * test vectors from the corners of the OBB into the BB
         */
        LineSegment bbEdge = zMove > 0 ? new LineSegment(aaBBCorners[0], aaBBCorners[1]) : new LineSegment(aaBBCorners[3], aaBBCorners[2]);
        if (cornerPos[0].x >= bb.minX && cornerPos[0].x <= bb.maxX) {
            intercept = bbEdge.getIntersect(obbLine1, interceptBase);
            if (intercept != null) {
                double z = intercept.z - cornerPos[0].z;
                if (Math.abs(z) < Math.abs(adjustedZMove)) {
                    adjustedZMove = z;
                }
            }
        }
        if (cornerPos[1].x >= bb.minX && cornerPos[1].x <= bb.maxX) {
            intercept = bbEdge.getIntersect(obbLine2, interceptBase);
            if (intercept != null) {
                double z = intercept.z - cornerPos[1].z;
                if (Math.abs(z) < Math.abs(adjustedZMove)) {
                    adjustedZMove = z;
                }
            }
        }
        if (cornerPos[2].x >= bb.minX && cornerPos[2].x <= bb.maxX) {
            intercept = bbEdge.getIntersect(obbLine3, interceptBase);
            if (intercept != null) {
                double z = intercept.z - cornerPos[2].z;
                if (Math.abs(z) < Math.abs(adjustedZMove)) {
                    adjustedZMove = z;
                }
            }
        }
        if (cornerPos[3].x >= bb.minX && cornerPos[3].x <= bb.maxX) {
            intercept = bbEdge.getIntersect(obbLine4, interceptBase);
            if (intercept != null) {
                double z = intercept.z - cornerPos[3].z;
                if (Math.abs(z) < Math.abs(adjustedZMove)) {
                    adjustedZMove = z;
                }
            }
        }

        /*
         * need to figure out if should test left or right side of BB, by testing vector from x,y,z to bb center
         */
        Vec3d bbVec1 = zMove > 0 ? aaBBCorners[0] : aaBBCorners[3];//minX and either min or maxZ
        Vec3d bbVec2 = zMove > 0 ? aaBBCorners[1] : aaBBCorners[2];//maxX and either min or maxZ

        LineSegment bbLine = new LineSegment(bbVec1, bbVec1.addVector(0, 0, -zMove));
        LineSegment bbLine2 = new LineSegment(bbVec2, bbVec2.addVector(0, 0, -zMove));
        //TODO find which obb lines I should be testing against...//
        //test OBBEdge1
        if (bbVec1.x >= Trig.min(cornerPos[0].x, cornerPos[1].x) && bbVec1.x <= Trig.max(cornerPos[0].x, cornerPos[1].x)) {
            intercept = obbEdge1.getIntersect(bbLine, interceptBase);
            if (intercept != null) {
                double z = bbVec1.z - intercept.z;
                if (Math.abs(z) < Math.abs(adjustedZMove)) {
                    adjustedZMove = z;
                }
            }
        }
        if (bbVec2.x >= Trig.min(cornerPos[0].x, cornerPos[1].x) && bbVec2.x <= Trig.max(cornerPos[0].x, cornerPos[1].x)) {
            intercept = obbEdge1.getIntersect(bbLine2, interceptBase);
            if (intercept != null) {
                double z = bbVec2.z - intercept.z;
                if (Math.abs(z) < Math.abs(adjustedZMove)) {
                    adjustedZMove = z;
                }
            }
        }
        //test OBBEdge2
        if (bbVec1.x >= Trig.min(cornerPos[1].x, cornerPos[2].x) && bbVec1.x <= Trig.max(cornerPos[1].x, cornerPos[2].x)) {
            intercept = obbEdge2.getIntersect(bbLine, interceptBase);
            if (intercept != null) {
                double z = bbVec1.z - intercept.z;
                if (Math.abs(z) < Math.abs(adjustedZMove)) {
                    adjustedZMove = z;
                }
            }
        }
        if (bbVec2.x >= Trig.min(cornerPos[1].x, cornerPos[2].x) && bbVec2.x <= Trig.max(cornerPos[1].x, cornerPos[2].x)) {
            intercept = obbEdge2.getIntersect(bbLine2, interceptBase);
            if (intercept != null) {
                double z = bbVec2.z - intercept.z;
                if (Math.abs(z) < Math.abs(adjustedZMove)) {
                    adjustedZMove = z;
                }
            }
        }
        //test OBBEdge3
        if (bbVec1.x >= Trig.min(cornerPos[2].x, cornerPos[3].x) && bbVec1.x <= Trig.max(cornerPos[2].x, cornerPos[3].x)) {
            intercept = obbEdge3.getIntersect(bbLine, interceptBase);
            if (intercept != null) {
                double z = bbVec1.z - intercept.z;
                if (Math.abs(z) < Math.abs(adjustedZMove)) {
                    adjustedZMove = z;
                }
            }
        }
        if (bbVec2.x >= Trig.min(cornerPos[2].x, cornerPos[3].x) && bbVec2.x <= Trig.max(cornerPos[2].x, cornerPos[3].x)) {
            intercept = obbEdge3.getIntersect(bbLine2, interceptBase);
            if (intercept != null) {
                double z = bbVec2.z - intercept.z;
                if (Math.abs(z) < Math.abs(adjustedZMove)) {
                    adjustedZMove = z;
                }
            }
        }
        //test OBBEdge4
        if (bbVec1.x >= Trig.min(cornerPos[3].x, cornerPos[0].x) && bbVec1.x <= Trig.max(cornerPos[3].x, cornerPos[0].x)) {
            intercept = obbEdge4.getIntersect(bbLine, interceptBase);
            if (intercept != null) {
                double z = bbVec1.z - intercept.z;
                if (Math.abs(z) < Math.abs(adjustedZMove)) {
                    adjustedZMove = z;
                }
            }
        }
        if (bbVec2.x >= Trig.min(cornerPos[3].x, cornerPos[0].x) && bbVec2.x <= Trig.max(cornerPos[3].x, cornerPos[0].x)) {
            intercept = obbEdge4.getIntersect(bbLine2, interceptBase);
            if (intercept != null) {
                double z = bbVec2.z - intercept.z;
                if (Math.abs(z) < Math.abs(adjustedZMove)) {
                    adjustedZMove = z;
                }
            }
        }

        //TODO find proper epsilon value
        if (adjustedZMove != z) {
            adjustedZMove *= 0.995d;
        }//minor correction so that entity should never clip into geometry, even with rounding errors...

        Vec3d closestIntercept = new Vec3d(0, 0, adjustedZMove);
        return closestIntercept;
    }

    /*
     * Return a corner of the OBB for the given index<br>
     * 0=front left<br>
     * 1=front right<br>
     * 2=rear right<br>
     * 3=rear left<br>
     *
     * @param index valid values are 0-3
     * @return the currently calculated corner position for the given index, vec is in world coordinates
     */
    public Vec3d getCorner(int index) {
        return cornerPos[index];
    }

    /*
     * Sets the input bb to encompass the extents of the OBB<br>
     *
     * @param bb the AABB to be set
     */
    public final void setAABBToOBBExtents(AxisAlignedBB bb) {
        bb.setBounds(-widthExtent, 0, -lengthExtent, widthExtent, height, lengthExtent);
        bb.offset(x, y, z);
    }

    @Override
    public String toString() {
        return "OBB: " + cornerPos[0] + " : " + cornerPos[1] + " : " + cornerPos[2] + " : " + cornerPos[3];
    }

    private static final class Axis {

        public double axisX, axisY, axisZ;

        public Axis(double x, double y, double z) {
            this.axisX = x;
            this.axisY = y;
            this.axisZ = z;
            normalize();
        }

        public final void normalize() {
            double sq = axisX * axisX + axisY * axisY + axisZ * axisZ;
            if (Math.abs(sq) > 0.0000001d) {
                sq = Math.sqrt(sq);
                axisX /= sq;
                axisY /= sq;
                axisZ /= sq;
            }
        }

        public final double dot(Vec3d vec) {
            return axisX * vec.x + axisY * vec.y + axisZ * vec.z;
        }

        @Override
        public String toString() {
            return String.format("Axis: %.2f, %.2f, %.2f", axisX, axisY, axisZ);
        }

        private Projection projectShape(Vec3d[] corners, Projection p) {
            double d = dot(corners[0]);
            double min = d;
            double max = d;
            int len = corners.length;
            for (int i = 1; i < len; i++)//skip first corner, already used to set starting min/max
            {
                d = dot(corners[i]);
                if (i == 0) {
                    min = max = d;
                } else {
                    if (d < min) {
                        min = d;
                    }
                    if (d > max) {
                        max = d;
                    }
                }
            }
            p.min = min;
            p.max = max;
            return p;
        }

    }

    private static final class Projection {

        public double min, max;

        public Projection(double min, double max) {
            this.min = min;
            this.max = max;
        }

        public boolean doesOverlap(Projection p) {
            if (p.max < min) {
                return false;
            }
            if (p.min > max) {
                return false;
            }
            return true;
        }

        //overlap code skimmed from: https://github.com/ghost7/collision/blob/master/sat/Polygon.cpp
        public double getOverlap(Projection p) {
            if (min > p.max || max < p.min) {
                return 0;
            } else if (min < p.min) {
                return p.min - max;
            } else {
                return p.max - min;
            }
        }

        @Override
        public String toString() {
            return String.format("Proj: %.2f |<->| %.2f", min, max);
        }
    }

    private static final class LineSegment {

        public Vec3d start, end;

        public LineSegment(Vec3d start, Vec3d end) {
            this.start = start;
            this.end = end;
        }

        public Vec3d getIntersect(LineSegment otherLine, Vec3d out) {
            if (getLineIntersection(start, end, otherLine.start, otherLine.end, out)) {
                return out;
            }
            return null;
        }

        private boolean getLineIntersection(Vec3d p0, Vec3d p1, Vec3d p2, Vec3d p3, Vec3d out) {
            double s02_x, s02_y, s10_x, s10_y, s32_x, s32_y, s_numer, t_numer, denom, t;
            s10_x = p1.x - p0.x;
            s10_y = p1.z - p0.z;
            s32_x = p3.x - p2.x;
            s32_y = p3.z - p2.z;

            denom = s10_x * s32_y - s32_x * s10_y;
            if (denom == 0) {
                return false;
            } // Collinear
            boolean denomPositive = denom > 0;

            s02_x = p0.x - p2.x;
            s02_y = p0.z - p2.z;
            s_numer = s10_x * s02_y - s10_y * s02_x;
            if ((s_numer < 0) == denomPositive) {
                return false;
            }

            t_numer = s32_x * s02_y - s32_y * s02_x;
            if ((t_numer < 0) == denomPositive) {
                return false;
            }

            if (((s_numer > denom) == denomPositive) || ((t_numer > denom) == denomPositive)) {
                return false;
            }
            // Collision detected
            t = t_numer / denom;
            if (out != null) {
                out.x = p0.x + (t * s10_x);
                out.z = p0.z + (t * s10_y);
            }
            return true;
        }
    }

}
