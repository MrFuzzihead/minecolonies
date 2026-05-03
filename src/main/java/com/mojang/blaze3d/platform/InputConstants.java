package com.mojang.blaze3d.platform;

/**
 * [1.7.10] Compatibility stub for Blaze3D InputConstants.
 */
public class InputConstants
{
    public static final int UNKNOWN_KEY = -1;

    public static boolean isKeyDown(long handle, int key) { return false; }
    public static Key getKey(int keyCode, int scanCode) { return new Key(keyCode, scanCode); }

    public static class Key
    {
        private final int value;
        private final int scanCode;
        public Key(int value, int scanCode) { this.value = value; this.scanCode = scanCode; }
        public int getValue() { return value; }
    }
}

