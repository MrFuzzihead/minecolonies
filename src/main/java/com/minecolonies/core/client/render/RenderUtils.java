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

// [1.7.10] client removed (use @SideOnly)
// [1.7.10] int /* InteractionHand */ removed
// [1.7.10] world.entity removed
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.EntityCreature;
import net.minecraft.world.item.UseAnim;

public class RenderUtils
{
    /**
     * Arm pose helper, take from PlayerRenderer#getArmPose
     *
     * @param entity
     * @param hand
     * @return
     */
    public static HumanoidModel.ArmPose getArmPose(EntityCreature entity, int /* InteractionHand */ hand)
    {
        if (entity.isLeftHanded())
        {
            hand = hand == 0 /* InteractionHand.MAIN_HAND */ ? 1 /* InteractionHand.OFF_HAND */ : 0 /* InteractionHand.MAIN_HAND */;
        }

        ItemStack itemstack = entity.getItemInHand(hand);
        if (itemstack.isEmpty())
        {
            return HumanoidModel.ArmPose.EMPTY;
        }
        else
        {
            if (entity.getUsedItemHand() == hand && entity.getUseItemRemainingTicks() > 0)
            {
                UseAnim useanim = itemstack.getUseAnimation();
                if (useanim == UseAnim.BLOCK)
                {
                    return HumanoidModel.ArmPose.BLOCK;
                }

                if (useanim == UseAnim.BOW)
                {
                    return HumanoidModel.ArmPose.BOW_AND_ARROW;
                }

                if (useanim == UseAnim.SPEAR)
                {
                    return HumanoidModel.ArmPose.THROW_SPEAR;
                }

                if (useanim == UseAnim.CROSSBOW && hand == entity.getUsedItemHand())
                {
                    return HumanoidModel.ArmPose.CROSSBOW_CHARGE;
                }

                if (useanim == UseAnim.SPYGLASS)
                {
                    return HumanoidModel.ArmPose.SPYGLASS;
                }

                if (useanim == UseAnim.TOOT_HORN)
                {
                    return HumanoidModel.ArmPose.TOOT_HORN;
                }

                if (useanim == UseAnim.BRUSH)
                {
                    return HumanoidModel.ArmPose.BRUSH;
                }
            }
            else if (!entity.swinging && itemstack.getItem() instanceof CrossbowItem && CrossbowItem.isCharged(itemstack))
            {
                return HumanoidModel.ArmPose.CROSSBOW_HOLD;
            }

            HumanoidModel.ArmPose forgeArmPose = net.minecraftforge.client.extensions.common.IClientItemExtensions.of(itemstack).getArmPose(entity, hand, itemstack);
            if (forgeArmPose != null)
            {
                return forgeArmPose;
            }

            return HumanoidModel.ArmPose.ITEM;
        }
    }
}





