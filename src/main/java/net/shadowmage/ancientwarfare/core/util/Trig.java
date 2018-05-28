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

import net.minecraft.util.Tuple;
import net.minecraft.util.math.MathHelper;
import net.shadowmage.ancientwarfare.vehicle.missiles.AmmoHwachaRocket;

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

	public static float getAngleDiff(float alpha, float beta) {
		float phi = Math.abs(beta - alpha) % 360;
		return phi > 180 ? 360 - phi : phi;
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

	/**
	 * @param x input hit x (horizontal distance)
	 * @param y input hit y (vertical distance)
	 * @param v velocity per second
	 * @return
	 */
	public static Tuple<Float, Float> getLaunchAngleToHit(float x, float y, float v) {
		float v2 = v * v;
		float v4 = v * v * v * v;
		float x2 = x * x;
		float sqRtVal = MathHelper.sqrt(v4 - GRAVITY * (GRAVITY * x2 + 2 * y * v2));
		float h = v2 + sqRtVal;
		float l = v2 - sqRtVal;
		h /= GRAVITY * x;
		l /= GRAVITY * x;
		h = wrapTo360(toDegrees((float) Math.atan(h)));
		l = wrapTo360(toDegrees((float) Math.atan(l)));
		return new Tuple<>(h, l);
	}

	/**
	 * @param x raw X distance (x2 - x1)
	 * @param y vertical distance (y2 - y1)
	 * @param z raw Z distance (z2 - z1)
	 * @param v initial launch velocity per second
	 * @return
	 */
	public static Tuple<Float, Float> getLaunchAngleToHit(float x, float y, float z, float v) {
		return getLaunchAngleToHit(MathHelper.sqrt(x * x + z * z), y, v);
	}

	public static float iterativeSpeedFinder(float x, float y, float z, float angle, int maxIterations, boolean rocket) {
		return bruteForceSpeedFinder(MathHelper.sqrt(x * x + z * z), y, angle, maxIterations, rocket);
	}

	public static float bruteForceRocketFinder(float x, float y, float angle, int maxIterations) {
		float bestVelocity = 0.f;
		float velocityIncrement = 5.29f;
		float testVelocity = 1.f;
		float gravityTick = 9.81f * 0.05f * 0.05f;
		int rocketBurnTime = 0;
		float posX = 0;
		float posY = 0;
		float motX = 0;
		float motY = 0;
		float motX0 = 0;
		float motY0 = 0;
		float hitX = 0;
		float hitY = 0;
		boolean hitGround = true;
		float hitDiffX;
		float hitDiffY;
		float hitPercent;
		//  maxIterations *= 4;
		for (int iter = 0; iter < maxIterations; iter++) {
			//reset pos
			//calc initial motion from input angle and current testVelocity
			hitGround = true;
			posX = 0.f;
			posY = 0.f;
			motX = Trig.sinDegrees(angle) * testVelocity * 0.05f;
			motY = Trig.cosDegrees(angle) * testVelocity * 0.05f;

			rocketBurnTime = (int) (testVelocity * AmmoHwachaRocket.burnTimeFactor);
			motX0 = (motX / (testVelocity * 0.05f)) * AmmoHwachaRocket.accelerationFactor;
			motY0 = (motY / (testVelocity * 0.05f)) * AmmoHwachaRocket.accelerationFactor;
			motX = motX0;
			motY = motY0;
			while (motY >= 0 || posY >= y) {

				//move
				//check hit
				//apply gravity if not hit
				posX += motX;
				posY += motY;
				if (rocketBurnTime > 0) {
					rocketBurnTime--;
					motX += motX0;
					motY += motY0;
				} else {
					motY -= gravityTick;
				}
				if (posX > x) {
					hitGround = false;
					break;//missile went too far
				}
			}
			if (hitGround)//if break was triggered by going negative on y axis, get a more precise hit vector
			{
				motY += gravityTick;
				hitDiffX = motX - posX;
				hitDiffY = motY - posY;
				hitPercent = (y - posY) / hitDiffY;
				hitX = posX + hitDiffX * hitPercent;
				hitY = posY + +hitDiffY * hitPercent;
			}
			if (hitGround && hitX < x)// hit was not far enough, increase power
			{
				bestVelocity = testVelocity;
				testVelocity += velocityIncrement;
			} else if (posX < x)//
			{
				bestVelocity = testVelocity;
				testVelocity += velocityIncrement;
			} else//it was too far, go back to previous power, decrease increment, increase by new increment
			{
				bestVelocity = testVelocity;
				testVelocity -= velocityIncrement;
				velocityIncrement *= 0.5f;
				testVelocity += velocityIncrement;
			}
		}

		return bestVelocity;
	}

	public static float bruteForceSpeedFinder(float x, float y, float angle, int maxIterations, boolean rocket) {
		angle = 90 - angle;
		if (rocket) {
			return bruteForceRocketFinder(x, y, angle, maxIterations);
		}
		float bestVelocity = 0.f;
		float velocityIncrement = 10.f;
		float testVelocity = 10.f;
		float gravityTick = 9.81f * 0.05f * 0.05f;
		float posX = 0;
		float posY = 0;
		float motX = 0;
		float motY = 0;
		float motX0 = 0;
		float motY0 = 0;
		float hitX = 0;
		float hitY = 0;
		boolean hitGround = true;
		float hitDiffX;
		float hitDiffY;
		float hitPercent;
		for (int iter = 0; iter < maxIterations; iter++) {
			//reset pos
			//calc initial motion from input angle and current testVelocity
			hitGround = true;
			posX = 0.f;
			posY = 0.f;
			motX = Trig.sinDegrees(angle) * testVelocity * 0.05f;
			motY = Trig.cosDegrees(angle) * testVelocity * 0.05f;
			while (motY >= 0 || posY >= y) {
				//move
				//check hit
				//apply gravity if not hit
				posX += motX;
				posY += motY;
				if (posX > x) {
					hitGround = false;
					break;//missile went too far
				}
				motY -= gravityTick;
			}
			if (hitGround)//if break was triggered by going negative on y axis, get a more precise hit vector
			{
				motY += gravityTick;
				hitDiffX = motX - posX;
				hitDiffY = motY - posY;
				hitPercent = (y - posY) / hitDiffY;
				hitX = posX + hitDiffX * hitPercent;
				hitY = posY + +hitDiffY * hitPercent;
			}
			if (hitGround && hitX < x)// hit was not far enough, increase power
			{
				bestVelocity = testVelocity;
				testVelocity += velocityIncrement;
			} else//it was too far, go back to previous power, decrease increment, increase by new increment
			{
				testVelocity -= velocityIncrement;
				bestVelocity = testVelocity;
				velocityIncrement *= 0.5f;
				testVelocity += velocityIncrement;
			}
		}
		return bestVelocity;
	}

	public static boolean isAngleBetween(float test, float min, float max) {
		test = wrapTo360(test);
		min = wrapTo360(min);
		max = wrapTo360(max);
		if (min > max) {
			return test >= min || test <= max;
		}
		return test >= min && test <= max;
	}

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
}
