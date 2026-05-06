package net.minecraft.advancements.critereon;

import net.minecraft.world.level.ItemLike;

/** [1.7.10 bridge] ItemPredicate */
public class ItemPredicate
{
    public static class Builder
    {
        public static Builder item() { return new Builder(); }
        public Builder of(final ItemLike item) { return this; }
        public ItemPredicate build() { return new ItemPredicate(); }
    }
}

