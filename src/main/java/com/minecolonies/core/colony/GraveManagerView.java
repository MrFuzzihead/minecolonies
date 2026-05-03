package com.minecolonies.core.colony;

import com.google.common.collect.ImmutableMap;
import com.minecolonies.api.colony.ICitizenData;
import com.minecolonies.api.colony.IColony;
import com.minecolonies.api.colony.managers.interfaces.IGraveManager;
import com.minecolonies.api.util.BlockPosUtil;
// [1.7.10] int[] -> int x,y,z
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTBase;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

import static com.minecolonies.api.util.constant.NbtTagConstants.*;

/**
 * Client-side read-only copy of the {@link com.minecolonies.core.colony.managers.GraveManager}.
 */
public class GraveManagerView implements IGraveManager
{
    private Map<int[], Boolean> graves = ImmutableMap.of();

    /**
     * This needs to read what {@link com.minecolonies.core.colony.managers.GraveManager#write} wrote.
     *
     * @param compound the compound.
     */
    @Override
    public void read(@NotNull NBTTagCompound compound)
    {
        final ImmutableMap.Builder<int[], Boolean> graves = ImmutableMap.builder();

        final NBTTagList gravesTagList = compound.getList(TAG_GRAVE, NBTBase.TAG_COMPOUND);
        for (int i = 0; i < gravesTagList.size(); ++i)
        {
            final NBTTagCompound graveCompound = gravesTagList.getCompound(i);
            if (graveCompound.contains(TAG_POS) && graveCompound.contains(TAG_RESERVED))
            {
                graves.put(BlockPosUtil.read(graveCompound, TAG_POS), graveCompound.getBoolean(TAG_RESERVED));
            }
        }

        this.graves = graves.build();
    }

    @Override
    public void write(@NotNull NBTTagCompound compound)
    {
    }

    @Override
    public void onColonyTick(IColony colony)
    {
    }

    @Override
    public boolean reserveGrave(int[] pos)
    {
        return false;
    }

    @Override
    public void unReserveGrave(int[] pos)
    {
    }

    @Override
    public int[] reserveNextFreeGrave()
    {
        return null;
    }

    @Override
    public int[] createCitizenGrave(World world, int[] pos, ICitizenData citizenData)
    {
        return null;
    }

    @NotNull
    @Override
    public Map<int[], Boolean> getGraves()
    {
        return this.graves;
    }

    @Override
    public boolean addNewGrave(@NotNull int[] pos)
    {
        return false;
    }

    @Override
    public void removeGrave(@NotNull int[] pos)
    {
    }
}




