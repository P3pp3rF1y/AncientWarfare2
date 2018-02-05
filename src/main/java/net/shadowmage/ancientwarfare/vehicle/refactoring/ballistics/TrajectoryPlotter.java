package net.shadowmage.ancientwarfare.vehicle.refactoring.ballistics;

import net.minecraft.util.math.MathHelper;
import net.shadowmage.ancientwarfare.core.config.AWLog;
import net.shadowmage.ancientwarfare.core.util.Trig;

public class TrajectoryPlotter {

    /*
     * @param dx       delta X (diff between launch and target position)
     * @param dy       delta Y (diff between launch and target position)
     * @param dz       delta Z (diff between launch and target position)
     * @param angle    the specified launch angle (single, non-adjustable)
     * @param maxPower the specified max launch power
     * @return power needed to hit target, or 0 if beyond maxPower value
     */
    public static final double getPowerToHit(double dx, double dy, double dz, float angle, float maxPower) {
        double len = MathHelper.sqrt(dx * dx + dz * dz);
        double pow = getLaunchPowerToHit(len, dy, angle, 10);
        if (pow <= maxPower) {
            return pow;
        }
        return 0;//TODO what to return for an invalid power setting?
    }

    public static final double getAngleToHit(double dx, double dy, double dz, float minAngle, float maxAngle, float velocity) {
        double len = MathHelper.sqrt(dx * dx + dz * dz);
        double[] angles = getLaunchAngleToHit(len, dy, velocity);
        if (angles[0] >= minAngle && angles[0] <= maxAngle) {
            return angles[0];
        } else if (angles[1] >= minAngle && angles[1] <= maxAngle) {
            return angles[1];
        }
        return 0;//TODO what to return for an invalid angle?
    }

    /*
     * TODO
     * gets the max effective range of the unit at a given firing angle
     */
    public static double getMaxRange(float angle, float v) {
        return 0;
    }

    /*
     * @param x input hit x (horizontal distance)
     * @param y input hit y (vertical distance)
     * @param v velocity per second
     * @return an array containing the two launch angles that can hit the specified position
     */
    private static double[] getLaunchAngleToHit(double x, double y, double v) {
        double v2 = v * v;
        double v4 = v * v * v * v;
        double x2 = x * x;
        double sqRtVal = Math.sqrt(v4 - Trig.GRAVITY * (Trig.GRAVITY * x2 + 2 * y * v2));
        double h = v2 + sqRtVal;
        double l = v2 - sqRtVal;
        h /= Trig.GRAVITY * x;
        l /= Trig.GRAVITY * x;
        h = Trig.toDegrees((float) Math.atan(h));
        l = Trig.toDegrees((float) Math.atan(l));
        return new double[]{h, l};
    }

    /*
     * iterative finder -- uses derivation of motion equations rather than simulation of ballistic trajectory
     *
     * @return last tested launch velocity closest to actual destination velocity
     */
    private static double getLaunchPowerToHit(double tx, double ty, double angle, int iterations) {
        double testPower = 10;
        double powerInc = 10;
        double y = 0;
        for (int i = 0; i < iterations; i++) {
            y = getProjectileHeight(angle, testPower, tx);
            if (Math.abs(ty - y) < 0.01d) {
                return testPower;
            }//if result is really close to dest, return result
            if (y > ty)//else too far, decrease the increment
            {
                testPower -= powerInc;//revert to previous power (or 0 if first iteration)
                powerInc *= 0.5d;//decrese increment, so next iteration less power is tested
            }
            //implicit else not far enough, increase power by raw increment
            testPower += powerInc;
        }
        return testPower;
    }

    /*
     * http://en.wikipedia.org/wiki/Trajectory_of_a_projectile#Angle_.CE.B8_required_to_hit_coordinate_.28x.2Cy.29
     * return the height of the projectile at a given distance from launch<br>
     * used by iterative finder to 'cheat' doing exact duplications of trajectory
     */
    private static double getProjectileHeight(double angle, double velocity, double x) {
        double g = Trig.GRAVITY;
        double sinA = Math.sin(Trig.TORADIANS * angle);//used to determine vertical component
        double cosA = Math.cos(Trig.TORADIANS * angle);//used to determine horizontal component
        double vx = (cosA * velocity);
        double vy = (sinA * velocity);
        double t = x / vx;//time on horizontal to reach x position
        double t2 = (t * t);
        double g12 = g / 2.d;
        /*
         * y = v0y t - 1/2 gt ^2
         */
        double y = vy * t - g12 * t2;
        return y;
    }

    /*
     * http://en.wikipedia.org/wiki/Trajectory_of_a_projectile#Angle_.CE.B8_required_to_hit_coordinate_.28x.2Cy.29
     * return the height of the projectile at a given distance from launch<br>
     * used by iterative finder to 'cheat' doing exact duplications of trajectory
     */
    private static double getProjectileHeight2(double angle, double v, double x) {
        double g = Trig.GRAVITY;
        double cosA = Math.cos(Trig.TORADIANS * angle);//used to determine horizontal component
        double a = 0;
        double b = x * Math.tan(Trig.TORADIANS * angle);
        double c = g * (x * x);
        double d = 2 * Math.pow(v * cosA, 2);
        return a + b - (c / d);
    }

    public static void loadTest() {
        double targetX = 190;
        double targetY = 00;
        double basePower = 50;

        long t1 = System.nanoTime();
        double[] angles = getLaunchAngleToHit(targetX, targetY, basePower);
        long t2 = System.nanoTime();
        double p1 = getLaunchPowerToHit(targetX, targetY, angles[0], 20);
        long t3 = System.nanoTime();
        double p2 = getLaunchPowerToHit(targetX, targetY, angles[1], 20);
        long t4 = System.nanoTime();

        long d1 = (t2 - t1);
        long d2 = (t3 - t2);
        long d3 = (t4 - t3);

        AWLog.logDebug("a/p : " + angles[0] + " : " + angles[1] + " :: " + p1 + " : " + p2);
        AWLog.logDebug("times: " + d1 + " :: " + d2 + " :: " + d3);
    }

}
