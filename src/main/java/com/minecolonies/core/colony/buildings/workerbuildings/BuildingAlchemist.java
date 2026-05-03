package com.minecolonies.core.colony.buildings.workerbuildings;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.BoneMealItem;

import com.minecolonies.api.colony.IColony;
import com.minecolonies.api.colony.jobs.registry.JobEntry;
import com.minecolonies.api.crafting.GenericRecipe;
import com.minecolonies.api.crafting.IGenericRecipe;
import com.minecolonies.api.crafting.registry.CraftingType;
import com.minecolonies.api.equipment.ModEquipmentTypes;
import com.minecolonies.api.items.ModItems;
import com.minecolonies.api.util.ItemStackUtils;
import com.minecolonies.core.colony.buildings.AbstractBuilding;
import com.minecolonies.core.colony.buildings.modules.AbstractCraftingBuildingModule;
// [1.7.10] int[] -> int x,y,z
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
// [1.7.10] NbtUtils removed
// [1.7.10] tags removed
import com.minecolonies.api.util.Tuple;
import net.minecraft.init.Items;
import net.minecraft.world.World;
import net.minecraft.init.Blocks;
// [1.7.10] BlockState -> int metadata
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static com.minecolonies.api.util.constant.BuildingConstants.CONST_DEFAULT_MAX_BUILDING_LEVEL;
import static com.minecolonies.api.util.constant.EquipmentLevelConstants.TOOL_LEVEL_WOOD_OR_GOLD;
import static com.minecolonies.api.util.constant.NbtTagConstants.*;

/**
 * Class of the alchemist building. Crafts potions and grows netherwart.
 */
public class BuildingAlchemist extends AbstractBuilding
{
    /**
     * Description string of the building.
     */
    private static final String ALCHEMIST = "alchemist";

    /**
     * List of soul sand blocks to grow onto.
     */
    private final List<int[]> soulsand = new ArrayList<>();

    /**
     * List of leave blocks to gather mistletoes from.
     */
    private final List<int[]> leaves = new ArrayList<>();

    /**
     * List of brewing stands.
     */
    private final List<int[]> brewingStands = new ArrayList<>();

    /**
     * Instantiates a new plantation building.
     *
     * @param c the colony.
     * @param l the location
     */
    public BuildingAlchemist(final IColony c, final int[] l)
    {
        super(c, l);
        keepX.put(itemStack -> ItemStackUtils.hasEquipmentLevel(itemStack, ModEquipmentTypes.shears.get(), TOOL_LEVEL_WOOD_OR_GOLD, getMaxEquipmentLevel()), new Tuple<>(1, true));
        keepX.put(itemStack ->  itemStack.getItem() == Items.nether_wart, new Tuple<>(16, false));
        keepX.put(itemStack -> ItemStackUtils.hasEquipmentLevel(itemStack, ModEquipmentTypes.axe.get(), TOOL_LEVEL_WOOD_OR_GOLD, getMaxEquipmentLevel()), new Tuple<>(1, true));
    }

    @NotNull
    @Override
    public String getSchematicName()
    {
        return ALCHEMIST;
    }

    @Override
    public int getMaxBuildingLevel()
    {
        return CONST_DEFAULT_MAX_BUILDING_LEVEL;
    }

    @Override
    public void registerBlockPosition(@NotNull final BlockState block, @NotNull final int[] pos, @NotNull final World world)
    {
        super.registerBlockPosition(block, pos, world);
        if (block.getBlock() == Blocks.soul_sand)
        {
            soulsand.add(pos);
        }
        else if (block.getBlock() instanceof net.minecraft.block.BlockLeaves) // [1.7.10] BlockTags.LEAVES -> instanceof BlockLeaves
        {
            leaves.add(pos);
        }
        else if (block.getBlock() == Blocks.brewing_stand)
        {
            brewingStands.add(pos);
        }
    }

    @Override
    public void deserializeNBT(final NBTTagCompound compound)
    {
        super.deserializeNBT(compound);
        final NBTTagList sandPos = compound.getList(TAG_PLANTGROUND, NBTTagCompound.TAG_COMPOUND);
        for (int i = 0; i < sandPos.size(); ++i)
        {
            soulsand.add(NbtUtils.readBlockPos(sandPos.getCompound(i).getCompound(TAG_POS)));
        }

        final NBTTagList leavesPos = compound.getList(TAG_LEAVES, NBTTagCompound.TAG_COMPOUND);
        for (int i = 0; i < leavesPos.size(); ++i)
        {
            leaves.add(NbtUtils.readBlockPos(leavesPos.getCompound(i).getCompound(TAG_POS)));
        }

        final NBTTagList brewingStandPos = compound.getList(TAG_BREWING_STAND, NBTTagCompound.TAG_COMPOUND);
        for (int i = 0; i < brewingStandPos.size(); ++i)
        {
            brewingStands.add(NbtUtils.readBlockPos(brewingStandPos.getCompound(i).getCompound(TAG_POS)));
        }
    }

    @Override
    public NBTTagCompound serializeNBT()
    {
        final NBTTagCompound compound = super.serializeNBT();
        @NotNull final NBTTagList sandCompoundList = new NBTTagList();
        for (@NotNull final int[] entry : soulsand)
        {
            @NotNull final NBTTagCompound sandCompound = new NBTTagCompound();
            sandCompound.put(TAG_POS, NbtUtils.writeBlockPos(entry));
            sandCompoundList.add(sandCompound);
        }
        compound.put(TAG_PLANTGROUND, sandCompoundList);

        @NotNull final NBTTagList leavesCompoundList = new NBTTagList();
        for (@NotNull final int[] entry : leaves)
        {
            @NotNull final NBTTagCompound leaveCompound = new NBTTagCompound();
            leaveCompound.put(TAG_POS, NbtUtils.writeBlockPos(entry));
            leavesCompoundList.add(leaveCompound);
        }
        compound.put(TAG_LEAVES, leavesCompoundList);

        @NotNull final NBTTagList brewingStandCompoundList = new NBTTagList();
        for (@NotNull final int[] entry : brewingStands)
        {
            @NotNull final NBTTagCompound brewingStandCompound = new NBTTagCompound();
            brewingStandCompound.put(TAG_POS, NbtUtils.writeBlockPos(entry));
            brewingStandCompoundList.add(brewingStandCompound);
        }
        compound.put(TAG_BREWING_STAND, brewingStandCompoundList);

        return compound;
    }

    /**
     * Get a list of all the available working positions.
     *
     * @return copy of the list of positions.
     */
    public List<int[]> getAllSoilPositions()
    {
        return new ArrayList<>(soulsand);
    }

    /**
     * Get a list of all leave positions.
     *
     * @return copy of the list of positions.
     */
    public List<int[]> getAllLeavePositions()
    {
        return new ArrayList<>(leaves);
    }

    /**
     * Get a list of all brewing stand positions.
     *
     * @return copy of the list of positions.
     */
    public List<int[]> getAllBrewingStandPositions()
    {
        return new ArrayList<>(brewingStands);
    }

    /**
     * Remove a vanished brewing stand.
     * @param pos the position of it.
     */
    public void removeBrewingStand(final int[] pos)
    {
        brewingStands.remove(pos);
    }

    /**
     * Remove soil position.
     * @param pos the position of it.
     */
    public void removeSoilPosition(final int[] pos)
    {
        soulsand.remove(pos);
    }

    /**
     * Remove leaf position.
     * @param pos the position of it.
     */
    public void removeLeafPosition(final int[] pos)
    {
        leaves.remove(pos);
    }

    public static class BrewingModule extends AbstractCraftingBuildingModule.Brewing
    {
        /**
         * Create a new module.
         *
         * @param jobEntry the entry of the job.
         */
        public BrewingModule(final JobEntry jobEntry)
        {
            super(jobEntry);
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

        @Override
        public boolean isRecipeCompatible(@NotNull final IGenericRecipe recipe)
        {
            if (!super.isRecipeCompatible(recipe))
                return false;

            return recipe.getPrimaryOutput().getItem() == ModItems.magicpotion;
        }

        @Override
        public Set<CraftingType> getSupportedCraftingTypes()
        {
            return Collections.emptySet();
        }

        @Override
        public @NotNull List<IGenericRecipe> getAdditionalRecipesForDisplayPurposesOnly(@NotNull final World world)
        {
            final List<IGenericRecipe> recipes = new ArrayList<>(super.getAdditionalRecipesForDisplayPurposesOnly(world));

            // growing mistletoe
            recipes.add(GenericRecipe.builder()
                    .withOutput(ModItems.mistletoe)
                    .withIntermediate(Blocks.leaves)  // [1.7.10] Blocks.OAK_LEAVES -> Blocks.leaves
                    .withRequiredTool(ModEquipmentTypes.shears.get())
                    .build());

            // growing netherwart
            recipes.add(GenericRecipe.builder()
                    .withOutput(Items.nether_wart, 4)
                    .withInputs(List.of(List.of(new ItemStack(Items.nether_wart))))
                    .withIntermediate(Blocks.soul_sand)
                    .build());

            return recipes;
        }
    }
}







