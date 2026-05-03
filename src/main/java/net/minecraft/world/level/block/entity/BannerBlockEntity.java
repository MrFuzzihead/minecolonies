package net.minecraft.world.level.block.entity;

import net.minecraft.world.item.DyeColor;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.Holder;
import net.minecraft.nbt.NBTTagList;

import java.util.ArrayList;
import java.util.List;

/**
 * [1.7.10] Compatibility stub for 1.21 BannerBlockEntity.
 */
public class BannerBlockEntity
{
    public static List<Pair<Holder<BannerPattern>, DyeColor>> createPatterns(DyeColor baseColor, NBTTagList layers)
    {
        return new ArrayList<>();
    }

    public static class Builder
    {
        public Builder addPattern(Holder<BannerPattern> pattern, DyeColor color) { return this; }
        public NBTTagList toListTag() { return new NBTTagList(); }
    }
}

