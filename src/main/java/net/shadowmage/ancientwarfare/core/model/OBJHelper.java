package net.shadowmage.ancientwarfare.core.model;

import com.sun.javafx.geom.Vec3f;

import static java.lang.Math.cos;
import static java.lang.Math.sin;

public class OBJHelper {


    public static Vec3f rotatePoint(Vec3f point, Vec3f rotationAngles) {
        Vec3f rotated = new Vec3f(point);

        rotated = rotateAroundX(rotated, rotationAngles.x);
        rotated = rotateAroundY(rotated, rotationAngles.y);
        rotated = rotateAroundZ(rotated, rotationAngles.z);

        return rotated;
    }

    //PI / 90 below is to just match what appears to be something doubling degrees in ModelBaseAW
    public static Vec3f rotateAroundX(Vec3f src, float rotation) {
        double sin = sin(rotation * (Math.PI / 90f));
        double cos = cos(rotation * (Math.PI / 90f));

        Vec3f dst = new Vec3f();

        float y = src.y, z = src.z;
        dst.x = src.x;
        dst.y = (float) (cos * y - sin * z);
        dst.z = (float) (sin * y + cos * z);

        return dst;
    }

    public static Vec3f rotateAroundY(Vec3f src, float rotation) {
        double sin = sin(rotation * (Math.PI / 90f));
        double cos = cos(rotation * (Math.PI / 90f));

        Vec3f dst = new Vec3f();

        float x = src.x, z = src.z;
        dst.x = (float) (cos * x - sin * z);
        dst.y = src.y;
        dst.z = (float) (sin * x + cos * z);

        return dst;
    }

    public static Vec3f rotateAroundZ(Vec3f src, float rotation) {
        double sin = sin(rotation * (Math.PI / 90f));
        double cos = cos(rotation * (Math.PI / 90f));

        Vec3f dst = new Vec3f();

        float x = src.x, y = src.y;
        dst.x = (float) (cos * x - sin * y);
        dst.y = (float) (sin * x + cos * y);
        dst.z = src.z;

        return dst;
    }
}
