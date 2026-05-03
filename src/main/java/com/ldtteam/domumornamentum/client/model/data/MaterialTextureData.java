package com.ldtteam.domumornamentum.client.model.data;

import net.minecraft.util.ResourceLocation;
import java.util.Collections;
import java.util.Map;

/**
 * [1.7.10] Compatibility stub for DomumOrnamentum MaterialTextureData.
 */
public class MaterialTextureData
{
    public static final MaterialTextureData EMPTY = new MaterialTextureData(Collections.emptyMap());

    private final Map<ResourceLocation, Object> texturedComponents;

    public MaterialTextureData(final Map<ResourceLocation, Object> texturedComponents)
    {
        this.texturedComponents = texturedComponents;
    }

    public Map<ResourceLocation, Object> getTexturedComponents()
    {
        return texturedComponents;
    }
}

