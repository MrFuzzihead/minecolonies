package com.minecolonies.core.client.render.mobs.norsemen;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayerParent;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.api.distmarker.Dist;

import com.minecolonies.api.entity.mobs.AbstractEntityMinecoloniesMonster;
import com.minecolonies.core.client.model.raiders.ModelChiefNorsemen;
import com.minecolonies.core.event.ClientRegistryHandler;
// [1.7.10] client removed (use @SideOnly)
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.NotNull;

/**
 * Renderer used for Chief norsemen.
 */
public class RendererChiefNorsemen extends AbstractRendererNorsemen<AbstractEntityMinecoloniesMonster, ModelChiefNorsemen>
{
    /**
     * Texture of the entity.
     */
    private static final ResourceLocation TEXTURE = new ResourceLocation("minecolonies:textures/entity/raiders/norsemen_chief.png");

    /**
     * Constructor method for renderer
     *
     * @param context the renderManager
     */
    public RendererChiefNorsemen(final EntityRendererProvider.Context context)
    {
        super(context, new ModelChiefNorsemen(context.bakeLayer(ClientRegistryHandler.NORSEMEN_CHIEF)), 0.5F);
    }

    @NotNull
    @Override
    public ResourceLocation getTextureLocation(final AbstractEntityMinecoloniesMonster entity)
    {
        return TEXTURE;
    }
}


