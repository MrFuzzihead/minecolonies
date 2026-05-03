package net.minecraft.world.level.block.entity;

import net.minecraft.resources.ResourceKey;

/**
 * [1.7.10] Compatibility stub for 1.21 BannerPattern.
 */
public class BannerPattern
{
    private final String hashname;

    public BannerPattern(String hashname)
    {
        this.hashname = hashname;
    }

    public String getHashname() { return hashname; }

    public static class Builder
    {
        public Builder addPattern(net.minecraft.core.Holder<BannerPattern> pattern, net.minecraft.world.item.DyeColor color) { return this; }
        public net.minecraft.nbt.NBTTagList toListTag() { return new net.minecraft.nbt.NBTTagList(); }
    }
}

