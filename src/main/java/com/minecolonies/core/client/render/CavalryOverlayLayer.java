package com.minecolonies.core.client.render;
import net.minecraft.world.entity.animal.Animal;
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

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
// [1.7.10] client removed (use @SideOnly)
// [1.7.10] client removed (use @SideOnly)
// [1.7.10] client removed (use @SideOnly)
// [1.7.10] client removed (use @SideOnly)
// [1.7.10] client removed (use @SideOnly)
import net.minecraft.util.ResourceLocation;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayerParent;
import net.minecraft.client.model.HorseModel;
import net.minecraft.world.entity.animal.horse.Horse;
import net.minecraft.client.renderer.MultiBufferSource;
// [1.7.10] world.entity removed

import javax.annotation.Nonnull;
import com.minecolonies.api.util.constant.Constants;
import com.minecolonies.core.entity.other.cavalry.CavalryHorseEntity;

public class CavalryOverlayLayer extends RenderLayer<Horse, HorseModel<Horse>> 
{

    public CavalryOverlayLayer(RenderLayerParent<Horse, HorseModel<Horse>> parent) 
    {
        super(parent);
    }

    /**
     * Renders the cavalry horse overlay layer, which decorates the horse
     * and indicates the horse's readiness for combat.
     *
     * @param pose    the pose stack
     * @param buffer  the multi buffer source
     * @param packedLight  the packed light
     * @param horse  the horse entity
     * @param limbSwing  the limb swing
     * @param limbSwingAmount  the limb swing amount
     * @param partialTicks  the partial ticks
     * @param ageInTicks  the age in ticks
     * @param netHeadYaw  the net head yaw
     * @param headPitch  the head pitch
     */
    @Override
    public void render(@Nonnull PoseStack pose, @Nonnull MultiBufferSource buffer, int packedLight,
                       @Nonnull Horse horse, float limbSwing, float limbSwingAmount,
                       float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) 
    {
        if (!(horse instanceof CavalryHorseEntity cavhorse)) return;

        // Compute readiness from cooldown
        float threshold = horse.getMaxHealth() * CavalryHorseEntity.COMBAT_READINESS_THRESHOLD;
        float cooldown  = Math.max(0f, cavhorse.getAnimalDataView() == null ? 0 : cavhorse.getAnimalDataView().getCombatCooldown());
        float readiness = net.minecraft.util.Mth.clamp(1.0f - (cooldown / Math.max(0.001f, threshold)), 0f, 1f);

        int segments = net.minecraft.util.Mth.clamp((int)Math.floor(readiness * 5f + 0.0001f), 0, 5);

        ResourceLocation OVERLAY_TEX = new ResourceLocation(Constants.MOD_ID, "textures/entity/horse/cavalry_overlay_layer" + segments + ".png");

        VertexConsumer vc = buffer.getBuffer(net.minecraft.client.renderer.RenderType.entityTranslucent(OVERLAY_TEX));
        this.getParentModel().renderToBuffer(pose, vc, packedLight, OverlayTexture.NO_OVERLAY, 1.0f, 1.0f, 1.0f, .85f);
    }
}



