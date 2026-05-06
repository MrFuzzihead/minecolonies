package com.mojang.blaze3d.platform;
/** [1.7.10 bridge] NativeImage */
public class NativeImage implements AutoCloseable {
    public static NativeImage read(java.io.InputStream stream) throws java.io.IOException { return new NativeImage(); }
    public int getWidth() { return 0; }
    public int getHeight() { return 0; }
    public void close() {}
}