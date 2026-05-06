package com.minecolonies.api.util;
import net.minecraft.core.Holder;

import com.minecolonies.api.crafting.IGenericRecipe;
import com.minecolonies.api.crafting.IRecipeStorage;
import com.minecolonies.api.crafting.ItemStorage;
import com.minecolonies.api.items.ModTags;
// [1.7.10] Holder removed
// [1.7.10] HolderLookup removed
// [1.7.10] Registries removed
// [1.7.10] int /* ResourceKey */ -> int dimensionId
// [1.7.10] tags removed
// [1.7.10] net.minecraft.world.item.* removed - using 1.7.10 ItemStack
import net.minecraft.item.ItemStack;
// [1.7.10] ForgeHooksClient removed (client-only)
// [1.7.10] MutableHashedLinkedMap removed
// [1.7.10] ModLoader removed
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

/**
 * Utility class that handles crafting duties
 */
public final class CraftingUtils
{
    private CraftingUtils()
    {
        throw new IllegalStateException("Tried to initialize: CraftingUtils but this is a Utility class.");
    }

    /**
     * Calculate the max time a recipe has to be executed.
     */
    public static int calculateMaxCraftingCount(@NotNull final ItemStack outputStack, @NotNull final IRecipeStorage storage)
    {
        int craftingCount = (int) Math.ceil(
          Math.max(ItemStackUtils.getSize(outputStack), ItemStackUtils.getSize(storage.getPrimaryOutput())) / (double) ItemStackUtils.getSize(storage.getPrimaryOutput()));

        for (final ItemStorage ingredientStorage : storage.getCleanedInput())
        {
            final ItemStack ingredient = ingredientStorage.getItemStack();
            final int ingredientInputCount = ItemStackUtils.getSize(ingredient) * craftingCount;
            if (ingredientInputCount > ingredient.getMaxStackSize())
            {
                craftingCount = Math.max(ingredient.getMaxStackSize(), ItemStackUtils.getSize(storage.getPrimaryOutput())) / ItemStackUtils.getSize(storage.getPrimaryOutput());
            }
        }

        return craftingCount;
    }

    /**
     * Calculate the max time a recipe has to be executed.
     */
    public static int calculateMaxCraftingCount(final int count, @NotNull final IRecipeStorage storage)
    {
        return (int) Math.ceil(Math.max(count, ItemStackUtils.getSize(storage.getPrimaryOutput())) / (double) ItemStackUtils.getSize(storage.getPrimaryOutput()));
    }

    /**
     * Generates an {@link OptionalPredicate} for product tag validation.
     * [1.7.10] Tags not available; always returns Optional.empty().
     */
    public static OptionalPredicate<ItemStack> getProductValidatorBasedOnTags(@NotNull final String crafterJobName)
    {
        // [1.7.10] Tags not available - no crafter product exclusions/inclusions
        return stack -> Optional.empty();
    }

    /**
     * Generates an {@link OptionalPredicate} for ingredient tag validation.
     * [1.7.10] Tags not available; always returns Optional.empty().
     */
    public static OptionalPredicate<ItemStack> getIngredientValidatorBasedOnTags(@NotNull final String crafterJobName)
    {
        return getIngredientValidatorBasedOnTags(crafterJobName, false);
    }

    /**
     * Generates an {@link OptionalPredicate} for ingredient tag validation.
     * [1.7.10] Tags not available; always returns Optional.empty().
     */
    public static OptionalPredicate<ItemStack> getIngredientValidatorBasedOnTags(@NotNull final String crafterJobName, final boolean includeDoRules)
    {
        // [1.7.10] Tags not available
        return stack -> Optional.empty();
    }

    /**
     * Checks the tags associated with a job against a recipe.
     * [1.7.10] Tags not available; always returns Optional.empty().
     */
    public static Optional<Boolean> isRecipeCompatibleBasedOnTags(@NotNull final IGenericRecipe recipe, @NotNull final String crafterJobName)
    {
        return OptionalPredicate.combine(recipe.matchesOutput(getProductValidatorBasedOnTags(crafterJobName)),
                () -> recipe.matchesInput(getIngredientValidatorBasedOnTags(crafterJobName)));
    }

    /**
     * [1.7.10] Creative tabs do not exist in the same form; this method is a no-op stub.
     * TODO: Port creative tab item iteration if needed.
     */
    public static void forEachCreativeTabItems(@NotNull final Object displayParams,
                                               @NotNull final BiConsumer<Object, Collection<ItemStack>> consumer)
    {
        // [1.7.10] CreativeModeTab system does not exist; no-op
    }
}
