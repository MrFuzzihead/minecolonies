package net.minecraftforge.client.event;

import net.minecraft.client.model.geom.ModelLayerLocation;
import java.util.function.Supplier;

/** [1.7.10 bridge] EntityRenderersEvent - no equivalent in 1.7.10 Forge */
public class EntityRenderersEvent
{
    public static class RegisterLayerDefinitions extends EntityRenderersEvent
    {
        public void registerLayerDefinition(final ModelLayerLocation location, final Supplier<?> supplier) {}
    }

    public static class RegisterRenderers extends EntityRenderersEvent
    {
    }
}

