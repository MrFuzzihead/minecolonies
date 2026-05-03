package com.minecolonies.core.colony.buildings.workerbuildings;

import com.google.common.collect.ImmutableList;
import com.minecolonies.api.colony.ICitizenData;
import com.minecolonies.api.colony.IColony;
import com.minecolonies.api.colony.IColonyManager;
import com.minecolonies.api.colony.buildings.IBuilding;
import com.minecolonies.api.colony.jobs.registry.JobEntry;
import com.minecolonies.api.colony.requestsystem.token.IToken;
import com.minecolonies.api.crafting.*;
import com.minecolonies.api.util.CraftingUtils;
import com.minecolonies.api.util.InventoryUtils;
import com.minecolonies.api.util.OptionalPredicate;
import com.minecolonies.core.colony.buildings.AbstractBuilding;
import com.minecolonies.core.colony.buildings.modules.AbstractCraftingBuildingModule;
// [1.7.10] int[] -> int x,y,z
// [1.7.10] tags removed
// [1.7.10] tags removed
// [1.7.10] net.minecraft.world.item.* removed - use 1.7.10 equivalents
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.init.Items;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
// [1.7.10] net.minecraftforge.common.Tags removed
// [1.7.10] items shim in com.minecolonies.api.shim
// [1.7.10] registries removed
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.minecolonies.api.util.constant.BuildingConstants.CONST_DEFAULT_MAX_BUILDING_LEVEL;
import static com.minecolonies.api.util.constant.TagConstants.CRAFTING_DYER;
import static com.minecolonies.api.util.constant.TagConstants.CRAFTING_DYER_SMELTING;

/**
 * Class of the dyer building.
 */
public class BuildingDyer extends AbstractBuilding
{
    /**
     * Description string of the building.
     */
    private static final String DYER = "dyer";

    /**
     * Instantiates a new dyer building.
     *
     * @param c the colony.
     * @param l the location
     */
    public BuildingDyer(final IColony c, final int[] l)
    {
        super(c, l);
    }

    @NotNull
    @Override
    public String getSchematicName()
    {
        return DYER;
    }

    @Override
    public int getMaxBuildingLevel()
    {
        return CONST_DEFAULT_MAX_BUILDING_LEVEL;
    }

    public static class CraftingModule extends AbstractCraftingBuildingModule.Crafting
    {
        private List<ItemStorage> woolItems;

        /**
         * Create a new module.
         *
         * @param jobEntry the entry of the job.
         */
        public CraftingModule(final JobEntry jobEntry)
        {
            super(jobEntry);
        }

        @NotNull
        @Override
        public OptionalPredicate<ItemStack> getIngredientValidator()
        {
            return CraftingUtils.getIngredientValidatorBasedOnTags(CRAFTING_DYER)
                    .combine(super.getIngredientValidator());
        }

        @Override
        public boolean isRecipeCompatible(@NotNull final IGenericRecipe recipe)
        {
            if (!super.isRecipeCompatible(recipe)) return false;
            return CraftingUtils.isRecipeCompatibleBasedOnTags(recipe, CRAFTING_DYER).orElse(false);
        }

        @Override
        public void improveRecipe(final IRecipeStorage recipe, final int count, final ICitizenData citizen)
        {
            // don't improve any dyeing recipes
        }

        @NotNull
        @Override
        public List<IGenericRecipe> getAdditionalRecipesForDisplayPurposesOnly(@NotNull World world)
        {
            // [1.7.10] TODO Phase 8: port leather dyeing display recipes; Tags/DyeableLeatherItem not available in 1.7.10
            return new ArrayList<>(super.getAdditionalRecipesForDisplayPurposesOnly(world));
        }

        @Override
        public IRecipeStorage getFirstRecipe(Predicate<ItemStack> stackPredicate)
        {
            IRecipeStorage recipe = super.getFirstRecipe(stackPredicate);

            if (recipe == null && stackPredicate.test(new ItemStack(Blocks.wool, 1, 0))) // [1.7.10] WHITE_WOOL -> wool meta 0
            {
                final HashMap<ItemStorage, Integer> inventoryCounts = new HashMap<>();

                if (!building.getColony().getServerBuildingManager().hasWarehouse())
                {
                    return null;
                }

                for (ItemStorage color : getWoolItems())
                {
                    for (IBuilding wareHouse : building.getColony().getServerBuildingManager().getWareHouses())
                    {
                        final int colorCount = InventoryUtils.getCountFromBuilding(wareHouse, color);
                        inventoryCounts.put(color, inventoryCounts.getOrDefault(color, 0) + colorCount);
                    }
                }

                if (inventoryCounts.isEmpty()) return null;
                ItemStorage woolToUse = inventoryCounts.entrySet().stream().min(java.util.Map.Entry.comparingByValue(Comparator.reverseOrder())).get().getKey();

                final IToken<?> token = getTokenForWool(woolToUse);
                recipe = IColonyManager.getInstance().getRecipeManager().getRecipe(token);
            }
            return recipe;
        }

        @Override
        public boolean holdsRecipe(final IToken<?> token)
        {
            if (super.holdsRecipe(token))
            {
                return true;
            }

            final IRecipeStorage recipe = IColonyManager.getInstance().getRecipeManager().getRecipe(token);
            if (recipe == null)
            {
                return false;
            }

            return recipe.getPrimaryOutput().getItem() == Item.getItemFromBlock(Blocks.wool) // [1.7.10] Items.WHITE_WOOL -> wool meta 0
                   && recipe.getPrimaryOutput().getItemDamage() == 0;
        }

        @Override
        public IRecipeStorage getFirstFulfillableRecipe(final Predicate<ItemStack> stackPredicate, final int count, final boolean considerReservation)
        {
            IRecipeStorage recipe = super.getFirstFulfillableRecipe(stackPredicate, count, considerReservation);
            if (recipe == null && stackPredicate.test(new ItemStack(Blocks.wool, 1, 0))) // [1.7.10] WHITE_WOOL -> wool meta 0
            {
                final Set<net.minecraftforge.items.IItemHandler> handlers = new HashSet<>();
                for (final ICitizenData workerEntity : building.getAllAssignedCitizen())
                {
                    handlers.add(workerEntity.getInventory());
                }

                for (ItemStorage color : getWoolItems())
                {
                    IToken<?> token = getTokenForWool(color);

                    final IRecipeStorage storage = IColonyManager.getInstance().getRecipeManager().getRecipes().get(token);

                    IRecipeStorage toTest = storage.getRecipeType() instanceof MultiOutputRecipe ? storage.getClassicForMultiOutput(stackPredicate) : storage;
                    if (toTest.canFullFillRecipe(count, considerReservation ? reservedStacks() : Collections.emptyMap(), new ArrayList<>(handlers), building))
                    {
                        return toTest;
                    }
                }
            }
            return recipe;
        }

        /**
         * Builds and returns a list of all colored wool types (non-white).
         * [1.7.10] Wool uses metadata 0-15; white is meta 0.
         */
        private List<ItemStorage> getWoolItems()
        {
            if (woolItems == null)
            {
                woolItems = new ArrayList<>();
                final Item woolItem = Item.getItemFromBlock(Blocks.wool);
                for (int meta = 1; meta <= 15; meta++) // meta 0 = white, skip it
                {
                    woolItems.add(new ItemStorage(new ItemStack(woolItem, 1, meta)));
                }
            }
            return woolItems;
        }

        /**
         * Creates the recipe to undye the given wool and returns its token.
         * [1.7.10] Uses dye meta 0 (white) and wool meta 0 (white).
         */
        private IToken<?> getTokenForWool(ItemStorage wool)
        {
            final IRecipeStorage tempRecipe = RecipeStorage.builder()
                    .withInputs(ImmutableList.of(wool, new ItemStorage(new ItemStack(Items.dye, 1, 0)))) // [1.7.10] WHITE_DYE -> dye meta 0
                    .withPrimaryOutput(new ItemStack(Blocks.wool, 1, 0)) // [1.7.10] WHITE_WOOL -> wool meta 0
                    .build();

            return IColonyManager.getInstance().getRecipeManager().checkOrAddRecipe(tempRecipe);
        }
    }

    public static class SmeltingModule extends AbstractCraftingBuildingModule.Smelting
    {
        /**
         * Create a new module.
         *
         * @param jobEntry the entry of the job.
         */
        public SmeltingModule(final JobEntry jobEntry)
        {
            super(jobEntry);
        }

        @NotNull
        @Override
        public OptionalPredicate<ItemStack> getIngredientValidator()
        {
            return CraftingUtils.getIngredientValidatorBasedOnTags(CRAFTING_DYER_SMELTING)
                    .combine(super.getIngredientValidator());
        }

        @Override
        public boolean isRecipeCompatible(@NotNull final IGenericRecipe recipe)
        {
            if (!super.isRecipeCompatible(recipe))
            {
                return false;
            }
            return CraftingUtils.isRecipeCompatibleBasedOnTags(recipe, CRAFTING_DYER_SMELTING).orElse(false);
        }

        @Override
        public void improveRecipe(final IRecipeStorage recipe, final int count, final ICitizenData citizen)
        {
            // don't improve any dyeing recipes
        }
    }
}




