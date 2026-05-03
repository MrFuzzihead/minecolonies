package com.minecolonies.core.colony.buildings.workerbuildings;

import com.minecolonies.api.colony.IColony;
import com.minecolonies.api.colony.buildings.modules.IBuildingEventsModule;
import com.minecolonies.api.colony.buildings.modules.IHasRequiredItemsModule;
import com.minecolonies.api.colony.buildings.modules.IPersistentModule;
import com.minecolonies.api.colony.buildings.modules.settings.ISettingKey;
import com.minecolonies.api.colony.jobs.ModJobs;
import com.minecolonies.api.colony.requestsystem.request.IRequest;
import com.minecolonies.api.colony.requestsystem.requestable.IDeliverable;
import com.minecolonies.api.crafting.GenericRecipe;
import com.minecolonies.api.crafting.IGenericRecipe;
import com.minecolonies.api.crafting.ItemStorage;
import com.minecolonies.api.equipment.ModEquipmentTypes;
import com.minecolonies.api.items.ModItems;
import com.minecolonies.api.util.ItemStackUtils;
import com.minecolonies.core.colony.buildings.AbstractBuilding;
import com.minecolonies.core.colony.buildings.modules.AnimalHerdingModule;
import com.minecolonies.core.colony.buildings.modules.settings.IntSetting;
import com.minecolonies.core.colony.buildings.modules.settings.SettingKey;
import com.minecolonies.core.colony.buildings.modules.settings.StringSetting;
// [1.7.10] int[] -> int x,y,z
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import com.minecolonies.api.util.Tuple;
// [1.7.10] world.entity removed
// [1.7.10] world.entity removed
// [1.7.10] world.entity removed
// [1.7.10] world.entity removed
import net.minecraft.item.ItemStack;
import net.minecraft.init.Items;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Predicate;

import static com.minecolonies.api.util.constant.Constants.MOD_ID;
import static com.minecolonies.api.util.constant.EquipmentLevelConstants.TOOL_LEVEL_WOOD_OR_GOLD;

/**
 * Creates a new building for the Cowboy.
 */
public class BuildingCowboy extends AbstractBuilding
{
    /**
     * Description of the job executed in the hut.
     */
    private static final String COWBOY = "cowboy";

    /**
     * The hut name, used for the lang string in the GUI
     */
    private static final String HUT_NAME = "cowboyhut";

    /**
     * Max building World of the hut.
     */
    private static final int MAX_BUILDING_LEVEL = 5;

    /**
     * Milking amount setting.
     */
    public static final ISettingKey<IntSetting> MILKING_AMOUNT  = new SettingKey<>(IntSetting.class, new ResourceLocation(MOD_ID, "milking_amount"));

    /**
     * Stewing amount setting.
     */
    public static final ISettingKey<IntSetting> STEWING_AMOUNT = new SettingKey<>(IntSetting.class, new ResourceLocation(MOD_ID, "stewing_amount"));

    /**
     * Milking days setting.
     */
    public static final ISettingKey<IntSetting> MILKING_DAYS  = new SettingKey<>(IntSetting.class, new ResourceLocation(MOD_ID, "milking_days"));

    /**
     * Milking days setting.
     */
    public static final ISettingKey<StringSetting> MILK_ITEM  = new SettingKey<>(StringSetting.class, new ResourceLocation(MOD_ID, "milk_item"));


    /**
     * Instantiates the building.
     *
     * @param c the colony.
     * @param l the location.
     */
    public BuildingCowboy(final IColony c, final int[] l)
    {
        super(c, l);
    }

    @NotNull
    @Override
    public String getSchematicName()
    {
        return COWBOY;
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
     * Get the milking input item.
     * @return the input item.
     */
    public ItemStack getMilkInputItem()
    {
        if (getSetting(MILK_ITEM).getValue().equals(ModItems.large_milk_bottle.getDescriptionId()))
        {
            return ModItems.large_empty_bottle.getDefaultInstance();
        }
        return Items.bucket.getDefaultInstance();
    }

    /**
     * Get the milking output item.
     * @return the output item.
     */
    public ItemStack getMilkOutputItem()
    {
        if (getSetting(MILK_ITEM).getValue().equals(ModItems.large_milk_bottle.getDescriptionId()))
        {
            return ModItems.large_milk_bottle.getDefaultInstance();
        }
        return Items.milk_bucket.getDefaultInstance();
    }

    /**
     * Cow (and Mooshroom) herding module
     */
    public static class HerdingModule extends AnimalHerdingModule implements IBuildingEventsModule, IPersistentModule
    {
        private int currentMilk;
        private int currentStew;
        private int currentMilkDays;

        public HerdingModule()
        {
            super(ModJobs.cowboy.get(), a -> a instanceof net.minecraft.entity.passive.EntityCow, new ItemStorage(Items.wheat, 2));
        }

        @Override
        public Map<Predicate<ItemStack>, Tuple<Integer, Boolean>> getRequiredItemsAndAmount()
        {
            final Map<Predicate<ItemStack>, Tuple<Integer, Boolean>> requiredItems = super.getRequiredItemsAndAmount();
            final int days = Math.max(1, getBuilding().getSetting(MILKING_DAYS).getValue());
            final int bucketsToKeep = (int) Math.ceil(2D * getBuilding().getSetting(MILKING_AMOUNT).getValue() / days);
            final int bowlsToKeep = (int) Math.ceil(2D * getBuilding().getSetting(STEWING_AMOUNT).getValue() / days);

            if (bucketsToKeep > 0)
            {
                requiredItems.put(s -> s.is(Items.bucket), new Tuple<>(bucketsToKeep, false));
                requiredItems.put(s -> s.is(ModItems.large_empty_bottle), new Tuple<>(bucketsToKeep, true));
            }

            if (bowlsToKeep > 0)
            {
                requiredItems.put(s -> s.is(Items.bowl), new Tuple<>(bowlsToKeep, false));
            }

            return requiredItems;
        }

        @NotNull
        @Override
        public List<IGenericRecipe> getRecipesForDisplayPurposesOnly(@NotNull net.minecraft.entity.passive.EntityAnimal animal)
        {
            // [1.7.10] EntityType/MushroomCow/Goat not available; return parent result only
            return new ArrayList<>(super.getRecipesForDisplayPurposesOnly(animal));
        }

        @Override
        public void serializeNBT(@NotNull NBTTagCompound compound)
        {
            compound.putInt("milkValue", currentMilk);
            compound.putInt("stewValue", currentStew);
            compound.putInt("milkDays", currentMilkDays);
        }

        @Override
        public void deserializeNBT(NBTTagCompound compound)
        {
            this.currentMilk = compound.getInt("milkValue");
            this.currentStew = compound.getInt("stewValue");
            this.currentMilkDays = compound.getInt("milkDays");
        }

        @Override
        public void onWakeUp()
        {
            ++this.currentMilkDays;

            if (this.currentMilkDays >= getBuilding().getSetting(MILKING_DAYS).getValue())
            {
                this.currentMilk = 0;
                this.currentStew = 0;
                this.currentMilkDays = 0;
            }
        }

        /**
         * @return true if the cowboy should be allowed to try to milk (not yet reached limit)
         */
        public boolean canTryToMilk()
        {
            return this.currentMilk < getBuilding().getSetting(MILKING_AMOUNT).getValue();
        }

        /**
         * @return true if the cowboy should be allowed to try to collect stew (not yet reached limit)
         */
        public boolean canTryToStew()
        {
            return this.currentStew < getBuilding().getSetting(STEWING_AMOUNT).getValue();
        }

        /**
         * Called to record successful milking.
         */
        public void onMilked()
        {
            ++this.currentMilk;
        }

        /**
         * Called to record successful stewing.
         */
        public void onStewed()
        {
            ++this.currentStew;
        }
    }
}




