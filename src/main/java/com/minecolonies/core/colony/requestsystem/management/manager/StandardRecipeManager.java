package com.minecolonies.core.colony.requestsystem.management.manager;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableMap;
import com.minecolonies.api.colony.requestsystem.StandardFactoryController;
import com.minecolonies.api.colony.requestsystem.token.IToken;
import com.minecolonies.api.crafting.IRecipeManager;
import com.minecolonies.api.crafting.IRecipeStorage;
import com.minecolonies.api.util.NBTUtils;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTBase;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

public class StandardRecipeManager implements IRecipeManager
{
    /**
     * The NBTBase to store the recipes to NBT.
     */
    private static final String TAG_RECIPES = "recipes";

    /**
     * Map of all recipes which have been discovered globally already.
     * Initializing with 4096 as size since default is 16, and growth is costly
     */
    private final BiMap<IToken<?>, IRecipeStorage> recipes = HashBiMap.create(4096);

    /**
     * Immutable cache.
     */
    private ImmutableMap<IToken<?>, IRecipeStorage> cache = null;

    /**
     * The list of recipes marked as used this session
     */
    private final Set<IToken<?>> usedRecipes = new HashSet<>();

    /**
     * If was written to since the last save.
     */
    private boolean dirty = false;

    /**
     * Nbt cache.
     */
    private NBTTagList nbtCache = null;

    @Override
    public ImmutableMap<IToken<?>, IRecipeStorage> getRecipes()
    {
        if (cache == null)
        {
            cache = ImmutableMap.copyOf(recipes);
        }
        return cache;
    }

    @Override
    public IRecipeStorage getRecipe(final IToken<?> token)
    {
        return recipes.get(token);
    }

    @Override
    public IToken<?> addRecipe(final IRecipeStorage storage)
    {
        recipes.put(storage.getToken(), storage);
        registerUse(storage.getToken());
        cache = null;
        dirty = true;
        return storage.getToken();
    }

    @Override
    public IToken<?> checkOrAddRecipe(final IRecipeStorage storage)
    {
        final IToken<?> token = getRecipeId(storage);
        if (token == null)
        {
            return addRecipe(storage);
        }
        registerUse(token);
        return token;
    }

    @Override
    public IToken<?> getRecipeId(final IRecipeStorage storage)
    {
        return recipes.inverse().get(storage);
    }

    @Override
    public void write(@NotNull final NBTTagCompound compound)
    {
        if (dirty || nbtCache == null)
        {
            nbtCache = recipes.entrySet().stream().filter(recipeEntry -> usedRecipes.contains(recipeEntry.getKey())).map(entry -> StandardFactoryController.getInstance().serialize(entry.getValue())).collect(NBTUtils.toListNBT());
        }

        compound.put(TAG_RECIPES, nbtCache);
        dirty = false;
    }

    @Override
    public void read(@NotNull final NBTTagCompound compound)
    {
        final NBTTagList list = compound.getList(TAG_RECIPES, NBTBase.TAG_COMPOUND);
        for (int i = 0; i < list.size(); i++)
        {
            IRecipeStorage recipe = StandardFactoryController.getInstance().deserialize(list.getCompound(i));
            if (recipe != null && !recipes.containsValue(recipe) && !recipe.getCleanedInput().isEmpty() && (!recipe.getPrimaryOutput().isEmpty() || recipe.getLootTable() != null || !recipe.getAlternateOutputs().isEmpty()))
            {
                try
                {
                    recipes.put(recipe.getToken(), recipe);
                }
                catch (Exception e)
                {
                    // Eat the exception
                }
            }
        }
        cache = null;
        nbtCache = list;
    }

    @Override
    public void reset()
    {
        recipes.clear();
        usedRecipes.clear();
    }

    @Override
    public void registerUse(final IToken<?> token)
    {
        if (usedRecipes.contains(token))
        {
            return;
        }
        usedRecipes.add(token);
        dirty = true;
    }
}




