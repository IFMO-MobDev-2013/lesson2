package ru.zulyaev.ifmo.android.lesson2.util;

/**
 * @author Никита
 */
public final class ColorUtils {
    private ColorUtils() {
    }

    public static float[] toHSLA(int color, float[] buffer) {
        float a = (color >> 24 & 0xFF) / 255f;
        float r = (color >> 16 & 0xFF) / 255f;
        float g = (color >> 8 & 0xFF) / 255f;
        float b = (color & 0xFF) / 255f;

        float max = MathUtils.max(r, g, b);
        float min = MathUtils.min(r, g, b);
        float h, s, l = (max + min) / 2;
        float diff = max - min;

        if (max == min) {
            h = s = 0;
        } else {
            s = l > 0.5f ? diff / (2 - max - min) : diff / (max + min);

            if (max == r) {
                h = (g - b) / diff + (g < b ? 6 : 0);
            } else if (max == g) {
                h = (b - r) / diff + 2;
            } else {
                h = (r - g) / diff + 4;
            }

            h /= 6;
        }

        buffer[0] = h;
        buffer[1] = s;
        buffer[2] = l;
        buffer[3] = a;

        return buffer;
    }

    public static int lighten(float[] hsla, float lightness) {
        hsla[2] = MathUtils.bound(hsla[2] + lightness, 0, 1);
        return hsla(hsla);
    }

    private static float hue(float h, float m1, float m2) {
        h = h < 0 ? h + 1 : (h > 1 ? h - 1 : h);
        if (h * 6 < 1) return m1 + (m2 - m1) * h * 6;
        else if (h * 2 < 1) return m2;
        else if (h * 3 < 2) return m1 + (m2 - m1) * (2f / 3f - h) * 6;
        else return m1;
    }

    public static int hsla(float h, float s, float l, float a) {
        float m2 = l <= 0.5f ? l * (s + 1) : l + s - l * s;
        float m1 = l * 2 - m2;
        return rgba(
                    Math.round(hue(h + 1f/3f, m1, m2) * 255),
                    Math.round(hue(h, m1, m2) * 255),
                    Math.round(hue(h - 1f/3f, m1, m2) * 255),
                    Math.round(a * 255)
                );

    }

    public static int hsla(float[] hlsa) {
        return hsla(hlsa[0], hlsa[1], hlsa[2], hlsa[3]);
    }

    public static int rgba(int r, int g, int b, int a) {
        return a << 24 | r << 16 | g << 8 | b;
    }
}
