package net.minecraft.client.model.geom;

/** [1.7.10 stub] ModelLayerLocation - identifies a model layer */
public class ModelLayerLocation
{
    private final net.minecraft.util.ResourceLocation model;
    private final String layer;

    public ModelLayerLocation(net.minecraft.util.ResourceLocation model, String layer)
    {
        this.model = model;
        this.layer = layer;
    }

    public net.minecraft.util.ResourceLocation getModel() { return model; }
    public String getLayer() { return layer; }
}

