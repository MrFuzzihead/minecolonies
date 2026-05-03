package com.minecolonies.core.client.render.worldevent;
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

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.Util;
// [1.7.10] client removed (use @SideOnly)
// [1.7.10] client removed (use @SideOnly)
// [1.7.10] client removed (use @SideOnly)
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderStateShard.DepthTestStateShard;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.util.function.Function;

public class RenderTypes
{
    /**
     * Usable for rendering simple flat textures
     * 
     * @param  resLoc texture location
     * @return        render type
     */
    public static RenderType worldEntityIcon(final ResourceLocation resLoc)
    {
        return InnerRenderTypes.WORLD_ENTITY_ICON.apply(resLoc);
    }

    /**
     * Used to draw overlay lines that only appear outside existing blocks.
     */
    public static RenderType LINES_OUTSIDE_BLOCKS = InnerRenderTypes.LINES_OUTSIDE_BLOCKS;

    /**
     * Used to draw overlay lines that only appear inside existing blocks.
     */
    public static RenderType LINES_INSIDE_BLOCKS = InnerRenderTypes.LINES_INSIDE_BLOCKS;

    public static final class InnerRenderTypes extends RenderType
    {
        private static final DepthTestStateShard ALWAYS_DEPTH_TEST = new AlwaysDepthTestStateShard();
        private static final DepthTestStateShard GREATER_DEPTH_TEST = new DepthTestStateShard(">", GL11.GL_GREATER);

        private InnerRenderTypes(final String nameIn,
            final VertexFormat formatIn,
            final VertexFormat.Mode drawModeIn,
            final int bufferSizeIn,
            final boolean useDelegateIn,
            final boolean needsSortingIn,
            final Runnable setupTaskIn,
            final Runnable clearTaskIn)
        {
            super(nameIn, formatIn, drawModeIn, bufferSizeIn, useDelegateIn, needsSortingIn, setupTaskIn, clearTaskIn);
            throw new IllegalStateException();
        }

        private static final Function<ResourceLocation, RenderType> WORLD_ENTITY_ICON = Util.memoize((p_173202_) -> {
            return create("minecolonies_entity_icon",
                DefaultVertexFormat.POSITION_TEX,
                VertexFormat.Mode.QUADS,
                1024,
                false,
                true,
                RenderType.CompositeState.builder()
                    .setShaderState(POSITION_TEX_SHADER)
                    .setTextureState(new RenderStateShard.TextureStateShard(p_173202_, false, false))
                    .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                    .setDepthTestState(ALWAYS_DEPTH_TEST)
                    .createCompositeState(false));
        });

        private static final RenderType LINES_OUTSIDE_BLOCKS = create("minecolonies:lines_outside_blocks",
                DefaultVertexFormat.POSITION_COLOR,
                VertexFormat.Mode.TRIANGLES,
                1024,
                false,
                false,
                RenderType.CompositeState.builder()
                        .setTextureState(NO_TEXTURE)
                        .setShaderState(POSITION_COLOR_SHADER)
                        .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                        .setDepthTestState(LEQUAL_DEPTH_TEST)
                        .setCullState(CULL)
                        .setLightmapState(NO_LIGHTMAP)
                        .setOverlayState(NO_OVERLAY)
                        .setLayeringState(NO_LAYERING)
                        .setOutputState(MAIN_TARGET)
                        .setTexturingState(DEFAULT_TEXTURING)
                        .setWriteMaskState(COLOR_WRITE)
                        .createCompositeState(false));

        private static final RenderType LINES_INSIDE_BLOCKS = create("minecolonies:lines_inside_blocks",
                DefaultVertexFormat.POSITION_COLOR,
                VertexFormat.Mode.TRIANGLES,
                1024,
                false,
                false,
                RenderType.CompositeState.builder()
                        .setTextureState(NO_TEXTURE)
                        .setShaderState(POSITION_COLOR_SHADER)
                        .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                        .setDepthTestState(GREATER_DEPTH_TEST)
                        .setCullState(CULL)
                        .setLightmapState(NO_LIGHTMAP)
                        .setOverlayState(NO_OVERLAY)
                        .setLayeringState(NO_LAYERING)
                        .setOutputState(MAIN_TARGET)
                        .setTexturingState(DEFAULT_TEXTURING)
                        .setWriteMaskState(COLOR_WRITE)
                        .createCompositeState(false));

    }

    private static class AlwaysDepthTestStateShard extends DepthTestStateShard
    {
        private AlwaysDepthTestStateShard()
        {
            super("true_always", -1);
            setupState = () -> {
                RenderSystem.enableDepthTest();
                RenderSystem.depthFunc(GL11.GL_ALWAYS);
            };
        }
    }
}


