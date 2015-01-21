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
package net.shadowmage.ancientwarfare.core.util;

import net.minecraft.util.MathHelper;

/**
 * because I hate it so much...why not make the
 * computer do it all for me?
 *
 * @author Shadowmage
 */
public class Trig {
    public static final float PI = 3.141592653589793f;
    public static final float TORADIANS = PI / 180.f;
    public static final float TODEGREES = 180.f / PI;
    public static final float GRAVITY = 9.81f;

    public static int getPower(int num, int exp) {
        return Double.valueOf(Math.floor(Math.pow(num, exp))).intValue();
    }

    public static float toRadians(float degrees) {
        return degrees * TORADIANS;
    }

    public static float toDegrees(float radians) {
        return radians * TODEGREES;
    }

    public static float cosDegrees(float degrees) {
        return MathHelper.cos(degrees * TORADIANS);
    }

    public static float sinDegrees(float degrees) {
        return MathHelper.sin(degrees * TORADIANS);
    }

    public static float cos(float radians) {
        return MathHelper.cos(radians);
    }

    public static float sin(float radians) {
        return MathHelper.sin(radians);
    }

    public static double wrapTo360(double in) {
        while (in >= 360) {
            in -= 360;
        }
        while (in < 0) {
            in += 360;
        }
        return in;
    }

    public static float wrapTo360(float in) {
        while (in >= 360.f) {
            in -= 360.f;
        }
        while (in < 0) {
            in += 360.f;
        }
        return in;
    }

    public static int getAbsDiff(int a, int b) {
        if (a < b) {
            return b - a;
        }
        return a - b;
    }

    public static float getAbsDiff(float a, float b) {
        if (a < b) {
            return b - a;
        }
        return a - b;
    }

    public static double getAbsDiff(double a, double b) {
        if (a < b) {
            return b - a;
        }
        return a - b;
    }

    /**
     * tests if _test_ is >=min && <=max
     */
    public static boolean isBetween(int test, int a, int b) {
        int min = a < b ? a : b;
        int max = a < b ? b : a;
        return test >= min && test <= max;
    }

    /**
     * is the angle between min and max (inclusive e.g. test <= max && test>=min)
     */
    public static boolean isAngleBetween(float test, float min, float max) {
        test = Trig.wrapTo360(test);
        min = Trig.wrapTo360(min);
        max = Trig.wrapTo360(max);
//  Config.logDebug(test+","+min+","+max);
        if (min > max) {
            return test >= min || test <= max;
        }
        if (test >= min && test <= max) {
            return true;
        }
        return false;
    }

    /**
     * returns the sqrt velocity of the input vectors (asuming base zero)
     */
    public static float getVelocity(float x, float y, float z) {
        return MathHelper.sqrt_float(x * x + y * y + z * z);
    }

    public static float getVelocity(double x, double y, double z) {
        return Trig.getVelocity((float) x, (float) y, (float) z);
    }

    public static float getDistance(double x, double y, double z, double x1, double y1, double z1) {
        return Math.abs(getVelocity(x1 - x, y1 - y, z1 - z));
    }

    /**
     * get velocity of a 2d vector
     */
    public static float getVelocity(double x, double z) {
        return MathHelper.sqrt_float((float) (x * x + z * z));
    }

    public static int getDifference(int a, int b) {
        return a < b ? b - a : a - b;
    }

    public static int getMin(int a, int b) {
        return a < b ? a : b;
    }

    public static int getMax(int a, int b) {
        return a < b ? b : a;
    }

    public static float getAngle(float x, float y) {
        return toDegrees((float) Math.atan2(y, x));
    }

    public static float getYawTowards(double x, double z, double tx, double tz) {
        float xAO = (float) (tx - x);
        float zAO = (float) (tz - z);
        float yaw = Trig.toDegrees((float) Math.atan2(zAO, xAO));
        yaw = -yaw;
        while (yaw < -180.f) {
            yaw += 360.f;
        }
        while (yaw >= 180.f) {
            yaw -= 360.f;
        }
        return yaw;
    }

    /**
     * get relative yaw change direction towards target from input yaw
     */
    public static float getYawTowardsTarget(double xStart, double zStart, double x, double z, float originYaw) {
        float xAO = (float) (xStart - x);
        float zAO = (float) (zStart - z);
        float yaw = Trig.toDegrees((float) Math.atan2(xAO, zAO));
        float vehYaw = originYaw;
        while (vehYaw < 0.f) {
            vehYaw += 360;
        }
        while (vehYaw >= 360.f) {
            vehYaw -= 360;
        }
        float yawDiff = yaw - vehYaw;
        while (yawDiff < -180.f) {
            yawDiff += 360.f;
        }
        while (yawDiff >= 180.f) {
            yawDiff -= 360.f;
        }
        return yawDiff;
    }

    public static float getYawDifference(float yaw, float dest) {
        float diff = Trig.getAbsDiff(yaw, dest);
        while (diff < -180) {
            diff += 360.f;
        }
        while (diff >= 180) {
            diff -= 360.f;
        }
        return diff;
    }

    public static byte getTurnDirection(float yaw, float dest) {
        float diff = Trig.getAbsDiff(yaw, dest);
        while (diff < -180) {
            diff += 360.f;
        }
        while (diff >= 180) {
            diff -= 360.f;
        }
        return (byte) (diff < 0 ? -1 : 1);
    }

    public static float getMin(float... vals) {
        float min = Float.MAX_VALUE;
        for (float val : vals) {
            if (val < min) {
                min = val;
            }
        }
        return min;
    }

    public static float getMax(float... vals) {
        float max = Float.MIN_VALUE;
        for (float val : vals) {
            if (val > max) {
                max = val;
            }
        }
        return max;
    }

}
