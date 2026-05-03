package com.minecolonies.api.advancements;

// [1.7.10 BACKPORT] Advancements / CriterionTrigger do not exist in Minecraft 1.7.10.
// This entire class is stubbed out so that the rest of the codebase compiles.
// All trigger logic is commented out below.
//
// Original 1.21 imports (removed):
//   com.google.common.collect.Maps
//   net.minecraft.advancements.CriterionTrigger
//   net.minecraft.advancements.CriterionTriggerInstance
//   net.minecraft.resources.ResourceLocation
//   net.minecraft.server.PlayerAdvancements

// TODO: [1.7.10 BACKPORT] If achievement-tracking (analogous to advancements) is desired,
// consider integrating with the vanilla 1.7.10 Achievement system or implementing
// a lightweight custom trigger/reward system.

import net.minecraft.util.ResourceLocation;

import java.util.Map;
import java.util.function.Function;

/**
 * Stub for the advancement criterion trigger base class.
 * All bodies are commented out — no advancement logic runs in 1.7.10.
 *
 * @param <T> stub type parameter (was CriterionListeners subclass)
 * @param <U> stub type parameter (was CriterionTriggerInstance subclass)
 */
public abstract class AbstractCriterionTrigger<T extends CriterionListeners<U>, U>
{
    private final ResourceLocation id;

    protected AbstractCriterionTrigger(final ResourceLocation id, final Function<Object, T> createNew)
    {
        this.id = id;
        // [1.7.10 BACKPORT] listener map and factory not used; no advancement system.
    }

    public ResourceLocation getId()
    {
        return id;
    }

    // [1.7.10 BACKPORT] All listener management methods below are no-ops.
    // In 1.21 these drove the Advancement criterion system via PlayerAdvancements.

    // public void addPlayerListener(PlayerAdvancements playerAdvancements, Listener<U> listener) { ... }
    // public void removePlayerListener(PlayerAdvancements playerAdvancements, Listener<U> listener) { ... }
    // protected T getListeners(PlayerAdvancements playerAdvancements) { return null; }
    // public void removePlayerListeners(PlayerAdvancements playerAdvancements) { ... }
}
