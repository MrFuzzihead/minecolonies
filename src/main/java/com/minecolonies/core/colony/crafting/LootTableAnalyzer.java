package com.minecolonies.core.colony.crafting;

// [1.7.10 TODO] LootTableAnalyzer: LootDataManager, LootTable, ForgeRegistries, GsonHelper, PotionUtils etc.
// have no equivalent in 1.7.10 — entire analysis logic is stubbed out.
// JEI loot table display will show no drops.

import com.minecolonies.api.util.Tuple;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Utility helper that analyzes a loot table to determine a likely list of drops.
 * [1.7.10 BACKPORT] All analysis logic is stubbed — loot tables are a 1.21 feature with no direct
 * equivalent in 1.7.10.
 */
public final class LootTableAnalyzer
{
    private LootTableAnalyzer() { }

    /** Stub: returns empty list — no loot table analysis in 1.7.10. */
    @NotNull
    public static List<LootDrop> toDrops(@Nullable final Object lootTableManager,
                                         @NotNull final ResourceLocation lootTableId)
    {
        return Collections.emptyList();
    }

    /** Stub: returns empty list — no loot table analysis in 1.7.10. */
    @NotNull
    public static List<LootDrop> toDrops(@Nullable final Object lootTableManager,
                                         @Nullable final Object lootTable)
    {
        return Collections.emptyList();
    }

    /** Stub: returns empty list — no loot table analysis in 1.7.10. */
    @NotNull
    public static List<LootDrop> consolidate(@NotNull final List<LootDrop> input)
    {
        return Collections.emptyList();
    }

    // -----------------------------------------------------------------------
    // LootDrop — kept for network serialization compatibility
    // -----------------------------------------------------------------------

    /**
     * Represents a single possible drop from a loot table.
     */
    public static class LootDrop
    {
        private final List<ItemStack> stacks;
        private final float probability;
        private final float quality;
        private final boolean conditional;

        public LootDrop(@NotNull final List<ItemStack> stacks, final float probability, final float quality, final boolean conditional)
        {
            this.stacks = stacks;
            this.probability = probability;
            this.quality = quality;
            this.conditional = conditional;
        }

        public LootDrop(@NotNull final List<LootDrop> drops)
        {
            this.stacks = drops.stream().flatMap(d -> d.getItemStacks().stream()).collect(Collectors.toList());
            this.probability = drops.get(0).getProbability();
            this.quality = drops.get(0).getQuality();
            this.conditional = drops.get(0).getConditional();
        }

        @NotNull public List<ItemStack> getItemStacks() { return this.stacks; }
        public float getProbability() { return this.probability; }
        public float getQuality() { return this.quality; }
        public boolean getConditional() { return this.conditional; }

        @Override
        public int hashCode()
        {
            return Objects.hash(probability, quality, conditional);
        }

        /** Copy a LootDrop to a packet buffer */
        public void serialize(@NotNull final PacketBuffer buffer)
        {
            buffer.writeVarInt(stacks.size());
            for (final ItemStack stack : stacks)
            {
                buffer.writeItemStackToBuffer(stack);
            }
            buffer.writeFloat(probability);
            buffer.writeFloat(quality);
            buffer.writeBoolean(conditional);
        }

        /** Recover a LootDrop from a packet buffer */
        public static LootDrop deserialize(@NotNull final PacketBuffer buffer)
        {
            final int size = buffer.readVarInt();
            final List<ItemStack> stacks = new ArrayList<>(size);
            for (int i = 0; i < size; ++i)
            {
                stacks.add(buffer.readItemStackFromBuffer());
            }
            final float probability = buffer.readFloat();
            final float quality = buffer.readFloat();
            final boolean conditional = buffer.readBoolean();
            return new LootDrop(stacks, probability, quality, conditional);
        }
    }
}
