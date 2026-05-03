package com.minecolonies.core.client.render.projectile;
import com.mojang.math.Axis;
import com.mojang.math.Pose;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.EntityModelSet;
import net.minecraft.world.item.BannerItem;
import net.minecraft.world.level.GameType;
import net.minecraft.network.chat.FormattedCharSequence;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.client.renderer.blockentity.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.ThrownTrident;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.UseAnim;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.api.distmarker.Dist;
import com.mojang.math.Pose;
import com.mojang.blaze3d.systems.RenderSystem;

import com.minecolonies.api.util.constant.Constants;
// [1.7.10] client removed (use @SideOnly)
// [1.7.10] client removed (use @SideOnly)
// [1.7.10] world.entity removed
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.NotNull;

/**
 * Custom renderer for the fire arrows.
 */
public class FireArrowRenderer extends ArrowRenderer<AbstractArrow>
{
    /**
     * Array of different textures.
     */
    private static final ResourceLocation[] RES = new ResourceLocation[]
                                                    {
                                                      new ResourceLocation(Constants.MOD_ID, "textures/item/magicalarrows/magical_arrow1.png"),
                                                      new ResourceLocation(Constants.MOD_ID, "textures/item/magicalarrows/magical_arrow2.png"),
                                                      new ResourceLocation(Constants.MOD_ID, "textures/item/magicalarrows/magical_arrow3.png"),
                                                      new ResourceLocation(Constants.MOD_ID, "textures/item/magicalarrows/magical_arrow4.png"),
                                                      new ResourceLocation(Constants.MOD_ID, "textures/item/magicalarrows/magical_arrow5.png"),
                                                      new ResourceLocation(Constants.MOD_ID, "textures/item/magicalarrows/magical_arrow6.png")
                                                    };

    public FireArrowRenderer(final EntityRendererProvider.Context context)
    {
        super(context);
    }

    @NotNull
    @Override
    public ResourceLocation getTextureLocation(@NotNull final AbstractArrow entity)
    {
        return RES[entity.tickCount % 6];
    }
}



