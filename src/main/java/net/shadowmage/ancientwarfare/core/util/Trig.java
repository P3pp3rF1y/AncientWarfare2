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
package net.shadowmage.ancientwarfare.core.util;

import net.minecraft.util.math.MathHelper;

/*
 * Static Math utilities class<br>
 *
 * @author Shadowmage
 */
public class Trig {
    public static final float PI = 3.141592653589793f;
    public static final float TORADIANS = PI / 180.f;
    public static final float TODEGREES = 180.f / PI;
    public static final float GRAVITY = 9.81f;
    public static final double gravityTick = GRAVITY * 0.05D * 0.05D;

    private Trig() {
    }//static utility class, no public facing constructor

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

    public static float getVelocity(double x, double y, double z) {
        return MathHelper.sqrt(x * x + y * y + z * z);
    }

    public static float getDistance(double x, double y, double z, double x1, double y1, double z1) {
        return Math.abs(getVelocity(x1 - x, y1 - y, z1 - z));
    }

    public static double min(double... vals) {
        double min = vals[0];
        for (int i = 1; i < vals.length; i++) {
            if (vals[i] < min) {
                min = vals[i];
            }
        }
        return min;
    }

    public static double max(double... vals) {
        double max = vals[0];
        for (int i = 1; i < vals.length; i++) {
            if (vals[i] > max) {
                max = vals[i];
            }
        }
        return max;
    }

    public static float min(float... vals) {
        float min = Float.MAX_VALUE;
        for (float val : vals) {
            if (val < min) {
                min = val;
            }
        }
        return min;
    }

    public static float max(float... vals) {
        float max = Float.MIN_VALUE;
        for (float val : vals) {
            if (val > max) {
                max = val;
            }
        }
        return max;
    }

	public static float wrapTo360(float angle) {
		return (angle %= 360) >= 0 ? angle : (angle + 360);
	}
}
