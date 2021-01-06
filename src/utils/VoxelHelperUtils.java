package utils;

import core.math.Vec3f;
import core.math.Vec4f;

public class VoxelHelperUtils {


    static private float max(float x, float y, float z){
        return Math.max(Math.max(x, y), z);
    }

    public static Vec3f mix(Vec3f p0, Vec3f p1, float t) {
        return p0.add((p1.sub(p0)).mul(t)); // p0 + ((p1 - p0) * t);
    }

    public static Vec4f mix(Vec4f p0, Vec4f p1, float t) {
        return p0.add((p1.sub(p0)).mul(t)); // p0 + ((p1 - p0) * t);
    }

    public static boolean isOutFromBounds(Vec3f p, Vec3f min, int size) {
        return  p.X < min.X || p.X > (min.X + size) ||
                p.Y < min.Y || p.Y > (min.Y + size) ||
                p.Z < min.Z || p.Z > (min.Z + size);
    }

    public static float clamp(float val, float min, float max) {
        return Math.max(min, Math.min(max, val));
    }

    public static int log2(int N) {
        return (int) (Math.log(N) / Math.log(2));
    }

    public static Vec3f ColourForMinLeafSize(int minLeafSize) {
        switch (minLeafSize) {
            case 1:
                return new Vec3f(0.3f, 0.1f, 0.f);
            case 2:
                return new Vec3f(0, 0.f, 0.5f);
            case 4:
                return new Vec3f(0, 0.5f, 0.5f);
            case 8:
                return new Vec3f(0.5f, 0.f, 0.5f);
            case 16:
                return new Vec3f(0.0f, 0.5f, 0.f);
            default:
                return new Vec3f(0.5f, 0.0f, 0.f);
        }
    }
}