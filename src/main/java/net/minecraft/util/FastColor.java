package net.minecraft.util;
/** [1.7.10 stub] FastColor */
public class FastColor {
    public static int argb32(int a, int r, int g, int b) { return (a << 24) | (r << 16) | (g << 8) | b; }
    public static int alpha(int color) { return (color >> 24) & 0xFF; }
    public static int red(int color) { return (color >> 16) & 0xFF; }
    public static int green(int color) { return (color >> 8) & 0xFF; }
    public static int blue(int color) { return color & 0xFF; }
    public static class ARGB32 {
        public static int color(int a, int r, int g, int b) { return (a << 24) | (r << 16) | (g << 8) | b; }
    }
}