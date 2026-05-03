package com.minecolonies.core.colony.buildings.workerbuildings;

import com.minecolonies.api.colony.IColony;
import com.minecolonies.api.util.BlockPosUtil;
import com.minecolonies.api.util.constant.NbtTagConstants;
import com.minecolonies.core.colony.buildings.AbstractBuilding;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
// [1.7.10] int[] -> int x,y,z
import net.minecraft.nbt.NBTBase;
import net.minecraft.world.World;
import net.minecraft.block.Block;
import net.minecraft.world.level.block.WoolCarpetBlock;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.minecolonies.api.util.constant.NbtTagConstants.TAG_POS;

/**
 * Creates a new building for the school.
 */
public class BuildingSchool extends AbstractBuilding
{
    /**
     * Description of the job executed in the hut.
     */
    private static final String SCHOOL = "school";

    /**
     * Max building World of the hut.
     */
    private static final int MAX_BUILDING_LEVEL = 5;

    /**
     * NBT value to store the carpet pos.
     */
    private static final String TAG_CARPET = "carpet";

    /**
     * List of carpets to sit on.
     */
    @NotNull
    private final List<int[]> carpet = new ArrayList<>();

    /**
     * Random obj for random calc.
     */
    private final Random random = new Random();

    /**
     * Instantiates the building.
     *
     * @param c the colony.
     * @param l the location.
     */
    public BuildingSchool(final IColony c, final int[] l)
    {
        super(c, l);
    }

    @NotNull
    @Override
    public String getSchematicName()
    {
        return SCHOOL;
    }

    @Override
    public int getMaxBuildingLevel()
    {
        return MAX_BUILDING_LEVEL;
    }

    @Override
    public void registerBlockPosition(@NotNull final Block block, @NotNull final int[] pos, @NotNull final World world)
    {
        super.registerBlockPosition(block, pos, world);
        if (block instanceof WoolCarpetBlock)
        {
            carpet.add(pos);
        }
    }

    @Override
    public void deserializeNBT(final NBTTagCompound compound)
    {
        super.deserializeNBT(compound);
        final NBTTagList carpetTagList = compound.getList(TAG_CARPET, NBTBase.TAG_COMPOUND);
        for (int i = 0; i < carpetTagList.size(); ++i)
        {
            final NBTTagCompound bedCompound = carpetTagList.getCompound(i);
            final int[] pos = BlockPosUtil.read(bedCompound, TAG_POS);
            if (!carpet.contains(pos))
            {
                carpet.add(pos);
            }
        }
    }

    @Override
    public NBTTagCompound serializeNBT()
    {
        final NBTTagCompound compound = super.serializeNBT();
        if (!carpet.isEmpty())
        {
            @NotNull final NBTTagList carpetTagList = new NBTTagList();
            for (@NotNull final int[] pos : carpet)
            {
                final NBTTagCompound carpetCompound = new NBTTagCompound();
                BlockPosUtil.write(carpetCompound, NbtTagConstants.TAG_POS, pos);
                carpetTagList.add(carpetCompound);
            }
            compound.put(TAG_CARPET, carpetTagList);
        }

        return compound;
    }

    /**
     * Get a random place to sit from the school.
     *
     * @return the place to sit.
     */
    @Nullable
    public int[] getRandomPlaceToSit()
    {
        if (carpet.isEmpty())
        {
            return null;
        }
        final int[] returnPos = carpet.get(random.nextInt(carpet.size()));
        if (colony.getWorld().getBlockState(returnPos).getBlock() instanceof WoolCarpetBlock)
        {
            return returnPos;
        }
        carpet.remove(returnPos);
        return null;
    }
}




