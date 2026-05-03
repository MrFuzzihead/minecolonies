package net.minecraft.client.model;

/**
 * [1.7.10] Compatibility stub for 1.21 CubeListBuilder.
 */
public class CubeListBuilder
{
    public static CubeListBuilder create() { return new CubeListBuilder(); }

    public CubeListBuilder texOffs(int u, int v) { return this; }
    public CubeListBuilder addBox(float x, float y, float z, float sizeX, float sizeY, float sizeZ) { return this; }
    public CubeListBuilder addBox(float x, float y, float z, float sizeX, float sizeY, float sizeZ, CubeDeformation deformation) { return this; }
    public CubeListBuilder addBox(float x, float y, float z, float sizeX, float sizeY, float sizeZ, CubeDeformation deformation, float u, float v) { return this; }
    public CubeListBuilder mirror() { return this; }
}

