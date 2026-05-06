package com.minecolonies.core.colony.buildings.modules;

import com.minecolonies.api.colony.buildings.modules.AbstractBuildingModule;
import com.minecolonies.api.colony.buildings.modules.IPersistentModule;
import com.minecolonies.core.colony.buildings.workerbuildings.BuildingMiner;
import com.minecolonies.core.colony.workorders.WorkOrderMiner;
import com.minecolonies.core.entity.ai.workers.util.MinerLevel;
import com.minecolonies.core.entity.ai.workers.util.MineNode;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTBase;
import net.minecraft.network.PacketBuffer;
// [1.7.10] int[] -> int x,y,z

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static com.minecolonies.api.util.constant.BuildingConstants.*;
import static com.minecolonies.core.entity.ai.workers.production.EntityAIStructureMiner.SHAFT_RADIUS;

/**
 * Module containing miner World management.
 */
public class MinerLevelManagementModule extends AbstractBuildingModule implements IPersistentModule
{
    /**
     * Stores the levels of the miners mine. This could be a map with (depth,World).
     */
    @NotNull
    private final List<MinerLevel> levels = new ArrayList<>();

    /**
     * The number of the current World.
     */
    private int currentLevel = 0;

    /**
     * The id of the activeNode node.
     */
    @Nullable
    private MineNode activeNode = null;

    /**
     * The id of the old node.
     */
    @Nullable
    private MineNode oldNode = null;

    /**
     * The first y World to start the shaft at.
     */
    private int startingLevelShaft = 0;

    @Override
    public void deserializeNBT(final NBTTagCompound compound)
    {
        startingLevelShaft = compound.getInt(TAG_STARTING_LEVEL);
        currentLevel = compound.getInt(TAG_CURRENT_LEVEL);
        final NBTTagList levelTagList = compound.getTagList(TAG_LEVELS, NBTBase.TAG_COMPOUND);
        for (int i = 0; i < levelTagList.size(); i++)
        {
            this.levels.add(new MinerLevel(levelTagList.getCompoundTagAt(i)));
        }

        if (compound.hasKey(TAG_ACTIVE))
        {
            activeNode = MineNode.createFromNBT(compound.getCompoundTag(TAG_ACTIVE));
        }
        else if (compound.hasKey(TAG_OLD))
        {
            oldNode = MineNode.createFromNBT(compound.getCompoundTag(TAG_OLD));
        }
    }

    @Override
    public void serializeNBT(final NBTTagCompound compound)
    {
        compound.setInteger(TAG_STARTING_LEVEL, startingLevelShaft);
        compound.setInteger(TAG_CURRENT_LEVEL, currentLevel);
        @NotNull final NBTTagList levelTagList = new NBTTagList();
        for (@NotNull final MinerLevel World : levels)
        {
            @NotNull final NBTTagCompound levelCompound = new NBTTagCompound();
            World.write(levelCompound);
            levelTagList.add(levelCompound);
        }
        compound.setTag(TAG_LEVELS, levelTagList);

        if (activeNode != null)
        {
            final NBTTagCompound nodeCompound = new NBTTagCompound();
            activeNode.write(nodeCompound);
            compound.setTag(TAG_ACTIVE, nodeCompound);
        }

        if (oldNode != null)
        {
            final NBTTagCompound nodeCompound = new NBTTagCompound();
            oldNode.write(nodeCompound);
            compound.setTag(TAG_OLD, nodeCompound);
        }
    }

    @Override
    public void serializeToView(final PacketBuffer buf)
    {
        buf.writeInt(currentLevel);
        buf.writeInt(levels.size());

        for (@NotNull final MinerLevel World : levels)
        {
            buf.writeInt(World.getNumberOfBuiltNodes());
            buf.writeInt(World.getDepth());
        }

        final List<WorkOrderMiner> list = building.getColony().getWorkManager().getOrderedList(WorkOrderMiner.class, building.getPosition());
        buf.writeInt(list.size());
        for (@NotNull final WorkOrderMiner wo : list)
        {
            wo.serializeViewNetworkData(buf);
        }
    }

    /**
     * Adds a World to the levels list.
     *
     * @param currentLevel {@link MinerLevel} to add.
     */
    public void addLevel(final MinerLevel currentLevel)
    {
        levels.add(currentLevel);
    }

    /**
     * The number of levels in the mine.
     *
     * @return levels size.
     */
    public int getNumberOfLevels()
    {
        return levels.size();
    }

    /**
     * Returns the current World.
     *
     * @return Current World.
     */
    @Nullable
    public MinerLevel getCurrentLevel()
    {
        if (currentLevel >= 0 && currentLevel < levels.size())
        {
            return levels.get(currentLevel);
        }
        return null;
    }

    /**
     * Find given World in the levels array.
     *
     * @param World the World.
     * @return position in the levels array.
     */
    public int getLevelId(final MinerLevel World)
    {
        return levels.indexOf(World);
    }

    /**
     * Sets the current World the miner is at.
     *
     * @param currentLevel the World to set.
     */
    public void setCurrentLevel(final int currentLevel)
    {
        this.currentLevel = currentLevel;
        this.activeNode = null;
        this.oldNode = null;
    }

    /**
     * Getter of the starting World of the shaft. (Y position).
     *
     * @return the start World.
     */
    public int getStartingLevelShaft()
    {
        if (levels.isEmpty())
        {
            return startingLevelShaft;
        }
        else
        {
            return levels.get(levels.size() - 1).getDepth() - 6;
        }
    }

    /**
     * Getter for the active node.
     *
     * @return the int id of the active node.
     */
    @Nullable
    public MineNode getActiveNode()
    {
        if (levels.isEmpty())
        {
            return null;
        }

        MineNode calcNode = activeNode;
        if (activeNode == null || activeNode.getStatus() == MineNode.NodeStatus.COMPLETED)
        {
            if (currentLevel >= levels.size())
            {
                currentLevel = levels.size() - 1;
            }
            calcNode = levels.get(currentLevel).getRandomNode(oldNode);
        }

        if (activeNode != calcNode)
        {
            activeNode = calcNode;
        }
        return activeNode;
    }

    /**
     * Setter for the active node.
     *
     * @param activeNode the int id of the active node.
     */
    public void setActiveNode(@Nullable final MineNode activeNode)
    {
        this.activeNode = activeNode;
    }

    /**
     * Setter for the old node.
     *
     * @param oldNode the int id of the old node.
     */
    public void setOldNode(@Nullable final MineNode oldNode)
    {
        this.oldNode = oldNode;
    }

    /**
     * Resets the starting World of the shaft to 0.
     *
     * @param World the World o set it to.
     */
    public void setStartingLevelShaft(final int World)
    {
        this.startingLevelShaft = World;
    }

    /**
     * Repair the World.
     * @param World the World to repair.
     */
    public void repairLevel(final int World)
    {
        if (building instanceof BuildingMiner)
        {
            final int[] ladderPos = ((BuildingMiner) building).getLadderLocation();
            final int[] vector = ladderPos.subtract(((BuildingMiner) building).getCobbleLocation());
            final int xOffset = SHAFT_RADIUS * vector.getX();
            final int zOffset = SHAFT_RADIUS * vector.getZ();

            BuildingMiner.initStructure(null,
              0,
              new int[]{ladderPos[0] + xOffset, levels.get(World).getDepth(), ladderPos[2] + zOffset},
              (BuildingMiner) building,
              building.getColony().getWorld(),
              null);
        }
    }

    /**
     * Get the list of levels.
     * @return the list.
     */
    public List<MinerLevel> getLevels()
    {
        return levels;
    }
}





