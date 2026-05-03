package com.minecolonies.core.client.render;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.BoneMealItem;
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

import com.minecolonies.api.blocks.ModBlocks;
import com.minecolonies.api.blocks.huts.AbstractBlockMinecoloniesDefault;
import com.minecolonies.core.tileentities.TileEntityNamedGrave;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
// [1.7.10] client removed (use @SideOnly)
// [1.7.10] client removed (use @SideOnly)
// [1.7.10] BlockState -> int metadata
// [1.7.10] client removed (use @SideOnly)
// [1.7.10] client removed (use @SideOnly)
// [1.7.10] client removed (use @SideOnly)
// [1.7.10] Direction -> net.minecraft.util.EnumFacing

// [1.7.10] chat.String replaced by IChatComponent/ChatComponentText
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class TileEntityNamedGraveRenderer implements BlockEntityRenderer<TileEntityNamedGrave> {

    /**
     * Basic rotation to achieve a certain direction.
     */
    private static final int BASIC_ROTATION = 90;

    /**
     * Rotate by amount to go east.
     */
    private static final int ROTATE_EAST = 1;

    /**
     * Rotate by amount to go north.
     */
    private static final int ROTATE_NORTH = 2;

    /**
     * Rotate by amount to go west.
     */
    private static final int ROTATE_WEST = 3;

    public TileEntityNamedGraveRenderer(final BlockEntityRendererProvider.Context context)
    {
        super();
    }


    @Override
    public void render(@NotNull final TileEntityNamedGrave tileEntity, final float partialTicks, final PoseStack matrixStack, @NotNull final MultiBufferSource buffer, final int combinedLight, final int combinedOverlay)
    {
        matrixStack.pushPose();

        if(tileEntity != null)
        {
            final BlockState state = tileEntity.getLevel().getBlockState(tileEntity.getBlockPos());
            if (state.getBlock() == ModBlocks.blockNamedGrave)
            {
                final Direction facing = state.getValue(AbstractBlockMinecoloniesDefault.FACING);
                switch (facing)
                {
                    case NORTH:
                        matrixStack.translate(0.5f, 1.18F, 0.48F); //in front of the center point of the name plate
                        matrixStack.scale(0.006F, -0.006F, 0.006F); //size of the text font
                        matrixStack.mulPose(Axis.YP.rotationDegrees(BASIC_ROTATION * ROTATE_NORTH));
                        break;
                    case SOUTH:
                        matrixStack.translate(0.5f, 1.18F, 0.54F); //in front of the center point of the name plate
                        matrixStack.scale(0.006F, -0.006F, 0.006F); //size of the text font
                        //don't rotate at all.
                        break;

                    case EAST:
                        matrixStack.translate(0.54f, 1.18F, 0.5F); //in front of the center point of the name plate
                        matrixStack.scale(0.006F, -0.006F, 0.006F); //size of the text font
                        matrixStack.mulPose(Axis.YP.rotationDegrees(BASIC_ROTATION * ROTATE_EAST));
                        break;
                    case WEST:
                        matrixStack.translate(0.48f, 1.18F, 0.5F); //in front of the center point of the name plate
                        matrixStack.scale(0.006F, -0.006F, 0.006F); //size of the text font
                        matrixStack.mulPose(Axis.YP.rotationDegrees(BASIC_ROTATION * ROTATE_WEST));
                        break;
                }

                if (tileEntity.getTextLines().isEmpty())
                {
                    renderText(matrixStack, buffer, combinedLight, "Unknown Citizen", 0);
                }
                else
                {
                    for (int i = 0; i < tileEntity.getTextLines().size(); i++)
                    {
                        renderText(matrixStack, buffer, combinedLight, tileEntity.getTextLines().get(i), i);
                    }
                }
            }
        }

        // restore the original transformation matrix + normals matrix
        matrixStack.popPose();
    }

    private void renderText(final PoseStack matrixStack, final MultiBufferSource buffer, final int combinedLight, String text, final int line)
    {
        final int maxSize = 20;
        if (text.length() > maxSize)
        {
            text = text.substring(0, maxSize);
        }

        final FormattedCharSequence iReorderingProcessor = FormattedCharSequence.forward(text, Style.EMPTY);
        if (iReorderingProcessor != null)
        {
            final Font fontRenderer = Minecraft.getInstance().font;

            float x = (float) (-fontRenderer.width(iReorderingProcessor) / 2); //render width of text divided by 2
            fontRenderer.drawInBatch(iReorderingProcessor, x, line * 10f,
              0xdcdcdc00, false, matrixStack.last().pose(), buffer, Font.DisplayMode.NORMAL, 0, combinedLight);
        }
    }

    // this should be true for tileentities which render globally (no render bounding box), such as beacons.
    @Override
    public boolean shouldRenderOffScreen(TileEntityNamedGrave tileEntityMBE21)
    {
        return false;
    }
}




