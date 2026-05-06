package com.minecolonies.api.colony.connections;

import com.minecolonies.api.util.BlockPosUtil;
// [1.7.10] int[] -> int x,y,z
import net.minecraft.nbt.NBTTagCompound;
import org.jetbrains.annotations.NotNull;

import static com.minecolonies.api.util.constant.NbtTagConstants.*;

/**
 * Node in the path from one colony to another.
 */
public class ColonyConnectionNode
{
    /**
     * The previous point in the connection grid.
     */
    private int[] previousNode = new int[]{0,0,0};

    /**
     * The next point in the connection grid.
     */
    private int[] nextNode = new int[]{0,0,0};

    /**
     * Position of this point.
     */
    private final int[] position;

    /**
     * Connected colony.
     */
    private int targetColonyId = -1;

    /**
     * Create a new connection node at a given pos.
     * @param position the pos.
     */
    public ColonyConnectionNode(final int[] position)
    {
        this.position = position;
    }

    /**
     * Alter the previous node connection.
     * @param previousNode the previous node pos.
     */
    public void alterPreviousNode(final int[] previousNode)
    {
        this.previousNode = previousNode;
    }

    /**
     * Alter the next node connection.
     * @param nextNode the next nod epos.
     */
    public void alterNextNode(final int[] nextNode)
    {
        this.nextNode = nextNode;
    }

    /**
     * Get position of this point.
     * @return the position.
     */
    public int[] getPosition()
    {
        return position;
    }

    /**
     * Get the previous node.
     * @return prev node pos or zero if not set.
     */
    public int[] getPreviousNode()
    {
        return previousNode;
    }

    /**
     * Get the next node.
     * @return next node pos or ZERO if not set.
     */
    public int[] getNextNode()
    {
        return nextNode;
    }

    /**
     * Get the connected target colony id.
     * @return the target colony id.
     */
    public int getTargetColonyId()
    {
        return targetColonyId;
    }

    /**
     * Set the target colony id.
     * @param targetColonyId the id to set.
     */
    public void setTargetColonyId(final int targetColonyId)
    {
        this.targetColonyId = targetColonyId;
    }

    /**
     * Write connections to NBT data for saving.
     * @return compound NBT-NBTBase.
     */
    public NBTTagCompound write()
    {
        final NBTTagCompound compound = new NBTTagCompound();
        BlockPosUtil.write(compound, TAG_POS, position);
        BlockPosUtil.write(compound, TAG_PREV_POS, previousNode);
        BlockPosUtil.write(compound, TAG_NEXT_POS, nextNode);
        compound.setInteger(TAG_TARGET_COLONY_ID, targetColonyId);
        return compound;
    }

    /**
     * Read connections from saved NBT data.
     *
     * @param compound NBT NBTBase.
     */
    public void read(@NotNull final NBTTagCompound compound)
    {
        this.previousNode = BlockPosUtil.read(compound, TAG_PREV_POS);
        this.nextNode = BlockPosUtil.read(compound, TAG_NEXT_POS);
        this.targetColonyId = compound.getInteger(TAG_TARGET_COLONY_ID); // [1.7.10]
    }

    /**
     * If another node can connect to this.
     * @return true if so.
     */
    public boolean hasNextNode()
    {
        return !nextNode.equals(new int[]{0,0,0});
    }
}




