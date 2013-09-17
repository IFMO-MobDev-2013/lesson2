package ru.zulyaev.ifmo.android.lesson2.util;

/**
 * @author Никита
 */
public final class MathUtils {
    private MathUtils() {
    }

    public static int gcd(int a, int b) {
        return b == 0 ? a : gcd(b, a % b);
    }

    public static int lcm(int a, int b) {
        return a / gcd(a, b) * b;
    }

    public static float bound(float value, float min, float max) {
        if (value < min) return min;
        return value > max ? max : value;
    }

    public static float max(float a, float b, float c) {
        if (a > b) {
            return a > c ? a : c;
        } else {
            return b > c ? b : c;
        }
    }

    public static float min(float a, float b, float c) {
        if (a < b) {
            return a < c ? a : c;
        } else {
            return b < c ? b : c;
        }
    }
}
