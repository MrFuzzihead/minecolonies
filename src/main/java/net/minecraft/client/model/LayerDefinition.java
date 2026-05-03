package net.minecraft.client.model;

import net.minecraft.client.model.geom.ModelPart;

/**
 * [1.7.10] Compatibility stub for 1.21 LayerDefinition.
 */
public class LayerDefinition
{
    public static LayerDefinition create(MeshDefinition mesh, int textureWidth, int textureHeight)
    {
        return new LayerDefinition();
    }

    public ModelPart bakeRoot() { return new ModelPart(); }
}

