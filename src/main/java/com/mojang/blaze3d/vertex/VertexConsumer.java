package com.mojang.blaze3d.vertex;

/** [1.7.10 stub] VertexConsumer - 1.21 rendering API */
public interface VertexConsumer
{
    VertexConsumer vertex(double x, double y, double z);
    VertexConsumer color(int r, int g, int b, int a);
    VertexConsumer uv(float u, float v);
    VertexConsumer overlayCoords(int u, int v);
    VertexConsumer uv2(int u, int v);
    VertexConsumer normal(float x, float y, float z);
    void endVertex();
}

