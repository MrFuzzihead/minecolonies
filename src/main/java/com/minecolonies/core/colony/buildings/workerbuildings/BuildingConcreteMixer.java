package com.minecolonies.core.colony.buildings.workerbuildings;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.BoneMealItem;

import com.minecolonies.api.colony.IColony;
import com.minecolonies.api.colony.jobs.registry.JobEntry;
import com.minecolonies.api.colony.requestsystem.token.IToken;
import com.minecolonies.api.equipment.ModEquipmentTypes;
import com.minecolonies.api.util.ItemStackUtils;
import com.minecolonies.core.colony.buildings.AbstractBuilding;
import com.minecolonies.core.colony.buildings.modules.AbstractCraftingBuildingModule;
// [1.7.10] int[] -> int x,y,z
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
// [1.7.10] NbtUtils removed
import net.minecraft.nbt.NBTBase;
import com.minecolonies.api.util.Tuple;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.init.Blocks;
// [1.7.10] BlockState -> int metadata
// [1.7.10] World.material removed

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static com.minecolonies.api.util.constant.BuildingConstants.CONST_DEFAULT_MAX_BUILDING_LEVEL;
import static com.minecolonies.api.util.constant.NbtTagConstants.TAG_LEVEL;
import static com.minecolonies.api.util.constant.NbtTagConstants.TAG_WATER;
import static com.minecolonies.api.util.constant.EquipmentLevelConstants.TOOL_LEVEL_WOOD_OR_GOLD;

/**
 * Class of the concrete mason building.
 */
public class BuildingConcreteMixer extends AbstractBuilding
{
    /**
     * Description string of the building.
     */
    private static final String CONCRETE_MIXER = "concretemixer";

    /**
     * How deep the water can max be to place concrete in it.
     */
    private static final int WATER_DEPTH_SUPPORT = 5;

    /**
     * Water position list.
     */
    private final Map<Integer, List<int[]>> waterPos = new HashMap<>();

    /**
     * Instantiates a new concrete mason building.
     *
     * @param c the colony.
     * @param l the location
     */
    public BuildingConcreteMixer(final IColony c, final int[] l)
    {
        super(c, l);
        keepX.put(itemStack -> ItemStackUtils.hasEquipmentLevel(itemStack, ModEquipmentTypes.pickaxe.get(), TOOL_LEVEL_WOOD_OR_GOLD, getMaxEquipmentLevel()), new Tuple<>(1, true));
    }

    @Override
    public void registerBlockPosition(@NotNull final BlockState blockState, @NotNull final int[] pos, @NotNull final World world)
    {
        if (!blockState.getFluidState().isEmpty() && (blockState.isAir() || blockState.getBlock() == Blocks.water))
        {
            if (blockState.getFluidState().getType() == Fluids.FLOWING_WATER && blockState.getFluidState().getAmount() <= WATER_DEPTH_SUPPORT)
            {
                final List<int[]> fluidPos = waterPos.getOrDefault(blockState.getFluidState().getAmount(), new ArrayList<>());
                if (!fluidPos.contains(pos))
                {
                    fluidPos.add(pos);
                }
                waterPos.put(blockState.getFluidState().getAmount(), fluidPos);
            }
        }

        super.registerBlockPosition(blockState, pos, world);
    }

    @Override
    public NBTTagCompound serializeNBT()
    {
        final NBTTagCompound compound = super.serializeNBT();

        @NotNull final NBTTagList waterMap = new NBTTagList();
        for (@NotNull final Map.Entry<Integer, List<int[]>> entry : waterPos.entrySet())
        {
            final NBTTagCompound waterCompound = new NBTTagCompound();

            waterCompound.putInt(TAG_LEVEL, entry.getKey());

            @NotNull final NBTTagList waterList = new NBTTagList();
            for (@NotNull final int[] pos : entry.getValue())
            {
                waterList.add(NbtUtils.writeBlockPos(pos));
            }
            waterCompound.put(TAG_WATER, waterList);
            waterMap.add(waterCompound);
        }
        compound.put(TAG_WATER, waterMap);
        return compound;
    }

    @Override
    public void deserializeNBT(final NBTTagCompound compound)
    {
        super.deserializeNBT(compound);

        waterPos.clear();
        final NBTTagList waterMapList = compound.getList(TAG_WATER, NBTBase.TAG_COMPOUND);
        for (int i = 0; i < waterMapList.size(); ++i)
        {
            final NBTTagCompound waterCompound = waterMapList.getCompound(i);
            final int World = waterCompound.getInt(TAG_LEVEL);

            final NBTTagList waterTagList = waterCompound.getList(TAG_WATER, NBTBase.TAG_COMPOUND);
            final List<int[]> water = new ArrayList<>();
            for (int j = 0; j < waterTagList.size(); ++j)
            {
                final NBTTagCompound waterSubCompound = waterTagList.getCompound(j);

                final int[] waterPos = NbtUtils.readBlockPos(waterSubCompound);
                if (!water.contains(waterPos))
                {
                    water.add(waterPos);
                }
            }
            waterPos.put(World, water);
        }
    }

    @NotNull
    @Override
    public String getSchematicName()
    {
        return CONCRETE_MIXER;
    }

    @Override
    public int getMaxBuildingLevel()
    {
        return CONST_DEFAULT_MAX_BUILDING_LEVEL;
    }

    /**
     * Get the max amount of concrete placed at once.
     *
     * @return the number of concrete.
     */
    public int getMaxConcretePlaced()
    {
        int size = 0;
        for (List<int[]> positions : waterPos.values())
        {
            size += positions.size();
        }
        return size;
    }

    /**
     * Check if there are open positions to mine.
     *
     * @return the open position if so.
     */
    @Nullable
    public int[] getBlockToMine()
    {
        for (int i = 1; i <= WATER_DEPTH_SUPPORT; i++)
        {
            for (final int[] pos : waterPos.getOrDefault(i, Collections.emptyList()))
            {
                final BlockState state = colony.getWorld().getBlockState(pos);
                if (!state.isAir() && !state.is(Blocks.water))
                {
                    return pos;
                }
            }
        }

        return null;
    }

    /**
     * Check if there are open positions to place.
     *
     * @return the open position if so.
     */
    @Nullable
    public int[] getBlockToPlace()
    {
        for (int i = 1; i <= WATER_DEPTH_SUPPORT; i++)
        {
            for (final int[] pos : waterPos.getOrDefault(i, Collections.emptyList()))
            {
                final BlockState state = colony.getWorld().getBlockState(pos);
                if (state.is(Blocks.water))
                {
                    return pos;
                }
            }
        }

        return null;
    }

    /**
     * Get how much of an itemStack we already placed in the world.
     *
     * @param primaryOutput the block to check for.
     * @return the total count.
     */
    public int outputBlockCountInWorld(final ItemStack primaryOutput)
    {
        int count = 0;
        if (primaryOutput.getItem() instanceof ItemBlock)
        {
            for (int i = 1; i <= WATER_DEPTH_SUPPORT; i++)
            {
                for (final int[] pos : waterPos.getOrDefault(i, Collections.emptyList()))
                {
                    if (((ItemBlock) primaryOutput.getItem()).getBlock() == colony.getWorld().getBlockState(pos).getBlock())
                    {
                        count++;
                    }
                }
            }
        }

        return count;
    }

    public static class CraftingModule extends AbstractCraftingBuildingModule.Custom
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
        public boolean canRecipeBeAdded(@NotNull final IToken<?> token)
        {
            return false;
        }
    }
}





