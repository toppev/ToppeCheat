package com.toppecraft.toppecheat.utils;

public class MathUtils {

    private static final float PI = (float) Math.PI;
    private static final float PI_2 = PI / 2;
    private static final float MINUS_PI_2 = -PI_2;
    public static boolean fastMath;

    public static double calcDistance(double num1, double num2) {
        return Math.abs(num1 - num2);
    }

    public static double toTwoDecimals(double d) {
        return (int) Math.round(d * 100) / 100.0;
    }

    public static double calcDistanceClamped(double a, double b) {
        return (a - b) < 0 ? 360 % (a - b) : (a - b);
    }

    public static double decimals(double a) {
        return a - Math.floor(a);
    }

    public static final float atan2(float y, float x) {
        if (!fastMath) {
            return (float) Math.atan2(y, x);
        }
        if (x == 0.0f) {
            if (y > 0.0f) {
                return PI_2;
            }
            if (y == 0.0f) {
                return 0.0f;
            }
            return MINUS_PI_2;
        }
        float atan;
        float z = y / x;
        if (Math.abs(z) < 1.0f) {
            atan = z / (1.0f + 0.28f * z * z);
            if (x < 0.0f) {
                return (y < 0.0f) ? atan - PI : atan + PI;
            }
            return atan;
        }
        atan = PI_2 - z / (z * z + 0.28f);
        return (y < 0.0f) ? atan - PI : atan;
    }
}
