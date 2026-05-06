package com.minecolonies.core.colony.buildings.workerbuildings;

import com.minecolonies.api.colony.ICitizenData;
import com.minecolonies.api.colony.IColony;
import com.minecolonies.api.colony.jobs.ModJobs;
import com.minecolonies.api.research.IGlobalResearchTree;
import com.minecolonies.api.research.ILocalResearch;
import com.minecolonies.api.util.MathUtils;
import com.minecolonies.api.util.MessageUtils;
import com.minecolonies.api.util.StatsUtil;
import com.minecolonies.core.colony.buildings.AbstractBuilding;
import com.minecolonies.core.colony.buildings.modules.WorkerBuildingModule;
// [1.7.10] int[] -> int x,y,z
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
// [1.7.10] NbtUtils removed
import net.minecraft.nbt.NBTBase;
import net.minecraft.util.IChatComponent;
// [1.7.10] chat.String replaced by IChatComponent/ChatComponentText
import net.minecraft.world.World;
import net.minecraft.block.Block;
// [1.7.10] Object /* Tags */ removed
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static com.minecolonies.api.util.constant.NbtTagConstants.TAG_BOOKCASES;
import static com.minecolonies.api.util.constant.NbtTagConstants.TAG_POS;
import static com.minecolonies.api.util.constant.TranslationConstants.MESSAGE_RESEARCHERS_MORE_KNOWLEDGE;
import static com.minecolonies.api.util.constant.TranslationConstants.RESEARCH_CONCLUDED;
import static com.minecolonies.api.util.constant.StatisticsConstants.RESEARCH_COMPLETED;

/**
 * Creates a new building for the university.
 */
public class BuildingUniversity extends AbstractBuilding
{
    /**
     * Description of the job executed in the hut.
     */
    private static final String UNIVERSITY = "university";

    /**
     * Offline processing World cap.
     */
    private static final int OFFLINE_PROCESSING_LEVEL_CAP = 3;

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
    public BuildingUniversity(final IColony c, final int[] l)
    {
        super(c, l);
    }

    @NotNull
    @Override
    public String getSchematicName()
    {
        return UNIVERSITY;
    }

    @Override
    public void deserializeNBT(final NBTTagCompound compound)
    {
        super.deserializeNBT(compound);
        final NBTTagList furnaceTagList = compound.getTagList(TAG_BOOKCASES, 10); // 10 = TAG_Compound
        for (int i = 0; i < furnaceTagList.tagCount(); ++i)
        {
            bookCases.add(com.minecolonies.api.util.BlockPosUtil.read(furnaceTagList.getCompoundTagAt(i), TAG_POS));
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
            com.minecolonies.api.util.BlockPosUtil.write(bookCompound, TAG_POS, entry);
            bookcaseTagList.appendTag(bookCompound);
        }
        compound.setTag(TAG_BOOKCASES, bookcaseTagList);

        return compound;
    }

    @Override
    public void registerBlockPosition(@NotNull final Block block, @NotNull final int[] pos, @NotNull final World world)
    {
        super.registerBlockPosition(block, pos, world);
        if (block == net.minecraft.init.Blocks.bookshelf)
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
        if (colony.getWorld().getBlock(returnPos[0], returnPos[1], returnPos[2]) == net.minecraft.init.Blocks.bookshelf)
        {
            return returnPos;
        }
        bookCases.remove(returnPos);
        return getPosition();
    }

    @Override
    public void onColonyTick(@NotNull final IColony colony)
    {
        super.onColonyTick(colony);

        final List<ILocalResearch> inProgress = colony.getResearchManager().getResearchTree().getResearchInProgress();
        final WorkerBuildingModule module = getModuleMatching(WorkerBuildingModule.class, m -> m.getJobEntry() == ModJobs.researcher);

        int i = 1;
        for (final ILocalResearch research : inProgress)
        {
            if (i > module.getAssignedCitizen().size())
            {
                return;
            }

            if (colony.getResearchManager()
                  .getResearchTree()
                  .getResearch(research.getBranch(), research.getId())
                  .research(colony.getResearchManager().getResearchEffects(), colony.getResearchManager().getResearchTree()))
            {
                onSuccess(research);
            }
            colony.getResearchManager().markDirty();
            i++;
        }
    }

    /**
     * Called on successfully concluding a research.
     *
     * @param research the concluded research.
     */
    public void onSuccess(final ILocalResearch research)
    {
        for (final ICitizenData citizen : colony.getCitizenManager().getCitizens())
        {
            citizen.applyResearchEffects();
        }

        StatsUtil.trackStat(this, RESEARCH_COMPLETED, 1);

        final String researchName = IGlobalResearchTree.getInstance().getResearch(research.getBranch(), research.getId()).getName();
        final String message = RESEARCH_CONCLUDED + ThreadLocalRandom.current().nextInt(3);

        MessageUtils.format(message, researchName).sendTo(colony).forManagers();
        colony.getResearchManager().checkAutoStartResearch();
        this.markDirty();
    }

    @Override
    public void processOfflineTime(final long time)
    {
        if (getBuildingLevel() >= OFFLINE_PROCESSING_LEVEL_CAP && time > 0)
        {
            MessageUtils.format(MESSAGE_RESEARCHERS_MORE_KNOWLEDGE).sendTo(colony).forAllPlayers();
            for (final ICitizenData citizenData : getAllAssignedCitizen())
            {
                if (citizenData.getJob() != null)
                {
                    citizenData.getJob().processOfflineTime(time);
                }
            }
        }
    }
}






