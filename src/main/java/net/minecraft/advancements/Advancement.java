package net.minecraft.advancements;

import net.minecraft.util.ResourceLocation;
import java.util.function.Consumer;

/** [1.7.10 bridge] Advancement - no 1.7.10 equivalent (advancements added in 1.12) */
public class Advancement
{
    public static class Builder
    {
        public static Builder advancement() { return new Builder(); }
        public Builder parent(final ResourceLocation id) { return this; }
        public Builder parent(final Advancement parent) { return this; }
        public Builder display(final Object... args) { return this; }
        public Builder addCriterion(final String key, final Object criterion) { return this; }
        public Advancement save(final Consumer<Advancement> consumer, final ResourceLocation id, final Object fileHelper) { return new Advancement(); }
        public Advancement save(final Consumer<Advancement> consumer, final String id, final Object fileHelper) { return new Advancement(); }
    }
}

