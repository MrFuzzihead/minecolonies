package net.minecraft.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.Holder;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.entity.BannerPattern;
import com.mojang.datafixers.util.Pair;
import net.minecraft.util.ResourceLocation;

import java.util.List;

/**
 * [1.7.10] Compatibility stub for 1.21 BannerRenderer.
 */
public class BannerRenderer
{
    public static void renderPatterns(
        PoseStack poseStack,
        MultiBufferSource.BufferSource source,
        int combinedLight,
        int combinedOverlay,
        ModelPart modelPart,
        ResourceLocation textureLocation,
        boolean isGlint,
        List<Pair<Holder<BannerPattern>, DyeColor>> patterns
    ) {}
}

