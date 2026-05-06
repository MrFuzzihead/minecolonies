package com.minecolonies.core.colony.buildings.modules;
import net.minecraft.util.Direction;
// [1.7.10] removed: import net.minecraft.core.Direction; (use net.minecraft.util.Direction)
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.BoneMealItem;

import com.minecolonies.api.colony.buildings.modules.AbstractBuildingModule;
import com.minecolonies.api.colony.buildings.modules.IAltersRequiredItems;
import com.minecolonies.api.colony.buildings.modules.IModuleWithExternalBlocks;
import com.minecolonies.api.colony.buildings.modules.IPersistentModule;
import com.minecolonies.api.crafting.ItemStorage;
import com.minecolonies.api.util.ItemStackUtils;
// [1.7.10] int[] -> int x,y,z
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
// [1.7.10] NbtUtils removed
import net.minecraft.nbt.NBTBase;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
// [1.7.10] FurnaceBlock -> Blocks.furnace; check block directly
import net.minecraft.init.Blocks;
// [1.7.10] BlockState -> int metadata

import org.apache.logging.log4j.util.TriConsumer;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import static com.minecolonies.api.util.constant.BuildingConstants.FUEL_LIST;
import static com.minecolonies.api.util.constant.Constants.STACKSIZE;

/**
 * Module for all workers that need a furnace.
 */
public class FurnaceUserModule extends AbstractBuildingModule implements IPersistentModule, IModuleWithExternalBlocks, IAltersRequiredItems
{
    /**
     * NBTBase to store the furnace position.
     */
    private static final String TAG_POS = "pos";

    /**
     * NBTBase to store the furnace position in compatibility (Baker)
     */
    private static final String TAG_POS_COMPAT = "furnacePos";

    /**
     * NBTBase to store the furnace list.
     */
    private static final String TAG_FURNACES = "furnaces";

    /**
     * List of registered furnaces.
     */
    private final List<int[]> furnaces = new ArrayList<>();

    /**
     * Construct a new furnace user module.
     */
    public FurnaceUserModule()
    {
        super();
    }

    @Override
    public void deserializeNBT(final NBTTagCompound compound)
    {
        final NBTTagList furnaceTagList = compound.getTagList(TAG_FURNACES, NBTBase.TAG_COMPOUND);
        for (int i = 0; i < furnaceTagList.size(); ++i)
        {
            if(furnaceTagList.getCompoundTagAt(i).contains(TAG_POS))
            {
                furnaces.add(NbtUtils.readBlockPos(furnaceTagList.getCompoundTagAt(i).getCompoundTag(TAG_POS)));
            }
            if(furnaceTagList.getCompoundTagAt(i).contains(TAG_POS_COMPAT))
            {
                furnaces.add(NbtUtils.readBlockPos(furnaceTagList.getCompoundTagAt(i).getCompoundTag(TAG_POS_COMPAT)));
            }
        }
    }

    @Override
    public void serializeNBT(final NBTTagCompound compound)
    {
        @NotNull final NBTTagList furnacesTagList = new NBTTagList();
        for (@NotNull final int[] entry : furnaces)
        {
            @NotNull final NBTTagCompound furnaceCompound = new NBTTagCompound();
            furnacecompound.setTag(TAG_POS, NbtUtils.writeBlockPos(entry));
            furnacesTagList.add(furnaceCompound);
        }
        compound.setTag(TAG_FURNACES, furnacesTagList);
    }

    @Override
    public void alterItemsToBeKept(final TriConsumer<Predicate<ItemStack>, Integer, Boolean> consumer)
    {
        consumer.accept(this::isAllowedFuel, STACKSIZE * building.getBuildingLevel(), false);
    }

    /**
     * Remove a furnace from the building.
     *
     * @param pos the position of it.
     */
    public void removeFromFurnaces(final int[] pos)
    {
        furnaces.remove(pos);
    }

    /**
     * Check if an ItemStack is one of the accepted fuel items.
     *
     * @param stack the itemStack to check.
     * @return true if so.
     */
    public boolean isAllowedFuel(final ItemStack stack)
    {
        if (ItemStackUtils.isEmpty(stack))
        {
            return false;
        }
        return building.getModuleMatching(ItemListModule.class, m -> m.getId().equals(FUEL_LIST)).isItemInList(new ItemStorage(stack));
    }

    /**
     * Return a list of furnaces assigned to this hut.
     *
     * @return copy of the list
     */
    public List<int[]> getFurnaces()
    {
        return new ArrayList<>(furnaces);
    }

    @Override
    public void onBlockPlacedInBuilding(@NotNull final BlockState blockState, @NotNull final int[] pos, @NotNull final World world)
    {
        if (blockState.getBlock() instanceof FurnaceBlock && !furnaces.contains(pos))
        {
            furnaces.add(pos);
        }
    }

    @Override
    public List<int[]> getRegisteredBlocks()
    {
        return new ArrayList<>(furnaces);
    }
}




