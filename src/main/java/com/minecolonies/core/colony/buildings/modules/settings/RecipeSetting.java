package com.minecolonies.core.colony.buildings.modules.settings;

// [1.7.10] blockui replaced by ModularUI2
// [1.7.10] blockui replaced by ModularUI2
// [1.7.10] blockui replaced by ModularUI2
// [1.7.10] blockui replaced by ModularUI2
import com.minecolonies.api.colony.IColonyManager;
import com.minecolonies.api.colony.buildings.IBuilding;
import com.minecolonies.api.colony.buildings.modules.ICommonSettingsModule;
import com.minecolonies.api.colony.buildings.modules.ICraftingBuildingModule;
import com.minecolonies.api.colony.buildings.modules.ISettingsModule;
import com.minecolonies.api.colony.buildings.modules.settings.ICraftingSetting;
import com.minecolonies.api.colony.buildings.modules.settings.ISetting;
import com.minecolonies.api.colony.buildings.modules.settings.ISettingKey;
import com.minecolonies.api.colony.buildings.modules.settings.ISettingsModuleView;
import com.minecolonies.api.colony.buildings.views.IBuildingView;
import com.minecolonies.api.colony.requestsystem.token.IToken;
import com.minecolonies.api.crafting.IRecipeStorage;
import com.minecolonies.core.colony.buildings.moduleviews.CraftingModuleView;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ResourceLocation;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.List;

/**
 * Stores a recipe based setting.
 */
public class RecipeSetting implements ICraftingSetting
{
    /**
     * Current index of the setting.
     */
    protected IToken<?> selectedRecipe;

    /**
     * The specific crafting module.
     */
    protected final String craftingModuleId;

    /**
     * Create a new crafting setting.
     * @param craftingModuleId the crafting module id.
     */
    public RecipeSetting(final String craftingModuleId)
    {
        this.craftingModuleId = craftingModuleId;
    }

    /**
     * Create a new string list setting.
     *
     * @param selectedRecipe the current selected recipe.
     * @param craftingModuleId the crafting module id.
     */
    public RecipeSetting(final IToken<?> selectedRecipe, final String craftingModuleId)
    {
        this.selectedRecipe = selectedRecipe;
        this.craftingModuleId = craftingModuleId;
    }

    @Override
    public IRecipeStorage getValue(final IBuilding building)
    {
        final ICraftingBuildingModule craftingModule = building.getModuleMatching(ICraftingBuildingModule.class, m -> m.getId().equals(craftingModuleId));
        for (final IToken<?> token : craftingModule.getRecipes())
        {
            if (token.equals(selectedRecipe))
            {
                return IColonyManager.getInstance().getRecipeManager().getRecipe(selectedRecipe);
            }
        }

        selectedRecipe = building.getFirstModuleOccurance(ICraftingBuildingModule.class).getRecipes().get(0);
        return IColonyManager.getInstance().getRecipeManager().getRecipe(selectedRecipe);
    }

    @Override
    public IRecipeStorage getValue(final IBuildingView building)
    {
        final CraftingModuleView craftingModule = building.getModuleViewMatching(CraftingModuleView.class, m -> m.getId().equals(craftingModuleId));

        for (final IRecipeStorage recipe : craftingModule.getRecipes())
        {
            if (recipe.getToken().equals(selectedRecipe))
            {
                return recipe;
            }
        }

        selectedRecipe = craftingModule.getRecipes().get(0).getToken();
        return craftingModule.getRecipes().get(0);
    }

    @Override
    public List<ItemStack> getSettings(final IBuilding building)
    {
        final List<ItemStack> settings = new ArrayList<>();
        for (final IToken<?> token : building.getFirstModuleOccurance(ICraftingBuildingModule.class).getRecipes())
        {
            settings.add(IColonyManager.getInstance().getRecipeManager().getRecipe(token).getPrimaryOutput());
        }
        return new ArrayList<>(settings);
    }

    @Override
    public List<ItemStack> getSettings(final IBuildingView building)
    {
        final List<ItemStack> settings = new ArrayList<>();
        for (final IRecipeStorage recipe : building.getModuleViewByType(CraftingModuleView.class).getRecipes())
        {
            settings.add(recipe.getPrimaryOutput());
        }
        return new ArrayList<>(settings);
    }

    @Override
    public ResourceLocation getLayoutItem()
    {
        return new ResourceLocation("minecolonies:gui/layouthuts/layoutcraftingsetting.xml");
    }

    @Override
    public void setupHandler(
      final ISettingKey<?> key,
      final Object pane,
      final ICommonSettingsModule settingsModuleView,
      final IBuildingView building,
      final Object /* BOWindow: todo ModularUI2 */ window)
    {
        // [1.7.10] todo: ModularUI2 port
    }

    @Override
    public void render(
      final ISettingKey<?> key,
      final Object pane,
      final ICommonSettingsModule settingsModuleView,
      final IBuildingView building,
      final Object /* BOWindow: todo ModularUI2 */ window)
    {
        // [1.7.10] todo: ModularUI2 port
    }

    @Override
    public void set(final IRecipeStorage value)
    {
        selectedRecipe = value.getToken();
    }

    @Override
    public boolean isActive(final ISettingsModule module)
    {
        final ICraftingBuildingModule craftingModule = module.getBuilding().getModuleMatching(ICraftingBuildingModule.class, m -> m.getId().equals(craftingModuleId));
        return !craftingModule.getRecipes().isEmpty();
    }

    @Override
    public boolean isActive(final ISettingsModuleView module)
    {
        final CraftingModuleView craftingModule = module.getBuildingView().getModuleViewMatching(CraftingModuleView.class, m -> m.getId().equals(craftingModuleId));
        return craftingModule != null && !craftingModule.getRecipes().isEmpty();
    }

    @Override
    public IToken<?> getValue()
    {
        return selectedRecipe;
    }

    @Override
    public boolean shouldHideWhenInactive()
    {
        return true;
    }

    @Override
    public void copyValue(final ISetting<?> setting)
    {
        if (setting instanceof final RecipeSetting other)
        {
            selectedRecipe = other.selectedRecipe;
        }
    }
}



