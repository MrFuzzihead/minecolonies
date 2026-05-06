package com.minecolonies.core.colony.buildings.workerbuildings;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.api.distmarker.Dist;

import net.minecraft.tags.TagKey;

// [1.7.10] blockui replaced by ModularUI2
import com.minecolonies.api.colony.IColony;
import com.minecolonies.api.colony.IColonyManager;
import com.minecolonies.api.colony.buildingextensions.IBuildingExtension;
import com.minecolonies.api.colony.buildingextensions.registry.BuildingExtensionRegistries;
import com.minecolonies.api.colony.buildings.modules.settings.ISettingKey;
import com.minecolonies.api.colony.jobs.registry.JobEntry;
import com.minecolonies.api.crafting.GenericRecipe;
import com.minecolonies.api.crafting.IGenericRecipe;
import com.minecolonies.api.equipment.ModEquipmentTypes;
import com.minecolonies.api.util.BlockPosUtil;
import com.minecolonies.api.util.CraftingUtils;
import com.minecolonies.api.util.ItemStackUtils;
import com.minecolonies.api.util.OptionalPredicate;
import com.minecolonies.api.util.constant.Constants;
import com.minecolonies.core.blocks.MinecoloniesCropBlock;
import com.minecolonies.core.client.gui.modules.building.FarmFieldsModuleWindow;
import com.minecolonies.core.colony.buildingextensions.FarmField;
import com.minecolonies.core.colony.buildings.AbstractBuilding;
import com.minecolonies.core.colony.buildings.modules.AbstractCraftingBuildingModule;
import com.minecolonies.core.colony.buildings.modules.BuildingExtensionsModule;
import com.minecolonies.core.colony.buildings.modules.settings.BoolSetting;
import com.minecolonies.core.colony.buildings.modules.settings.SettingKey;
import com.minecolonies.core.colony.buildings.moduleviews.FieldsModuleView;
import com.minecolonies.core.items.ItemCrop;
import net.minecraft.util.EnumChatFormatting;
// [1.7.10] client removed (use @SideOnly)
// [1.7.10] int[] -> int x,y,z
// [1.7.10] net.minecraft.core.Registry removed
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IChatComponent;
// [1.7.10] chat.String replaced by IChatComponent/ChatComponentText
import net.minecraft.util.ResourceLocation;
// [1.7.10] tags removed
import com.minecolonies.api.util.Tuple;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.init.Items;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.init.Blocks;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.BlockStem;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
// [1.7.10] net.minecraftforge.common.Tags removed
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static com.minecolonies.api.util.constant.EquipmentLevelConstants.TOOL_LEVEL_WOOD_OR_GOLD;
import static com.minecolonies.api.util.constant.NbtTagConstants.*;
import static com.minecolonies.api.util.constant.TagConstants.CRAFTING_FARMER;
import static com.minecolonies.api.util.constant.TranslationConstants.PARTIAL_JEI_INFO;
import static com.minecolonies.api.util.constant.translation.GuiTranslationConstants.FIELD_LIST_FARMER_NO_SEED;

/**
 * Class which handles the farmer building.
 */
public class BuildingFarmer extends AbstractBuilding
{
    /**
     * The beekeeper mode.
     */
    public static final ISettingKey<BoolSetting> FERTILIZE =
      new SettingKey<>(BoolSetting.class, new ResourceLocation(Constants.MOD_ID, "fertilize"));

    /**
     * Descriptive string of the profession.
     */
    private static final String FARMER = "farmer";

    /**
     * The maximum building World of the hut.
     */
    private static final int MAX_BUILDING_LEVEL = 5;

    /**
     * The offset to work at relative to the scarecrow.
     */
    @Nullable
    private int[] workingOffset;

    /**
     * The previous position which has been worked at.
     */
    @Nullable
    private int[] prevPos;

    /**
     * The current index within the current field
     */
    private int cell = -1;

    /**
     * Public constructor which instantiates the building.
     *
     * @param c the colony the building is in.
     * @param l the position it has been placed (it's id).
     */
    public BuildingFarmer(final IColony c, final int[] l)
    {
        super(c, l);
        keepX.put(itemStack -> ItemStackUtils.hasEquipmentLevel(itemStack, ModEquipmentTypes.hoe.get(), TOOL_LEVEL_WOOD_OR_GOLD, getMaxEquipmentLevel()), new Tuple<>(1, true));
        keepX.put(itemStack -> ItemStackUtils.hasEquipmentLevel(itemStack, ModEquipmentTypes.axe.get(), TOOL_LEVEL_WOOD_OR_GOLD, getMaxEquipmentLevel()), new Tuple<>(1, true));
    }

    @Override
    public boolean canBeGathered()
    {
        // Normal crafters are only gatherable when they have a task, i.e. while producing stuff.
        // BUT, the farmer both gathers and crafts things now, like the lumberjack
        return true;
    }

    /**
     * Override this method if you want to keep an amount of items in inventory. When the inventory is full, everything get's dumped into the building chest. But you can use this
     * method to hold some stacks back.
     *
     * @return a map of objects which should be kept.
     */
    @Override
    public Map<Predicate<ItemStack>, Tuple<Integer, Boolean>> getRequiredItemsAndAmount()
    {
        final Map<Predicate<ItemStack>, Tuple<Integer, Boolean>> toKeep = new HashMap<>(super.getRequiredItemsAndAmount());
        for (BuildingExtensionsModule module : getModulesByType(BuildingExtensionsModule.class))
        {
            for (final IBuildingExtension field : module.getOwnedExtensions())
            {
                if (field instanceof FarmField farmField && !farmField.getSeed().isEmpty())
                {
                    toKeep.put(stack -> ItemStack.isSameItem(farmField.getSeed(), stack), new Tuple<>(64, true));
                }
            }
        }
        return toKeep;
    }

    @Override
    public boolean canEat(final ItemStack stack)
    {
        for (BuildingExtensionsModule module : getModulesByType(BuildingExtensionsModule.class))
        {
            for (final IBuildingExtension field : module.getOwnedExtensions())
            {
                if (field instanceof FarmField farmField && !farmField.getSeed().isEmpty() && ItemStackUtils.compareItemStacksIgnoreStackSize(farmField.getSeed(), stack))
                {
                    return false;
                }
            }
        }

        if (stack.getItem() == Items.wheat)
        {
            return false;
        }
        return super.canEat(stack);
    }

    @Override
    public int getMaxBuildingLevel()
    {
        return MAX_BUILDING_LEVEL;
    }

    @NotNull
    @Override
    public String getSchematicName()
    {
        return FARMER;
    }

    /**
     * Getter for request fertilizer
     */
    public boolean requestFertilizer()
    {
        return getSetting(FERTILIZE).getValue();
    }

    /**
     * Get the offset to work at relative to the scarecrow.
     * @return the int[].
     */
    public int[] getWorkingOffset()
    {
        return workingOffset;
    }

    /**
     * Set the current index within the current field
     * @param i the value to set.
     * @return current value.
     */
    public int setCell(final int i)
    {
        cell = i;
        return cell;
    }

    /**
     * Get the current index within the current field
     * @return current value.
     */
    public int getCell()
    {
        return cell;
    }

    /**
     * Set the previous position which has been worked at.
     * @param position to set.
     */
    public void setPrevPos(final int[] position)
    {
        this.prevPos = position;
    }

    /**
     * Set the offset to work at relative to the scarecrow.
     * @param blockPos the pos to set.
     */
    public void setWorkingOffset(final int[] blockPos)
    {
        this.workingOffset = blockPos;
    }

    /**
     * Get the previous position which has been worked at.
     * @return current prev pos.
     */
    public int[] getPrevPos()
    {
        return prevPos;
    }

    @Override
    public NBTTagCompound serializeNBT()
    {
        final NBTTagCompound NBTTagCompound = super.serializeNBT();
        NBTTagCompound.putInt(TAG_CELL, this.cell);
        if (workingOffset != null)
        {
            BlockPosUtil.write(NBTTagCompound, TAG_WORKING_OFFSET, workingOffset);
        }
        if (prevPos != null)
        {
            BlockPosUtil.write(NBTTagCompound, TAG_PREV_POS, prevPos);
        }
        return NBTTagCompound;
    }

    @Override
    public void deserializeNBT(final NBTTagCompound compound)
    {
        super.deserializeNBT(compound);
        if (compound.contains(TAG_CELL))
        {
            this.cell = compound.getInt(TAG_CELL);
        }
        if (compound.contains(TAG_WORKING_OFFSET))
        {
            this.workingOffset = BlockPosUtil.read(compound, TAG_WORKING_OFFSET);
        }
        if (compound.contains(TAG_PREV_POS))
        {
            this.prevPos = BlockPosUtil.read(compound, TAG_PREV_POS);
        }
    }

    /**
     * Field module implementation for the farmer.
     */
    public static class FarmerFieldsModule extends BuildingExtensionsModule
    {
        @Override
        protected int getMaxExtensionCount()
        {
            return building.getBuildingLevel();
        }

        @Override
        public Class<?> getExpectedExtensionType()
        {
            return FarmField.class;
        }

        @Override
        public @NotNull List<IBuildingExtension> getMatchingExtension(final Predicate<IBuildingExtension> predicateToMatch)
        {
            return building.getColony().getServerBuildingManager().getBuildingExtensions(field -> field.getBuildingExtensionType() == BuildingExtensionRegistries.farmField.get() && predicateToMatch.test(field));
        }

        @Override
        public boolean canAssignExtensionOverride(final IBuildingExtension extension)
        {
            return extension instanceof FarmField farmField && !farmField.getSeed().isEmpty();
        }
    }

    /**
     * Field module view implementation for the farmer.
     */
    public static class FarmerFieldsModuleView extends FieldsModuleView
    {
        @Override
        @OnlyIn(Dist.CLIENT)
        public Object /* BOWindow: todo ModularUI2 */ getWindow()
        {
            return new FarmFieldsModuleWindow(this);
        }

        @Override
        public boolean canAssignFieldOverride(final IBuildingExtension field)
        {
            return field instanceof FarmField farmField && !farmField.getSeed().isEmpty();
        }

        @Override
        protected List<IBuildingExtension> getFieldsInColony()
        {
            return getColony().getClientBuildingManager().getBuildingExtensions(field -> field.getBuildingExtensionType().equals(BuildingExtensionRegistries.farmField.get()));
        }

        @Override
        public @Nullable String getFieldWarningTooltip(final IBuildingExtension field)
        {
            String result = super.getFieldWarningTooltip(field);
            if (result != null)
            {
                return result;
            }

            if (field instanceof FarmField farmField && farmField.getSeed().isEmpty())
            {
                return String.translatable(FIELD_LIST_FARMER_NO_SEED);
            }

            return null;
        }
    }

    public static class CraftingModule extends AbstractCraftingBuildingModule.Crafting
    {
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
            return CraftingUtils.getIngredientValidatorBasedOnTags(CRAFTING_FARMER)
                     .combine(super.getIngredientValidator());
        }

        @Override
        public boolean isRecipeCompatible(@NotNull final IGenericRecipe recipe)
        {
            if (!super.isRecipeCompatible(recipe))
            {
                return false;
            }
            return CraftingUtils.isRecipeCompatibleBasedOnTags(recipe, CRAFTING_FARMER).orElse(false);
        }

        @NotNull
        @Override
        public List<IGenericRecipe> getAdditionalRecipesForDisplayPurposesOnly(@NotNull World world)
        {
            List<IGenericRecipe> recipes = new ArrayList<>(super.getAdditionalRecipesForDisplayPurposesOnly(world));
            for (final ItemStack stack : IColonyManager.getInstance().getCompatibilityManager().getListOfAllItems())
            {
                if (stack.getItem() instanceof ItemCrop cropItem && cropItem.getBlock() instanceof MinecoloniesCropBlock crop)
                {
                    // MineColonies crop
                    // [1.7.10] TagKey<Biome> biome restrictions not available; restrictions omitted
                    recipes.add(GenericRecipe.builder()
                            .withInputs(List.of(List.of(new ItemStack(cropItem))))
                            .withIntermediate(crop.getPreferredFarmland())
                            .withLootTable(crop.getLootTable())
                            .withRequiredTool(ModEquipmentTypes.hoe.get())
                            .build());
                }
                else if (stack.getItem() instanceof ItemBlock item && item.getBlock() instanceof BlockCrops crop)
                {
                    // regular crop
                    recipes.add(GenericRecipe.builder()
                            .withInputs(List.of(List.of(new ItemStack(crop, 1, 0))))
                            .withIntermediate(Blocks.farmland)
                            .withRequiredTool(ModEquipmentTypes.hoe.get())
                            .build());
                }
                else if (stack.getItem() instanceof ItemBlock item && item.getBlock() instanceof BlockStem stem)
                {
                    // pumpkin/melon seed -> stem
                    recipes.add(GenericRecipe.builder()
                            .withInputs(List.of(List.of(stack)))
                            .withIntermediate(Blocks.farmland)
                            .withRequiredTool(ModEquipmentTypes.hoe.get())
                            .build());
                }
                else if (ItemStackUtils.compareItemStacksIgnoreStackSize(stack, new ItemStack(Items.wheat_seeds), false, false)
                        || ItemStackUtils.compareItemStacksIgnoreStackSize(stack, new ItemStack(Items.pumpkin_seeds), false, false)
                        || ItemStackUtils.compareItemStacksIgnoreStackSize(stack, new ItemStack(Items.melon_seeds), false, false))
                {
                    // [1.7.10] Tags.Items.SEEDS replaced by specific checks
                    recipes.add(GenericRecipe.builder()
                            .withInputs(List.of(List.of(stack)))
                            .withIntermediate(Blocks.farmland)
                            .withRequiredTool(ModEquipmentTypes.hoe.get())
                            .build());
                }
            }
            return recipes;
        }

        // [1.7.10] provideBiomeList removed - TagKey<Biome>/Registry<Biome> not available

        @NotNull
        @Override
        public List<ResourceLocation> getAdditionalLootTables()
        {
            final List<ResourceLocation> tables = new ArrayList<>(super.getAdditionalLootTables());
            for (final ItemStack stack : IColonyManager.getInstance().getCompatibilityManager().getListOfAllItems())
            {
                if (stack.getItem() instanceof ItemCrop cropItem && cropItem.getBlock() instanceof MinecoloniesCropBlock crop)
                {
                    tables.add(crop.getLootTable());
                }
                else if (stack.getItem() instanceof BlockItem item && item.getBlock() instanceof CropBlock crop)
                {
                    tables.add(crop.getLootTable());
                }
            }
            return tables;
        }
    }
}






