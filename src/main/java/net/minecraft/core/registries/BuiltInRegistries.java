package net.minecraft.core.registries;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.entity.BannerPattern;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * [1.7.10] Compatibility stub for 1.21 BuiltInRegistries.
 */
public class BuiltInRegistries
{
    public static final BannerPatternRegistry BANNER_PATTERN = new BannerPatternRegistry();

    public static class BannerPatternRegistry
    {
        public Stream<Holder<BannerPattern>> holders() { return Stream.empty(); }
        public Optional<Holder<BannerPattern>> getHolder(ResourceKey<BannerPattern> key) { return Optional.empty(); }
        public Holder<BannerPattern> getHolderOrThrow(ResourceKey<BannerPattern> key) { return new Holder<>(null); }
    }
}

