package com.minecolonies.core.colony.buildings.modules;
import net.minecraft.util.Direction;
// [1.7.10] removed: import net.minecraft.core.Direction; (use net.minecraft.util.Direction)
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.BoneMealItem;

import com.minecolonies.api.colony.buildings.modules.*;
import com.minecolonies.api.util.WorldUtil;
import net.minecraft.nbt.NBTBase;
// [1.7.10] BedBlock -> Blocks.bed; BedPart not available (check meta)
import net.minecraft.init.Blocks;
// [1.7.10] BlockState -> int metadata
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
// [1.7.10] NbtUtils removed
// [1.7.10] BedPart -> check block metadata; no BedPart enum
// [1.7.10] int[] -> int x,y,z
import net.minecraft.world.World;

import org.jetbrains.annotations.NotNull;

import java.util.*;

import static com.minecolonies.api.util.constant.NbtTagConstants.TAG_BEDS;

/**
 * The class of the citizen hut.
 */
public class BedHandlingModule extends AbstractBuildingModule implements IModuleWithExternalBlocks, IPersistentModule, IBuildingEventsModule
{
    /**
     * List of all beds.
     */
    @NotNull
    private final Set<int[]> bedList = new HashSet<>();

    @Override
    public void deserializeNBT(final NBTTagCompound compound)
    {
        final NBTTagList bedTagList = compound.getTagList(TAG_BEDS, NBTBase.TAG_COMPOUND);
        for (int i = 0; i < bedTagList.size(); ++i)
        {
            final NBTTagCompound bedCompound = bedTagList.getCompoundTagAt(i);
            final int[] bedPos = NbtUtils.readBlockPos(bedCompound);
            bedList.add(bedPos);
        }
    }

    @Override
    public void serializeNBT(final NBTTagCompound compound)
    {
        if (!bedList.isEmpty())
        {
            @NotNull final NBTTagList bedTagList = new NBTTagList();
            for (@NotNull final int[] pos : bedList)
            {
                bedTagList.add(NbtUtils.writeBlockPos(pos));
            }
            compound.setTag(TAG_BEDS, bedTagList);
        }
    }

    @Override
    public void onBlockPlacedInBuilding(@NotNull final BlockState blockState, @NotNull final int[] pos, @NotNull final World world)
    {
        int[] registrationPosition = pos;
        if (blockState.getBlock() instanceof BedBlock)
        {
            if (blockState.getValue(BedBlock.PART) == BedPart.FOOT)
            {
                registrationPosition = registrationPosition.relative(blockState.getValue(BedBlock.FACING));
            }

            bedList.add(registrationPosition);
        }
    }

    @Override
    public List<int[]> getRegisteredBlocks()
    {
        return new ArrayList<>(bedList);
    }

    @Override
    public void onWakeUp()
    {
        final World world = building.getColony().getWorld();
        if (world == null)
        {
            return;
        }

        for (final int[] pos : bedList)
        {
            if (WorldUtil.isBlockLoaded(world, pos))
            {
                final BlockState state = world.getBlockState(pos);
                if (state.getBlock() instanceof BedBlock
                      && state.getValue(BedBlock.OCCUPIED)
                      && state.getValue(BedBlock.PART).equals(BedPart.HEAD))
                {
                    world.setBlock(pos, state.setValue(BedBlock.OCCUPIED, false), 0x03);
                }
            }
        }
    }

    /**
     * Remove a bed from a pos.
     * @param pos the pos to remove.
     */
    public void removeBed(final int[] pos)
    {
        bedList.remove(pos);
    }
}




