package com.minecolonies.api.crafting;

import com.google.common.collect.ImmutableList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * The mult-output recipe type
 */
public class MultiOutputRecipe extends AbstractRecipeType<IRecipeStorage>
{
    /**
     * Cache of item stacks for display
     */
    private final ArrayList<ItemStack> outputDisplayStacks = new ArrayList<>();

    /**
     * Multi-Output recipe type
     */
    public MultiOutputRecipe(IRecipeStorage recipe)
    {
        super(recipe);
    }

    @Override
    public List<ItemStack> getOutputDisplayStacks()
    {
        if(outputDisplayStacks.isEmpty())
        {
            if (!com.minecolonies.api.util.ItemStackUtils.isEmpty(recipe.getPrimaryOutput()))
            {
                outputDisplayStacks.add(recipe.getPrimaryOutput());
            }
            outputDisplayStacks.addAll(recipe.getAlternateOutputs());
        }
        return ImmutableList.copyOf(outputDisplayStacks);
    }

    @Override
    public ResourceLocation getId()
    {
        return ModRecipeTypes.MULTI_OUTPUT_ID;
    }
}

