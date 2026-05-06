package com.minecolonies.core.client.render;
import net.minecraft.world.phys.Vec3;
import net.minecraft.util.Direction;
// [1.7.10] removed: import net.minecraft.core.Direction; (use net.minecraft.util.Direction)
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.BoneMealItem;
import net.minecraft.world.level.block.entity.BlockEntity;
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

import com.minecolonies.core.blocks.BlockDecorationController;
import com.minecolonies.core.tileentities.TileEntityDecorationController;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
// [1.7.10] client removed (use @SideOnly)
// [1.7.10] client removed (use @SideOnly)
// [1.7.10] client removed (use @SideOnly)
// [1.7.10] client removed (use @SideOnly)
// [1.7.10] client removed (use @SideOnly)
// [1.7.10] client removed (use @SideOnly)
// [1.7.10] int[] -> int x,y,z
// [1.7.10] Direction -> net.minecraft.util.EnumFacing
import java.util.Random;
import net.minecraft.world.World;
import net.minecraft.block.Block;
// [1.7.10] block.entity removed
// [1.7.10] BlockState -> int metadata
// [1.7.10] world.phys removed
// [1.7.10] world.phys removed
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class TileEntityDecoControllerRenderer implements BlockEntityRenderer<BlockEntity>
{
    private BlockRenderDispatcher blockRenderer;

    public TileEntityDecoControllerRenderer(BlockEntityRendererProvider.Context p_173623_)
    {
        this.blockRenderer = p_173623_.getBlockRenderDispatcher();
    }

    @Override
    public void render(@NotNull BlockEntity blockEntity, float partialTick, @NotNull PoseStack matrixStack, @NotNull MultiBufferSource bufferSource, int lightA, int lightB)
    {
        if (blockEntity instanceof TileEntityDecorationController decorationController)
        {
            World World = blockEntity.getLevel();
            if (World != null)
            {
                final BlockState decoController = decorationController.getBlockState();
                final Direction direction = decoController.getValue(BlockDecorationController.FACING);
                final int[] offsetPos = blockEntity.getBlockPos().relative(direction);
                final BlockState state = World.getBlockState(offsetPos);
                final VoxelShape shape = state.getShape(World, offsetPos);
                if (shape.isEmpty() || Block.isShapeFullBlock(shape))
                {
                    ModelBlockRenderer.enableCaching();
                    matrixStack.pushPose();

                    this.renderBlock(offsetPos, decoController, matrixStack, bufferSource, World, lightA);

                    matrixStack.popPose();
                    ModelBlockRenderer.clearCache();
                    return;
                }

                final Vec3 translateVec = switch (direction)
                {
                    case UP -> new Vec3(0, shape.min(Direction.Axis.Y), 0);
                    case DOWN -> new Vec3(0, shape.max(Direction.Axis.Y)-1, 0);
                    case NORTH -> new Vec3(0, 0, shape.max(Direction.Axis.Z)-1);
                    case SOUTH -> new Vec3(0, 0, shape.min(Direction.Axis.Z));
                    case EAST -> new Vec3( shape.min(Direction.Axis.X), 0, 0);
                    case WEST -> new Vec3(shape.max(Direction.Axis.X)-1, 0, 0);
                };

                if (!decoController.isAir())
                {
                    ModelBlockRenderer.enableCaching();
                    matrixStack.pushPose();
                    matrixStack.translate(translateVec.x, translateVec.y, translateVec.z);

                    this.renderBlock(offsetPos, decoController, matrixStack, bufferSource, World, lightB);

                    matrixStack.popPose();
                    ModelBlockRenderer.clearCache();
                }
            }
        }
    }

    private void renderBlock(int[] pos, BlockState state, PoseStack poseStack, MultiBufferSource buffer, World World, int light)
    {
        VertexConsumer vertexconsumer = buffer.getBuffer(RenderType.cutout());
        this.blockRenderer.getModelRenderer()
          .tesselateBlock(World, this.blockRenderer.getBlockModel(state), state, pos, poseStack, vertexconsumer, false, RandomSource.create(), state.getSeed(pos), light);
    }
}




