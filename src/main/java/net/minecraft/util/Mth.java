package net.minecraft.util;

/**
 * [1.7.10] Compatibility stub for 1.21 Mth (math utility class).
 */
public class Mth
{
    public static double clamp(double value, double min, double max) { return Math.min(Math.max(value, min), max); }
    public static float clamp(float value, float min, float max) { return Math.min(Math.max(value, min), max); }
    public static int clamp(int value, int min, int max) { return Math.min(Math.max(value, min), max); }
    public static int floor(double value) { return (int) Math.floor(value); }
    public static int ceil(double value) { return (int) Math.ceil(value); }
    public static float lerp(float delta, float start, float end) { return start + delta * (end - start); }
    public static double lerp(double delta, double start, double end) { return start + delta * (end - start); }
    public static int abs(int value) { return Math.abs(value); }
    public static float abs(float value) { return Math.abs(value); }
    public static int square(int value) { return value * value; }
    public static float sqrt(float value) { return (float) Math.sqrt(value); }
}

