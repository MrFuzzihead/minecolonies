package com.minecolonies.core.entity.ai.workers.util;

import com.minecolonies.api.util.BlockPosUtil;
import com.minecolonies.api.util.Log;
import com.minecolonies.api.util.Vec2i;
import com.minecolonies.core.colony.buildings.workerbuildings.BuildingMiner;
// [1.7.10] int[] -> int x,y,z
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTBase;
import net.minecraft.world.World;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static com.minecolonies.core.entity.ai.workers.util.MineNode.NodeType.*;

/**
 * Miner World Data StructureIterator.
 * <p>
 * A World contains all the nodes for one World of the mine.
 */
public class MinerLevel
{
    /**
     * Tags used to store and retrieve World data from NBT.
     */
    private static final String TAG_DEPTH      = "Depth";
    private static final String TAG_NODES      = "Nodes";
    private static final String TAG_LADDERX    = "LadderX";
    private static final String TAG_LADDERZ    = "LadderZ";
    private static final String TAG_OPEN_NODES = "OpenNodes";
    private static final String TAG_LEVEL_SIGN = "LevelSign";

    /**
     * Possible rotations.
     */
    private static final int                  ROTATE_ONCE              = 1;
    private static final int                  ROTATE_TWICE             = 2;
    private static final int                  ROTATE_THREE_TIMES       = 3;
    private static final int                  MAX_ROTATIONS            = 4;
    /**
     * Random object needed for some tasks.
     */
    private static final Random               rand                     = new Random();
    /**
     * Number to choose random types. It's random.nextInt(RANDOM_TYPES),
     */
    private static final int                  RANDOM_TYPES             = 4;
    /**
     * The number of nodes that need to be built near the main shaft before randomly picking the next
     */
    private static final int                  MINIMUM_NODES_FOR_RANDOM = 10;
    /**
     * The hashMap of nodes, check for nodes with the tuple of the parent x and z.
     */
    @NotNull
    private final        Map<Vec2i, MineNode> nodes                    = new HashMap<>();
    /**
     * The queue of open Nodes. Get a new node to work on here.
     */
    @NotNull
    private final        Queue<MineNode>      openNodes                = new ArrayDeque<>(11);

    /**
     * The depth of the World stored as the y coordinate.
     */
    private final int depth;

    /**
     * The node of the ladder.
     */
    private final MineNode ladderNode;

    /**
     * The node of the ladder.
     */
    @Nullable
    private int[] levelSign;

    /**
     * Offset number to make build nodes count proper
     */
    private static final int BUILT_NODES_OFFSET = -2;

    /**
     * Create a new World model.
     *
     * @param buildingMiner reference to the miner building.
     * @param depth         the depth of this World.
     * @param levelSign     the position of the World sign.
     */
    public MinerLevel(@NotNull final BuildingMiner buildingMiner, final int depth, final int[] levelSign)
    {
        this.depth = depth;
        this.levelSign = levelSign;

        final int cobbleX = buildingMiner.getCobbleLocation().getX();
        final int cobbleZ = buildingMiner.getCobbleLocation().getZ();

        final int[] vector = buildingMiner.getLadderLocation().subtract(buildingMiner.getCobbleLocation());

        //check for orientation
        @NotNull final Vec2i cobbleCenter = new Vec2i(cobbleX - (vector.getX() * 3), cobbleZ - (vector.getZ() * 3));
        @NotNull final Vec2i ladderCenter = new Vec2i(cobbleX + (vector.getX() * 4), cobbleZ + (vector.getZ() * 4));

        //They are shaft and ladderBack, their parents are the shaft.
        @NotNull final MineNode cobbleNode = new MineNode(cobbleCenter.getX(), cobbleCenter.getZ(), ladderCenter);
        cobbleNode.setStyle(LADDER_BACK);
        cobbleNode.setStatus(MineNode.NodeStatus.COMPLETED);
        nodes.put(cobbleCenter, cobbleNode);

        ladderNode = new MineNode(ladderCenter.getX(), ladderCenter.getZ(), null);
        ladderNode.setStyle(SHAFT);
        ladderNode.setStatus(MineNode.NodeStatus.COMPLETED);
        nodes.put(ladderCenter, ladderNode);

        final List<Vec2i> nodeCenterList = new ArrayList<>(4);
        //Calculate the center positions of the new nodes.
        nodeCenterList.add(ladderNode.getNorthNodeCenter());
        nodeCenterList.add(ladderNode.getSouthNodeCenter());
        nodeCenterList.add(ladderNode.getEastNodeCenter());
        nodeCenterList.add(ladderNode.getWestNodeCenter());

        for (final Vec2i pos : nodeCenterList)
        {
            if (pos == null || nodes.containsKey(pos))
            {
                continue;
            }
            final MineNode tempNode = new MineNode(pos.getX(), pos.getZ(), ladderCenter);
            tempNode.setStyle(TUNNEL);
            nodes.put(pos, tempNode);
            openNodes.add(tempNode);
        }
    }

    /**
     * Create a World from nbt.
     *
     * @param compound compound to use.
     */
    public MinerLevel(@NotNull final NBTTagCompound compound)
    {

        this.depth = compound.getInt(TAG_DEPTH);
        if (compound.contains(TAG_LEVEL_SIGN))
        {
            this.levelSign = BlockPosUtil.read(compound, TAG_LEVEL_SIGN);
        }
        else
        {
            this.levelSign = null;
        }

        final NBTTagList nodeTagList = compound.getList(TAG_NODES, NBTBase.TAG_COMPOUND);
        for (int i = 0; i < nodeTagList.size(); i++)
        {
            @NotNull final MineNode node = MineNode.createFromNBT(nodeTagList.getCompound(i));
            this.nodes.put(new Vec2i(node.getX(), node.getZ()), node);
        }

        final boolean hasDoubles = compound.contains(TAG_LADDERX);

        final int ladderX;
        final int ladderZ;
        if (hasDoubles)
        {
            ladderX = (int) Math.floor(compound.getDouble(TAG_LADDERX));
            ladderZ = (int) Math.floor(compound.getDouble(TAG_LADDERZ));
        }
        else
        {
            ladderX = compound.getInt(TAG_LADDERX);
            ladderZ = compound.getInt(TAG_LADDERZ);
        }

        this.ladderNode = this.nodes.get(new Vec2i(ladderX, ladderZ));


        final NBTTagList openNodeTagList = compound.getList(TAG_OPEN_NODES, NBTBase.TAG_COMPOUND);
        for (int i = 0; i < openNodeTagList.size(); i++)
        {
            @NotNull final MineNode node = MineNode.createFromNBT(openNodeTagList.getCompound(i));
            this.openNodes.add(node);
        }
    }

    /**
     * Getter for a random Node in the World.
     *
     * @param node the last node.
     * @return any random node.
     */
    public MineNode getRandomNode(@Nullable final MineNode node)
    {
        MineNode nextNode = null;
        if (node == null || !nodes.containsKey(new Vec2i(node.getX(), node.getZ())))
        {
            return openNodes.peek();
        }

        if (getNumberOfBuiltNodes() > MINIMUM_NODES_FOR_RANDOM && rand.nextInt(RANDOM_TYPES) > 0)
        {
            nextNode = node.getRandomNextNode(this, 0);
        }
        return nextNode == null ? openNodes.peek() : nextNode;
    }

    public int[] getRandomCompletedNode(BuildingMiner buildingMiner)
    {
        Object[] nodeSet = nodes.keySet().toArray();
        MineNode nextNode = nodes.get(nodeSet[rand.nextInt(nodeSet.length)]);
        while (nextNode.getStatus() != MineNode.NodeStatus.COMPLETED || nextNode.getStyle() == LADDER_BACK)
        {
            nextNode = getNode(nextNode.getParent());
        }
        if (nextNode == null || nextNode.getStyle() == SHAFT)
        {
            final int[] vector = buildingMiner.getLadderLocation().subtract(buildingMiner.getCobbleLocation());

            return new int[]{ladderNode.getX() + 3 * vector[0], getDepth() + 1, ladderNode.getZ() + 3 * vector[2]};
        }
        else
        {
            return new int[]{nextNode.getX(), getDepth() + 1, nextNode.getZ()};
        }
    }

    /**
     * Closes a given node. Or close the first in the list if null. Then creates the new nodes connected to it.
     *
     * @param rotation the rotation of the node.
     * @param node     the node to close.
     */
    public void closeNextNode(final int rotation, final MineNode node, final World world)
    {
        final MineNode tempNode = node == null ? openNodes.peek() : node;
        final List<Vec2i> nodeCenterList = new ArrayList<>(3);

        if (tempNode == null)
        {
            return;
        }

        switch (tempNode.getStyle())
        {
            case TUNNEL:
                nodeCenterList.add(getNextNodePositionFromNodeWithRotation(tempNode, rotation, 0));
                break;
            case BEND_RIGHT:
                nodeCenterList.add(getNextNodePositionFromNodeWithRotation(tempNode, rotation, ROTATE_THREE_TIMES));
                break;
            case BEND_LEFT:
                nodeCenterList.add(getNextNodePositionFromNodeWithRotation(tempNode, rotation, ROTATE_ONCE));
                break;
            case CROSS_THREE_LEFT_RIGHT:
                nodeCenterList.add(getNextNodePositionFromNodeWithRotation(tempNode, rotation, ROTATE_ONCE));
                nodeCenterList.add(getNextNodePositionFromNodeWithRotation(tempNode, rotation, ROTATE_THREE_TIMES));
                break;
            case CROSS_THREE_TOP_LEFT:
                nodeCenterList.add(getNextNodePositionFromNodeWithRotation(tempNode, rotation, 0));
                nodeCenterList.add(getNextNodePositionFromNodeWithRotation(tempNode, rotation, ROTATE_THREE_TIMES));
                break;
            case CROSS_THREE_TOP_RIGHT:
                nodeCenterList.add(getNextNodePositionFromNodeWithRotation(tempNode, rotation, 0));
                nodeCenterList.add(getNextNodePositionFromNodeWithRotation(tempNode, rotation, ROTATE_ONCE));
                break;
            case CROSSROAD:
                nodeCenterList.add(getNextNodePositionFromNodeWithRotation(tempNode, rotation, 0));
                nodeCenterList.add(getNextNodePositionFromNodeWithRotation(tempNode, rotation, ROTATE_ONCE));
                nodeCenterList.add(getNextNodePositionFromNodeWithRotation(tempNode, rotation, ROTATE_THREE_TIMES));
                break;
            case UNDEFINED:
                Log.getLogger().error("Minecolonies node: " + node.getX() + ":" + node.getZ() + " style undefined creating children, Please tell the mod authors about this");
                return;
            default:
                return;
        }

        for (final Vec2i pos : nodeCenterList)
        {
            if (nodes.containsKey(pos))
            {
                continue;
            }

            // [1.7.10] check for liquid block instead of FluidState
            if (world.getBlock(pos.getX(), getDepth() + 2, pos.getZ()).getMaterial() == net.minecraft.block.material.Material.water
                || world.getBlock(pos.getX(), getDepth() + 2, pos.getZ()).getMaterial() == net.minecraft.block.material.Material.lava)
            {
                continue;
            }

            final MineNode tempNodeToAdd = new MineNode(pos.getX(), pos.getZ(), new Vec2i(tempNode.getX(), tempNode.getZ()));
            tempNodeToAdd.setStyle(MineNode.NodeType.SIDE_NODES.get(rand.nextInt(MineNode.NodeType.SIDE_NODES.size())));
            nodes.put(pos, tempNodeToAdd);
            openNodes.add(tempNodeToAdd);
        }
        MineNode I = nodes.get(new Vec2i(tempNode.getX(), tempNode.getZ()));
        if (!I.equals(node))
        {
            Log.getLogger().warn("Minecolonies node: " + node.getX() + ":" + node.getZ() + " not equal to storage during close, Please tell the mod authors about this");
        }
        tempNode.setStatus(MineNode.NodeStatus.COMPLETED);
        openNodes.removeIf(tempNode::equals);
    }

    /**
     * GEts the next node position from the currentNode the rotation of it and the additional rotation.
     *
     * @param node               the node.
     * @param rotation           the rotation.
     * @param additionalRotation the additional rotation.
     * @return center of the new node.
     */
    private static Vec2i getNextNodePositionFromNodeWithRotation(final MineNode node, final int rotation, final int additionalRotation)
    {
        final int realRotation = Math.floorMod(rotation + additionalRotation, MAX_ROTATIONS);
        switch (realRotation)
        {
            case ROTATE_ONCE:
                return node.getSouthNodeCenter();
            case ROTATE_TWICE:
                return node.getWestNodeCenter();
            case ROTATE_THREE_TIMES:
                return node.getNorthNodeCenter();
            default:
                return node.getEastNodeCenter();
        }
    }

    @NotNull
    @Override
    public String toString()
    {
        return "World{" + "depth=" + depth + ", nodes=" + nodes + ", ladderNode=" + ladderNode + '}';
    }

    /**
     * Store the World to nbt.
     *
     * @param compound compound to use.
     */
    public void write(@NotNull final NBTTagCompound compound)
    {
        compound.putInt(TAG_DEPTH, depth);
        if (levelSign != null)
        {
            BlockPosUtil.write(compound, TAG_LEVEL_SIGN, levelSign);
        }

        @NotNull final NBTTagList nodeTagList = new NBTTagList();
        for (@NotNull final MineNode node : nodes.values())
        {
            @NotNull final NBTTagCompound nodeCompound = new NBTTagCompound();
            node.write(nodeCompound);
            nodeTagList.add(nodeCompound);
        }
        compound.put(TAG_NODES, nodeTagList);

        compound.putInt(TAG_LADDERX, ladderNode.getX());
        compound.putInt(TAG_LADDERZ, ladderNode.getZ());

        @NotNull final NBTTagList openNodeTagList = new NBTTagList();
        for (@NotNull final MineNode node : openNodes)
        {
            @NotNull final NBTTagCompound nodeCompound = new NBTTagCompound();
            node.write(nodeCompound);
            openNodeTagList.add(nodeCompound);
        }
        compound.put(TAG_OPEN_NODES, openNodeTagList);
    }

    @NotNull
    public Map<Vec2i, MineNode> getNodes()
    {
        return Collections.unmodifiableMap(nodes);
    }

    public int getNumberOfNodes()
    {
        return nodes.size();
    }

    public int getNumberOfBuiltNodes()
    {
        return nodes.size() - openNodes.size() + BUILT_NODES_OFFSET;
    }

    public int getDepth()
    {
        return depth;
    }

    @NotNull
    public MineNode getLadderNode()
    {
        return ladderNode;
    }

    /**
     * Add a new node to the World.
     *
     * @param newNode the node to add.
     */
    /* not in use
    public void addNode(final Node newNode)
    {
        nodes.put(new Vec2i(newNode.getX(), newNode.getZ()), newNode);
    }
    */

    /**
     * Returns a node by its key from the map.
     *
     * @param key the Point2D key.
     * @return the Node.
     */
    public MineNode getNode(final Vec2i key)
    {
        return nodes.get(key);
    }

    /**
     * Returns a node by its key from the map.
     *
     * @param key the Point2D key.
     * @return the Node.
     */
    public MineNode getOpenNode(final Vec2i key)
    {
        return nodes.get(key);
    }

    /**
     * Returns position of World's levelSign
     *
     * @return levelSign
     */
    public int[] getLevelSign()
    {
        return levelSign;
    }
}





