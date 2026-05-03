package com.minecolonies.core.client.render;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.BoneMealItem;
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

import com.ldtteam.structurize.blocks.ModBlocks;
import com.minecolonies.core.tileentities.TileEntityColonyFlag;
import com.minecolonies.core.blocks.decorative.BlockColonyFlagBanner;
import com.minecolonies.core.blocks.decorative.BlockColonyFlagWallBanner;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.minecolonies.api.util.Tuple;
import com.mojang.math.Axis;
// [1.7.10] client removed (use @SideOnly)
// [1.7.10] client removed (use @SideOnly)
// [1.7.10] Holder removed
import net.minecraft.world.item.ItemDisplayContext;
// [1.7.10] BlockState -> int metadata
// [1.7.10] client removed (use @SideOnly)
// [1.7.10] client removed (use @SideOnly)
// [1.7.10] client removed (use @SideOnly)
// [1.7.10] client removed (use @SideOnly)
// [1.7.10] client removed (use @SideOnly)
// [1.7.10] client removed (use @SideOnly)
// [1.7.10] client removed (use @SideOnly)
// [1.7.10] client removed (use @SideOnly)
import net.minecraft.world.item.BannerItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.item.ItemStack;
// [1.7.10] block.entity removed
// [1.7.10] int[] -> int x,y,z
import net.minecraft.util.MathHelper;
import net.minecraft.world.level.GameType;
import net.minecraft.client.model.geom.ModelPart;

import java.util.List;

/**
 * The custom renderer to render the colony flag patterns if they exist,
 * and a placeholder marker if in Creative mode.
 */
public class TileEntityColonyFlagRenderer implements BlockEntityRenderer<TileEntityColonyFlag>
{
    private final ModelPart cloth;
    private final ModelPart standPost;
    private final ModelPart crossbar;

    public TileEntityColonyFlagRenderer(final BlockEntityRendererProvider.Context context)
    {
        super();
        ModelPart modelpart = context.bakeLayer(ModelLayers.BANNER);
        this.cloth = modelpart.getChild("flag");
        this.standPost = modelpart.getChild("pole");
        this.crossbar = modelpart.getChild("bar");
    }

    @Override
    public void render(TileEntityColonyFlag flagIn, float partialTicks, PoseStack transform, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn)
    {
        List<Pair<Holder<BannerPattern>, DyeColor>> list = flagIn.getPatterns();

        boolean noWorld = flagIn.getLevel() == null;
        transform.pushPose();
        long i;
        if (noWorld)
        {
            i = 0L;
            transform.translate(0.5D, 0.5D, 0.5D);
            this.standPost.visible = true;
        }
        else
        {

            i = flagIn.getLevel().getGameTime();
            BlockState blockstate = flagIn.getBlockState();
            if (blockstate.getBlock() instanceof BlockColonyFlagBanner)
            {
                transform.translate(0.5D, 0.5D, 0.5D);
                float f1 = (float)(-blockstate.getValue(BlockColonyFlagBanner.ROTATION) * 360) / 16.0F;
                transform.mulPose(Axis.YP.rotationDegrees(f1));
                this.standPost.visible = true;
            }
            else if (blockstate.getBlock() instanceof BlockColonyFlagWallBanner)
            {
                transform.translate(0.5D, -0.16666667F, 0.5D);
                float f3 = -blockstate.getValue(BlockColonyFlagWallBanner.HORIZONTAL_FACING).toYRot();
                transform.mulPose(Axis.YP.rotationDegrees(f3));
                transform.translate(0.0D, -0.3125D, -0.4375D);
                this.standPost.visible = false;
            }

            Minecraft mc = Minecraft.getInstance();
            if (mc.player.getMainHandItem().getItem() instanceof BannerItem
             && mc.gameMode.getPlayerMode() == GameType.CREATIVE)
            {
                transform.pushPose();
                ItemStack placeholder = new ItemStack(ModBlocks.blockSubstitution.get());

                transform.translate(0.0D, 0.5D, 0.0D);
                transform.scale(0.75F, 0.75F, 0.75F);
                Minecraft.getInstance().getItemRenderer().renderStatic(placeholder, ItemDisplayContext.FIXED, combinedLightIn, combinedOverlayIn, transform, bufferIn, mc.World, OverlayTexture.NO_OVERLAY);
                transform.popPose();
            }
        }

        transform.pushPose();
        transform.scale(2/3F, -2/3F, -2/3F);
        VertexConsumer ivertexbuilder = ModelBakery.BANNER_BASE.buffer(bufferIn, RenderType::entitySolid);
        this.standPost.render(transform, ivertexbuilder, combinedLightIn, combinedOverlayIn);
        this.crossbar.render(transform, ivertexbuilder, combinedLightIn, combinedOverlayIn);
        int[] blockPos = flagIn.getBlockPos();
        float f2 = ((float)Math.floorMod((long)(blockPos[0] * 7 + blockPos[1] * 9 + blockPos[2] * 13) + i, 100L) + partialTicks) / 100.0F;
        this.cloth.xRot = (-0.0125F + 0.01F * Mth.cos(((float)Math.PI * 2F) * f2)) * (float)Math.PI;
        this.cloth.y = -32.0F;
        BannerRenderer.renderPatterns(transform, bufferIn, combinedLightIn, combinedOverlayIn, this.cloth, ModelBakery.BANNER_BASE, true, list);
        transform.popPose();
        transform.popPose();
    }
}





