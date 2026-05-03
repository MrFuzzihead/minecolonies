package com.minecolonies.core.client.render;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.RenderType;
import com.mojang.math.Pose;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.Material;
import net.minecraft.client.model.BookModel;
import net.minecraft.client.renderer.blockentity.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.GameType;
import net.minecraft.network.chat.FormattedCharSequence;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Matrix4f;
import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.ThrownTrident;
import net.minecraft.client.EntityModelSet;
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

import com.minecolonies.core.tileentities.TileEntityColonyBuilding;
import com.minecolonies.core.tileentities.TileEntityEnchanter;
import com.minecolonies.api.util.constant.Constants;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
// [1.7.10] client removed (use @SideOnly)
// [1.7.10] client removed (use @SideOnly)
// [1.7.10] client removed (use @SideOnly)
// [1.7.10] client removed (use @SideOnly)
// [1.7.10] client removed (use @SideOnly)
// [1.7.10] client removed (use @SideOnly)
// [1.7.10] client removed (use @SideOnly)
// [1.7.10] client removed (use @SideOnly)
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.MathHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class TileEntityEnchanterRenderer implements BlockEntityRenderer<TileEntityColonyBuilding>
{
    public static final Material TEXTURE_BOOK;
    static
    {
        TEXTURE_BOOK = new Material(TextureAtlas.LOCATION_BLOCKS, new ResourceLocation(Constants.MOD_ID, "block/enchanting_table_book"));
    }

    /**
     * The book model to be rendered.
     */
    private final BookModel modelBook;

    /**
     * Create the renderer.
     *
     * @param context the context.
     */
    public TileEntityEnchanterRenderer(final BlockEntityRendererProvider.Context context)
    {
        super();
        this.modelBook = new BookModel(context.bakeLayer(ModelLayers.BOOK));
    }

    @Override
    public void render(
      @NotNull final TileEntityColonyBuilding ent,
      float partialTicks,
      @NotNull final PoseStack matrixStack,
      @NotNull final MultiBufferSource renderTypeBuffer,
      final int lightA,
      final int lightB)
    {
        if (ent instanceof TileEntityEnchanter)
        {
            final TileEntityEnchanter entity = (TileEntityEnchanter) ent;
            matrixStack.pushPose();
            matrixStack.translate(0.5D, 0.75D, 0.5D);
            float tick = (float) entity.tickCount + partialTicks;
            matrixStack.translate(0.0D, (0.1F + Mth.sin(tick * 0.1F) * 0.01F), 0.0D);

            double rotVPrev = entity.bookRotation - entity.bookRotationPrev;
            float circleRot = (float) ((rotVPrev + Math.PI % (2 * Math.PI)) - Math.PI);

            float tickBasedRot = entity.bookRotationPrev + circleRot * partialTicks;
            matrixStack.mulPose(Axis.YP.rotation(-tickBasedRot));
            matrixStack.mulPose(Axis.ZP.rotationDegrees(80.0F));
            float pageFlip = Mth.lerp(partialTicks, entity.pageFlipPrev, entity.pageFlip);
            float flipA = Mth.frac(pageFlip + 0.25F) * 1.6F - 0.3F;
            float flipB = Mth.frac(pageFlip + 0.75F) * 1.6F - 0.3F;
            float bookSpread = Mth.lerp(partialTicks, entity.bookSpreadPrev, entity.bookSpread);
            this.modelBook.setupAnim(tick, Mth.clamp(flipA, 0.0F, 1.0F), Mth.clamp(flipB, 0.0F, 1.0F), bookSpread);
            VertexConsumer vertexConsumer = TEXTURE_BOOK.buffer(renderTypeBuffer, RenderType::entitySolid);
            this.modelBook.renderToBuffer(matrixStack, vertexConsumer, lightA, lightB, 1.0F, 1.0F, 1.0F, 1.0F);
            matrixStack.popPose();
        }
    }
}


