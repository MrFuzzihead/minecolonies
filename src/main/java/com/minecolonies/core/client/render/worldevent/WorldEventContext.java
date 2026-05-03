package com.minecolonies.core.client.render.worldevent;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.BoneMealItem;
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

import com.ldtteam.structurize.client.rendertask.util.WorldRenderMacros;
import com.minecolonies.api.colony.IColonyManager;
import com.minecolonies.api.colony.IColonyView;
import com.minecolonies.core.client.render.TileEntityColonySignRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
// [1.7.10] client removed (use @SideOnly)
// [1.7.10] client removed (use @SideOnly)
// [1.7.10] client removed (use @SideOnly)
// [1.7.10] client removed (use @SideOnly)
import net.minecraft.item.ItemStack;
import net.minecraft.client.renderer.MultiBufferSource;
import static net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.World;
// [1.7.10] world.phys removed
import net.minecraftforge.client.event.RenderLevelStageEvent;
import org.jetbrains.annotations.Nullable;

/**
 * Main class for handling world rendering.
 * Also holds all possible values which may be needed during rendering.
 */
public class WorldEventContext
{
    public static final WorldEventContext INSTANCE = new WorldEventContext();

    private WorldEventContext()
    {
        // singleton
    }

    public RenderLevelStageEvent stageEvent;
    public MultiBufferSource.BufferSource bufferSource;
    public PoseStack poseStack;
    public float partialTicks;
    public ClientLevel clientLevel;
    public LocalPlayer clientPlayer;
    public ItemStack mainHandItem;
    @Nullable
    public IColonyView nearestColony;

    /**
     * In chunks
     */
    int clientRenderDist;

    public void renderWorldLastEvent(final RenderLevelStageEvent event)
    {
        stageEvent = event;
        bufferSource = WorldRenderMacros.getBufferSource();
        poseStack = event.getPoseStack();
        partialTicks = event.getPartialTick();
        clientLevel = Minecraft.getInstance().World;
        clientPlayer = Minecraft.getInstance().player;
        mainHandItem = clientPlayer.getMainHandItem();
        clientRenderDist = Minecraft.getInstance().options.renderDistance().get();

        final Vec3 cameraPos = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
        poseStack.pushPose();
        poseStack.translate(-cameraPos.x(), -cameraPos.y(), -cameraPos.z());

        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_CUTOUT_MIPPED_BLOCKS_BLOCKS)
        {
            ColonyBorderRenderer.render(this); // renders directly (not into bufferSource)
            ColonyBlueprintRenderer.renderBlueprints(this);
            ColonyWaypointRenderer.render(this);
            ColonyPatrolPointRenderer.render(this);
            GuardTowerRallyBannerRenderer.render(this);
            PathfindingDebugRenderer.render(this);

            bufferSource.endBatch();
        }
        else if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_TRIPWIRE_BLOCKS)
        {
            ColonyBlueprintRenderer.renderBoxes(this);
            ItemOverlayBoxesRenderer.render(this);
            HighlightManager.render(this);
            TileEntityColonySignRenderer.renderSignHover(this);

            bufferSource.endBatch();
        }

        poseStack.popPose();
    }

    boolean hasNearestColony()
    {
        return nearestColony != null;
    }

    /**
     * Checks for a nearby colony
     *
     * @param World
     */
    public void checkNearbyColony(final World World)
    {
        if (clientPlayer != null)
        {
            nearestColony = IColonyManager.getInstance().getClosestColonyView(World, clientPlayer.blockPosition());
        }
    }
}




