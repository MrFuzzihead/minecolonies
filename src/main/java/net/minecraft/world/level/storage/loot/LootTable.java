package net.minecraft.world.level.storage.loot;

import net.minecraft.util.ResourceLocation;

/** [1.7.10 bridge] LootTable - no 1.7.10 equivalent (loot tables added in 1.9) */
public class LootTable
{
    public static final LootTable EMPTY = new LootTable();

    public void validate(final ValidationContext context) {}

    public static class Builder
    {
        public Builder withPool(final Object pool) { return this; }
        public Builder apply(final Object function) { return this; }
        public LootTable build() { return new LootTable(); }
    }
}

