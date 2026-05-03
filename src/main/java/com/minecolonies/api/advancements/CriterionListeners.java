package com.minecolonies.api.advancements;

// [1.7.10 BACKPORT] Advancements / CriterionTrigger do not exist in Minecraft 1.7.10.
// This class is stubbed out — all bodies are no-ops.
//
// Original 1.21 imports (removed):
//   com.google.common.collect.Sets
//   net.minecraft.advancements.CriterionTrigger
//   net.minecraft.advancements.CriterionTriggerInstance
//   net.minecraft.server.PlayerAdvancements

import java.util.function.Predicate;

/**
 * Stub for the advancement criterion listener manager.
 * No advancement logic runs in 1.7.10.
 *
 * @param <T> stub type parameter (was CriterionTriggerInstance subclass)
 */
public class CriterionListeners<T>
{
    public CriterionListeners(final Object playerAdvancements)
    {
        // [1.7.10 BACKPORT] no-op
    }

    public boolean isEmpty()             { return true; }
    public void add(final Object l)      { /* no-op */ }
    public void remove(final Object l)   { /* no-op */ }
    public void trigger()                { /* no-op */ }
    public void trigger(final Predicate<T> test) { /* no-op */ }
}
