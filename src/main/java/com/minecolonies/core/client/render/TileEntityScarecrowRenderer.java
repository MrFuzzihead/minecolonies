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

import com.minecolonies.api.blocks.huts.AbstractBlockMinecoloniesDefault;
import com.minecolonies.api.tileentities.AbstractTileEntityScarecrow;
import com.minecolonies.api.tileentities.ScareCrowType;
import com.minecolonies.api.util.constant.Constants;
import com.minecolonies.core.blocks.BlockScarecrow;
import com.minecolonies.core.client.model.ScarecrowModel;
import com.minecolonies.core.event.ClientRegistryHandler;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
// [1.7.10] client removed (use @SideOnly)
// [1.7.10] client removed (use @SideOnly)
// [1.7.10] client removed (use @SideOnly)
// [1.7.10] client removed (use @SideOnly)
// [1.7.10] client removed (use @SideOnly)
// [1.7.10] client removed (use @SideOnly)
// [1.7.10] Direction -> net.minecraft.util.EnumFacing
import net.minecraft.util.ResourceLocation;
import net.minecraft.client.renderer.entity.Material;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;

/**
 * Class to render the scarecrow.
 */
@OnlyIn(Dist.CLIENT)
public class TileEntityScarecrowRenderer implements BlockEntityRenderer<AbstractTileEntityScarecrow>
{
    /**
     * Offset to the block middle.
     */
    private static final double BLOCK_MIDDLE = 0.5;

    /**
     * Y-Offset in order to have the scarecrow over ground.
     */
    private static final double YOFFSET = 1.5;

    /**
     * Rotate the model some degrees.
     */
    private static final int ROTATION = 180;

    /**
     * Basic rotation to achieve a certain direction.
     */
    private static final int BASIC_ROTATION = 90;

    /**
     * Rotate by amount to go east.
     */
    private static final int ROTATE_EAST = 1;

    /**
     * Rotate by amount to go south.
     */
    private static final int ROTATE_SOUTH = 2;

    /**
     * Rotate by amount to go west.
     */
    private static final int ROTATE_WEST = 3;

    /**
     * The model of the scarecrow.
     */
    @NotNull
    private ScarecrowModel model;

    public static final Material SCARECROW_A;
    public static final Material       SCARECROW_B;
    static
    {
        SCARECROW_A = new Material(TextureAtlas.LOCATION_BLOCKS, new ResourceLocation(Constants.MOD_ID, "block/blockscarecrowpumpkin"));
        SCARECROW_B = new Material(TextureAtlas.LOCATION_BLOCKS, new ResourceLocation(Constants.MOD_ID, "block/blockscarecrownormal"));
    }
    /**
     * The public constructor for the renderer.
     *
     * @param context the render context.
     */
    public TileEntityScarecrowRenderer(final BlockEntityRendererProvider.Context context)
    {
        super();
        this.model = new ScarecrowModel(context.bakeLayer(ClientRegistryHandler.SCARECROW));
    }

    @Override
    public void render(
      final AbstractTileEntityScarecrow te,
      final float partialTicks,
      final PoseStack matrixStack,
      @NotNull final MultiBufferSource iRenderTypeBuffer,
      final int lightA,
      final int lightB)
    {
        if (te.getBlockState().getValue(BlockScarecrow.HALF) == DoubleBlockHalf.UPPER)
        {
            return;
        }
        //Store the transformation
        matrixStack.pushPose();
        //Set viewport to tile entity position to render it
        matrixStack.translate(BLOCK_MIDDLE, YOFFSET, BLOCK_MIDDLE);
        matrixStack.mulPose(Axis.ZP.rotationDegrees(ROTATION));

        //In the case of worldLags tileEntities may sometimes disappear.
        if (te.getLevel().getBlockState(te.getBlockPos()).getBlock() instanceof BlockScarecrow)
        {
            final Direction facing = te.getLevel().getBlockState(te.getBlockPos()).getValue(AbstractBlockMinecoloniesDefault.FACING);
            switch (facing)
            {
                case EAST:
                    matrixStack.mulPose(Axis.YP.rotationDegrees(BASIC_ROTATION * ROTATE_EAST));
                    break;
                case SOUTH:
                    matrixStack.mulPose(Axis.YP.rotationDegrees(BASIC_ROTATION * ROTATE_SOUTH));
                    break;
                case WEST:
                    matrixStack.mulPose(Axis.YP.rotationDegrees(BASIC_ROTATION * ROTATE_WEST));
                    break;
                default:
                    //don't rotate at all.
            }
        }

        final VertexConsumer vertexConsumer = getMaterial(te).buffer(iRenderTypeBuffer, RenderType::entitySolid);
        this.model.renderToBuffer(matrixStack, vertexConsumer, lightA, lightB, 1.0F, 1.0F, 1.0F, 1.0F);
        matrixStack.popPose();
    }

    /**
     * Returns the Material of the scarecrow texture.
     *
     * @param tileEntity the tileEntity of the scarecrow.
     * @return the material.
     */
    @NotNull
    private static Material getMaterial(@NotNull final AbstractTileEntityScarecrow tileEntity)
    {
        if (tileEntity.getScarecrowType() == ScareCrowType.PUMPKINHEAD)
        {
            return SCARECROW_A;
        }
        else
        {
            return SCARECROW_B;
        }
    }
}



