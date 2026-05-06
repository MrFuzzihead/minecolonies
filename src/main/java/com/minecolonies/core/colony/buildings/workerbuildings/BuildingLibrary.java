package com.minecolonies.core.colony.buildings.workerbuildings;

import com.minecolonies.api.colony.IColony;
import com.minecolonies.api.util.MathUtils;
import com.minecolonies.core.colony.buildings.AbstractBuilding;
import com.minecolonies.core.datalistener.StudyItemListener;
// [1.7.10] int[] -> int x,y,z
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
// [1.7.10] NbtUtils removed
import net.minecraft.nbt.NBTBase;
import com.minecolonies.api.util.Tuple;
import net.minecraft.world.World;
import net.minecraft.block.Block;
import net.minecraftforge.common.Tags;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static com.minecolonies.api.util.constant.NbtTagConstants.TAG_BOOKCASES;
import static com.minecolonies.api.util.constant.NbtTagConstants.TAG_POS;

/**
 * Creates a new building for the Library.
 */
public class BuildingLibrary extends AbstractBuilding
{
    /**
     * Description of the block used to set this block.
     */
    private static final String LIBRARY_HUT_NAME = "library";

    /**
     * Max building World of the hut.
     */
    private static final int MAX_BUILDING_LEVEL = 5;

    /**
     * List of registered barrels.
     */
    private final List<int[]> bookCases = new ArrayList<>();

    /**
     * Instantiates the building.
     *
     * @param c the colony.
     * @param l the location.
     */
    public BuildingLibrary(final IColony c, final int[] l)
    {
        super(c, l);
        keepX.put(StudyItemListener::isStudyItem, new Tuple<>(64, true));
    }

    @NotNull
    @Override
    public String getSchematicName()
    {
        return LIBRARY_HUT_NAME;
    }

    @Override
    public void deserializeNBT(final NBTTagCompound compound)
    {
        super.deserializeNBT(compound);
        final NBTTagList furnaceTagList = compound.getTagList(TAG_BOOKCASES, NBTBase.TAG_COMPOUND);
        for (int i = 0; i < furnaceTagList.size(); ++i)
        {
            bookCases.add(NbtUtils.readBlockPos(furnaceTagList.getCompoundTagAt(i).getCompoundTag(TAG_POS)));
        }
    }

    @Override
    public NBTTagCompound serializeNBT()
    {
        final NBTTagCompound compound = super.serializeNBT();
        @NotNull final NBTTagList bookcaseTagList = new NBTTagList();
        for (@NotNull final int[] entry : bookCases)
        {
            @NotNull final NBTTagCompound bookCompound = new NBTTagCompound();
            bookcompound.setTag(TAG_POS, NbtUtils.writeBlockPos(entry));
            bookcaseTagList.add(bookCompound);
        }
        compound.setTag(TAG_BOOKCASES, bookcaseTagList);

        return compound;
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
        if (block.defaultBlockState().is(Tags.Blocks.BOOKSHELVES))
        {
            bookCases.add(pos);
        }
    }

    /**
     * Returns a random bookshelf from the list.
     *
     * @return the position of it.
     */
    public int[] getRandomBookShelf()
    {
        if (bookCases.isEmpty())
        {
            return getPosition();
        }
        final int[] returnPos = bookCases.get(MathUtils.RANDOM.nextInt(bookCases.size()));
        if (colony.getWorld().getBlockState(returnPos).is(Tags.Blocks.BOOKSHELVES))
        {
            return returnPos;
        }
        bookCases.remove(returnPos);
        return getPosition();
    }
}




