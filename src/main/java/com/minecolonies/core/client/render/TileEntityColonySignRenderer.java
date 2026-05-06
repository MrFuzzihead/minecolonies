package com.minecolonies.core.client.render;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.chat.Style;
import net.minecraft.util.Direction;
// [1.7.10] removed: import net.minecraft.core.Direction; (use net.minecraft.util.Direction)
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
import com.minecolonies.core.client.render.worldevent.ColonyWorldRenderMacros;
import com.minecolonies.core.client.render.worldevent.WorldEventContext;
import com.minecolonies.core.tileentities.TileEntityColonySign;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
// [1.7.10] client removed (use @SideOnly)
// [1.7.10] client removed (use @SideOnly)
// [1.7.10] client removed (use @SideOnly)
// [1.7.10] client removed (use @SideOnly)
// [1.7.10] client removed (use @SideOnly)
// [1.7.10] int[] -> int x,y,z
import net.minecraft.util.IChatComponent;
// [1.7.10] chat.String replaced by IChatComponent/ChatComponentText
// [1.7.10] chat.String replaced by IChatComponent/ChatComponentText
import java.util.Random;
// [1.7.10] BlockState -> int metadata
// [1.7.10] client removed (use @SideOnly)
// [1.7.10] client removed (use @SideOnly)
// [1.7.10] client removed (use @SideOnly)

// [1.7.10] chat.String replaced by IChatComponent/ChatComponentText
// [1.7.10] world.phys removed
// [1.7.10] world.phys removed
// [1.7.10] world.phys removed
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraftforge.client.model.data.ModelData;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

import java.util.List;

import static com.minecolonies.api.util.constant.TranslationConstants.NEXT;
import static com.minecolonies.api.util.constant.TranslationConstants.PREVIOUS;
import static com.minecolonies.core.blocks.BlockColonySign.CONNECTED;

@OnlyIn(Dist.CLIENT)
public class TileEntityColonySignRenderer implements BlockEntityRenderer<TileEntityColonySign>
{
    /**
     * The models of the signs.
     */
    private final BakedModel model;
    private final BakedModel model2;

    /**
     * Cached render dispatcher.
     */
    private final BlockRenderDispatcher renderDispatcher;

    public TileEntityColonySignRenderer(final BlockEntityRendererProvider.Context context)
    {
        super();
        model = context.getBlockRenderDispatcher().getBlockModel(ModBlocks.blockColonySign.defaultBlockState());
        model2 = context.getBlockRenderDispatcher().getBlockModel(ModBlocks.blockColonySign.defaultBlockState().setValue(CONNECTED, true));
        renderDispatcher = context.getBlockRenderDispatcher();
    }

    @Override
    public void render(
        @NotNull final TileEntityColonySign tileEntity,
        final float partialTicks,
        final PoseStack matrixStack,
        @NotNull final MultiBufferSource buffer,
        final int combinedLight,
        final int combinedOverlay)
    {
        final float relativeRotationToColony = tileEntity.getRelativeRotation();
        final BlockState state = tileEntity.getBlockState();
        if (state.getBlock() == ModBlocks.blockColonySign)
        {
            matrixStack.pushPose();
            matrixStack.translate(0.5, 0.5, 0.5);
            matrixStack.mulPose(Axis.YP.rotationDegrees(relativeRotationToColony));
            matrixStack.translate(-0.5, -0.5, -0.5);
            renderSingleBlock(state, matrixStack, buffer, combinedLight, combinedOverlay, tileEntity.getTargetColonyId() != tileEntity.getCachedSignAboveColony());
            matrixStack.popPose();

           renderTextOnSide(matrixStack, relativeRotationToColony, tileEntity, buffer, combinedLight, true);
           renderTextOnSide(matrixStack, relativeRotationToColony, tileEntity, buffer, combinedLight, false);
        }
    }

    /**
     * Render the text on the sign and handle rotation etc.
     * @param matrixStack the matrix stack.
     * @param relativeRotationToColony the relative rotation.
     * @param tileEntity the block entity.
     * @param buffer the buffer.
     * @param combinedLight the light.
     * @param mirrored if mirrored or not.
     */
    private void renderTextOnSide(final PoseStack matrixStack, final float relativeRotationToColony, final @NotNull TileEntityColonySign tileEntity, final @NotNull MultiBufferSource buffer, final int combinedLight, final boolean mirrored)
    {
        matrixStack.pushPose();
        matrixStack.translate(0.5f, 0.5F, 0.5f);
        matrixStack.mulPose(Axis.YP.rotationDegrees(relativeRotationToColony));
        if (mirrored)
        {
            matrixStack.mulPose(Axis.YP.rotationDegrees(180));
        }
        matrixStack.translate(-0.0f, -0.1F, 0.2f);

        matrixStack.scale(0.007F, -0.007F, 0.007F);

        final String colonyName = tileEntity.getColonyName();
        final int distance = tileEntity.getColonyDistance();
        if (colonyName.isEmpty())
        {
            renderText(matrixStack, buffer, combinedLight, "Unknown Colony", 0, 0);
            renderText(matrixStack, buffer, combinedLight, String.translatable("com.minecolonies.coremod.dist.blocks",distance).getString(), 3, 0);
        }
        else
        {
            final String targetColonyName = tileEntity.getTargetColonyName();
            if (!targetColonyName.isEmpty() && tileEntity.getTargetColonyId() != tileEntity.getCachedSignAboveColony())
            {
                final int targetColonyDistance = tileEntity.getTargetColonyDistance();
                renderColonyNameOnSign(colonyName, matrixStack, buffer, combinedLight, distance, -10);
                renderColonyNameOnSign(targetColonyName, matrixStack, buffer, combinedLight, targetColonyDistance, -60);
            }
            else
            {
                renderColonyNameOnSign(colonyName, matrixStack, buffer, combinedLight, distance, -35);
            }
        }
        matrixStack.popPose();
    }

    /**
     * Render the name and distance on the sign at offset.
     * @param colonyName the name.
     * @param matrixStack the stack.
     * @param buffer the buffer.
     * @param combinedLight the light.
     * @param distance the distance to the colony.
     * @param offset the offset to render it at.
     */
    private void renderColonyNameOnSign(final String colonyName, final PoseStack matrixStack, final @NotNull MultiBufferSource buffer, final int combinedLight, final int distance, final int offset)
    {
        final int textWidth = Minecraft.getInstance().font.width(colonyName);
        if (textWidth > 90)
        {
            final List<FormattedText> splitName = Minecraft.getInstance().font.getSplitter().splitLines(colonyName, 90, Style.EMPTY);;
            for (int i = 0; i < Math.min(2, splitName.size()); i++)
            {
                renderText(matrixStack, buffer, combinedLight, splitName.get(i).getString(), i, offset);
            }
            renderText(matrixStack, buffer, combinedLight, String.translatable("com.minecolonies.coremod.dist.blocks",distance).getString(), 3, offset);
        }
        else
        {
            renderText(matrixStack, buffer, combinedLight, colonyName, 0, offset);
            renderText(matrixStack, buffer, combinedLight, String.translatable("com.minecolonies.coremod.dist.blocks",distance).getString(), 3, offset);
        }
    }

    /**
     * Utility ro render a single block (the sign model).
     * @param state the state of the sign.
     * @param pose the poststack.
     * @param buffer the buffer.
     * @param combinedLight light combined.
     * @param combinedOverlay overlay.
     * @param connected if two colonies are connected.
     */
    private void renderSingleBlock(final BlockState state, final PoseStack pose, final MultiBufferSource buffer, final int combinedLight, final int combinedOverlay, final boolean connected)
    {
        final BakedModel usedModel = connected ? model2 : model;
        for (net.minecraft.client.renderer.RenderType rt : usedModel.getRenderTypes(state, RandomSource.create(42), ModelData.EMPTY))
        {
            this.renderDispatcher.getModelRenderer().renderModel(pose.last(),
                buffer.getBuffer(net.minecraftforge.client.RenderTypeHelper.getEntityRenderType(rt, false)),
                state,
                usedModel,
                0,
                0,
                0,
                combinedLight,
                combinedOverlay,
                ModelData.EMPTY,
                rt);
        }
    }

    /**
     * Text render utility.
     * @param matrixStack the matrix stack.
     * @param buffer the buffer.
     * @param combinedLight the light.
     * @param text the text to render.
     * @param line the line of the text.
     * @param offset additional offset.
     */
    private void renderText(final PoseStack matrixStack, final MultiBufferSource buffer, final int combinedLight, String text, final int line, final float offset)
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
            fontRenderer.drawInBatch(iReorderingProcessor, x, line * 8f + offset,
                0xdcdcdc00, false, matrixStack.last().pose(), buffer, Font.DisplayMode.NORMAL, 0, combinedLight);
        }
    }

    @Override
    public boolean shouldRenderOffScreen(TileEntityColonySign tileEntityMBE21)
    {
        return false;
    }

    public static void renderSignHover(final WorldEventContext context)
    {
        final HitResult rayTraceResult = Minecraft.getInstance().hitResult;
        if (!(rayTraceResult instanceof final BlockHitResult blockRayTraceResult) || blockRayTraceResult.getType() == HitResult.Type.MISS)
            return;

        final int[] posAtCamera = blockRayTraceResult.getBlockPos();
        if (context.clientLevel.getBlockState(posAtCamera).getBlock() != ModBlocks.blockColonySign)
        {
            return;
        }

        if (context.clientLevel.getBlockEntity(posAtCamera) instanceof TileEntityColonySign tileEntityColonySign)
        {
            if (!new int[]{0,0,0}.equals(tileEntityColonySign.getPreviousPos()))
            {
                renderTextBoxAtPos(context, tileEntityColonySign.getPreviousPos(), List.of(String.translatable(PREVIOUS).getString()));
            }
            if (!new int[]{0,0,0}.equals(tileEntityColonySign.getNextPosition()))
            {
                renderTextBoxAtPos(context, tileEntityColonySign.getNextPosition(), List.of(String.translatable(NEXT).getString()));
            }
        }
    }

    private static void renderTextBoxAtPos(final WorldEventContext context, final int[] pos, final List<String> text)
    {
        final MultiBufferSource.BufferSource buffer = Minecraft.getInstance().renderBuffers().bufferSource();
        ColonyWorldRenderMacros.renderLineBox(context.poseStack, buffer, new AABB(pos), 0.05f, 0xffffffff, true);
        renderDebugText(pos, text, context.poseStack, true, 3, 2.5f, buffer);
        ColonyWorldRenderMacros.endRenderLineBox(buffer);
        buffer.endBatch();
    }

    public static void renderDebugText(final int[] renderPos,
        final List<String> text,
        final PoseStack matrixStack,
        final boolean forceWhite,
        final int mergeEveryXListElements,
        final float scale,
        final MultiBufferSource buffer)
    {
        if (mergeEveryXListElements < 1)
        {
            throw new IllegalArgumentException("mergeEveryXListElements is less than 1");
        }

        final EntityRenderDispatcher erm = Minecraft.getInstance().getEntityRenderDispatcher();
        final int cap = text.size();
        if (cap > 0)
        {
            final Font fontrenderer = Minecraft.getInstance().font;

            matrixStack.pushPose();
            matrixStack.translate(renderPos.getX() + 0.5d, renderPos.getY() + 0.6d, renderPos.getZ() + 0.5d);
            matrixStack.mulPose(erm.cameraOrientation());
            matrixStack.scale(-0.014f, -0.014f, 0.014f);

            final float backgroundTextOpacity = Minecraft.getInstance().options.getBackgroundOpacity(0.25F);
            final int alphaMask = (int) (backgroundTextOpacity * 255.0F) << 24;

            final Matrix4f rawPosMatrix = matrixStack.last().pose();
            rawPosMatrix.scale(scale, scale, scale);

            for (int i = 0; i < cap; i += mergeEveryXListElements)
            {
                final String renderText = String.literal(
                    mergeEveryXListElements == 1 ? text.get(i) : text.subList(i, Math.min(i + mergeEveryXListElements, cap)).toString());
                final float textCenterShift = (float) (-fontrenderer.width(renderText) / 2);

                fontrenderer.drawInBatch(renderText,
                    textCenterShift,
                    0,
                    forceWhite ? 0xffffffff : 0x20ffffff,
                    false,
                    rawPosMatrix,
                    buffer,
                    Font.DisplayMode.SEE_THROUGH,
                    alphaMask,
                    0x00f000f0);
                if (!forceWhite)
                {
                    fontrenderer.drawInBatch(renderText, textCenterShift, 0, 0xffffffff, false, rawPosMatrix, buffer, Font.DisplayMode.NORMAL, 0, 0x00f000f0);
                }
                matrixStack.translate(0.0d, fontrenderer.lineHeight + 1, 0.0d);
            }

            matrixStack.popPose();
        }
    }
}





