package com.minecolonies.core.client.render;
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
import com.minecolonies.core.client.model.SpearModel;
import com.mojang.blaze3d.vertex.PoseStack;
// [1.7.10] client removed (use @SideOnly)
// [1.7.10] client removed (use @SideOnly)
// [1.7.10] client removed (use @SideOnly)
// [1.7.10] client removed (use @SideOnly)
// [1.7.10] client removed (use @SideOnly)
// [1.7.10] client removed (use @SideOnly)
import net.minecraft.util.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@OnlyIn(Dist.CLIENT)
public class SpearItemTileEntityRenderer extends BlockEntityWithoutLevelRenderer
{
    private static final ResourceLocation TEXTURE = new ResourceLocation(Constants.MOD_ID, "textures/entity/spear.png");
    private              SpearModel       model;
    private final        EntityModelSet   set;

    public SpearItemTileEntityRenderer()
    {
        super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
        this.set = Minecraft.getInstance().getEntityModels();
        this.model = new SpearModel(this.set.bakeLayer(ModelLayers.TRIDENT));
    }

    @Override
    public void onResourceManagerReload(ResourceManager manager)
    {
        this.model = new SpearModel(this.set.bakeLayer(ModelLayers.TRIDENT));
    }

    @Override
    public void renderByItem(
      final ItemStack stack,
      final ItemDisplayContext displayContext,
      final PoseStack matrixStack,
      final MultiBufferSource buffer,
      final int combinedLight,
      final int combinedOverlay)
    {
        matrixStack.pushPose();
        model.renderToBuffer(matrixStack, ItemRenderer.getFoilBuffer(buffer, model.renderType(TEXTURE), false, stack.hasFoil()), combinedLight, combinedOverlay, 1, 1, 1, 1);
        matrixStack.popPose();
    }
}


