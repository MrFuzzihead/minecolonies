package net.minecraft.world.flag;
/** [1.7.10] Stub for 1.21 FeatureFlags */
public class FeatureFlags {
    public static final FeatureFlagRegistry REGISTRY = new FeatureFlagRegistry();
    public static final FeatureFlagSet VANILLA_SET = new FeatureFlagSet();
    public static class FeatureFlagRegistry {
        public FeatureFlagSet allFlags() { return new FeatureFlagSet(); }
    }
}