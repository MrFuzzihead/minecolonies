package com.mojang.blaze3d.vertex;
/** [1.7.10 stub] Tesselator */
public class Tesselator { private static final Tesselator INSTANCE = new Tesselator(); public static Tesselator getInstance() { return INSTANCE; } public BufferBuilder getBuilder() { return new BufferBuilder(1024); } public void end() {} }