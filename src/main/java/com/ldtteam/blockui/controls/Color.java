package com.ldtteam.blockui.controls;

/**
 * [1.7.10] Compatibility stub for BlockUI Color utility.
 */
public class Color
{
    public static int rgbaToInt(int r, int g, int b, int a) { return (a << 24) | (r << 16) | (g << 8) | b; }
    public static int rgbToInt(int r, int g, int b) { return (255 << 24) | (r << 16) | (g << 8) | b; }
}

