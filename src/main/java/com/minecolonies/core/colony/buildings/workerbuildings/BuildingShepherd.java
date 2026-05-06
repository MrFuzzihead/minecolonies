package com.minecolonies.core.colony.buildings.workerbuildings;
import net.minecraft.world.entity.animal.Animal;

import com.minecolonies.api.colony.IColony;
import com.minecolonies.api.colony.buildings.modules.settings.ISettingKey;
import com.minecolonies.api.colony.jobs.ModJobs;
import com.minecolonies.api.crafting.GenericRecipe;
import com.minecolonies.api.crafting.IGenericRecipe;
import com.minecolonies.api.crafting.ItemStorage;
import com.minecolonies.api.equipment.ModEquipmentTypes;
import com.minecolonies.core.colony.buildings.AbstractBuilding;
import com.minecolonies.core.colony.buildings.modules.AnimalHerdingModule;
import com.minecolonies.core.colony.buildings.modules.settings.BoolSetting;
import com.minecolonies.core.colony.buildings.modules.settings.SettingKey;
// [1.7.10] int[] -> int x,y,z
import net.minecraft.util.ResourceLocation;
// [1.7.10] tags removed
// [1.7.10] world.entity removed
// [1.7.10] world.entity removed
import net.minecraft.item.ItemStack;
import net.minecraft.init.Items;
// [1.7.10] registries removed
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Creates a new building for the Shepherd.
 */
public class BuildingShepherd extends AbstractBuilding
{
    /**
     * Automatic dyeing.
     */
    public static final ISettingKey<BoolSetting> DYEING = new SettingKey<>(BoolSetting.class, new ResourceLocation(com.minecolonies.api.util.constant.Constants.MOD_ID, "dyeing"));

    /**
     * Automatic shearing.
     */
    public static final ISettingKey<BoolSetting> SHEARING = new SettingKey<>(BoolSetting.class, new ResourceLocation(com.minecolonies.api.util.constant.Constants.MOD_ID, "shearing"));

    /**
     * Description of the job executed in the hut.
     */
    private static final String SHEPHERD = "shepherd";

    /**
     * The hut name, used for the lang string in the GUI
     */
    private static final String HUT_NAME = "shepherdhut";

    /**
     * Max building World of the hut.
     */
    private static final int MAX_BUILDING_LEVEL = 5;

    /**
     * Instantiates the building.
     *
     * @param c the colony.
     * @param l the location.
     */
    public BuildingShepherd(final IColony c, final int[] l)
    {
        super(c, l);
    }

    @NotNull
    @Override
    public String getSchematicName()
    {
        return SHEPHERD;
    }

    @Override
    public int getMaxBuildingLevel()
    {
        return MAX_BUILDING_LEVEL;
    }

    @Override
    public boolean canEat(final ItemStack stack)
    {
        if (stack.getItem() == Items.wheat)
        {
            return false;
        }
        return super.canEat(stack);
    }

    /**
     * Sheep herding module
     */
    public static class HerdingModule extends AnimalHerdingModule
    {
        public HerdingModule()
        {
            super(ModJobs.shepherd.get(), a -> a instanceof net.minecraft.entity.passive.EntitySheep, new ItemStorage(Items.wheat, 2));
        }

        @NotNull
        @Override
        public List<IGenericRecipe> getRecipesForDisplayPurposesOnly(@NotNull net.minecraft.entity.passive.EntityAnimal animal)
        {
            // [1.7.10] ItemTags/ForgeRegistries not available; return parent result only
            return new ArrayList<>(super.getRecipesForDisplayPurposesOnly(animal));
        }

        // we *could* add a custom crafting module to show shears -> wool as well, but it's good
        // enough to show it as a drop on kill (which also happens).
    }
}




