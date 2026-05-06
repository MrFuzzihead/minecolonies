package com.minecolonies.core.colony.managers;
// [1.7.10] removed bad 1.21 imports
// import net.minecraft.util.Direction;
// import net.minecraft.world.level.block.state.BlockState;
// import net.minecraft.world.entity.player.Player;
// import net.minecraft.world.phys.AABB;
// import net.minecraft.util.RandomSource;
// import net.minecraft.world.item.BoneMealItem;

import com.minecolonies.api.blocks.ModBlocks;
import com.minecolonies.api.colony.*;
import com.minecolonies.api.colony.connections.*;
import com.minecolonies.api.util.BlockPosUtil;
import com.minecolonies.api.util.MessageUtils;
import com.minecolonies.api.util.WorldUtil;
import com.minecolonies.core.blocks.BlockColonySign;
import com.minecolonies.core.entity.pathfinding.Pathfinding;
import com.minecolonies.core.entity.pathfinding.pathjobs.PathJobSignConnection;
import com.minecolonies.core.entity.pathfinding.pathresults.PathResult;
// [1.7.10] int[] -> int x,y,z
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTBase;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.StatCollector;
// [1.7.10] tags removed
// [1.7.10] BlockState -> int metadata
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static com.minecolonies.api.colony.connections.ConnectionEventType.*;
import static com.minecolonies.api.util.constant.NbtTagConstants.*;
import static com.minecolonies.api.util.constant.TranslationConstants.*;

public class ColonyConnectionManager implements IColonyConnectionManager
{
    /**
     * Max sign range for stacking.
     */
    private static final int MAX_SIGN_RANGE = 5;

    /**
     * There are the individual connection points. The position represents the position a sign is at.
     */
    private final Map<int[], ColonyConnectionNode> colonyConnections = new LinkedHashMap<>();

    /**
     * List of gate house positions.
     */
    private final List<int[]> gateHouses = new ArrayList<>();

    /**
     * Connected colony.
     */
    private final IColony colony;

    /**
     * Connected colonies mapped to their gate position.
     */
    private final TreeMap<Integer, ColonyConnection> directlyConnectedColonies = new TreeMap<>();

    /**
     * Cached connection data.
     */
    private final TreeMap<Integer, ColonyConnection> indirectlyConnectedColoniesCache = new TreeMap<>();

    /**
     * Connection events affecting this colony. From colony id invoking the event, to event data.
     */
    private final Map<Integer, ConnectionEvent> connectionEvents = new TreeMap<>();

    /**
     * Pending connection points. This is stored to nbt.
     */
    private final Map<int[], PendingConnectionNode> pendingColonyConnections = new LinkedHashMap<>();

    /**
     * Create a new connection manager.
     * @param colony its colony.
     */
    public ColonyConnectionManager(final IColony colony)
    {
        this.colony = colony;
    }

    /**
     * Get the closest node with an open nextNode connection.
     * @param pos the pos the connection point is at.
     * @return a possible node or null.
     */
    @Nullable
    private ColonyConnectionNode getClosestNodeWithOpenConnection(final int[] pos, final boolean logIfNull)
    {
        int distance = Integer.MAX_VALUE;
        ColonyConnectionNode potentialConnection = null;
        for (final ColonyConnectionNode node : colonyConnections.values())
        {
            // Only connect to a node with correct distance.
            if (!node.hasNextNode())
            {
                final int[] np = node.getPosition();
                final int localDistance = (int) BlockPosUtil.getDistanceSquared(np[0], np[1], np[2], pos[0], pos[1], pos[2]); // [1.7.10] int[].distSqr -> BlockPosUtil helper
                if (localDistance <= 50*50 && localDistance < distance)
                {
                    distance = localDistance;
                    potentialConnection = node;
                }
            }
        }
        if (potentialConnection == null)
        {
            MessageUtils.format(COM_MINECOLONIES_SIGN_TOO_FAR, distance).sendTo(this.colony).forManagers();
        }
        return potentialConnection;
    }

    @Override
    public boolean addNewConnectionNode(final int[] connectionPoint)
    {
        if (!pendingColonyConnections.isEmpty())
        {
            MessageUtils.format(COM_MINECOLONIES_CONNECTION_PATH_PENDING).withPriority(MessageUtils.MessagePriority.DANGER).sendTo(colony).forManagers();
            return false;
        }

        for (final int[] gateHousePos : gateHouses)
        {
            if (BlockPosUtil.getDistanceSquared(gateHousePos[0], gateHousePos[1], gateHousePos[2], connectionPoint[0], connectionPoint[1], connectionPoint[2]) <= 50*50) // [1.7.10] int[].distSqr -> BlockPosUtil helper
            {
                final PendingConnectionNode newNode = new PendingConnectionNode(connectionPoint, createSignPath(connectionPoint, gateHousePos), PendingConnectionNode.PendingConnectionType.DEFAULT);
                newNode.alterPreviousNode(gateHousePos);

                pendingColonyConnections.put(connectionPoint, newNode);
                return true;
            }
        }

        final ColonyConnectionNode potentialConnection = getClosestNodeWithOpenConnection(connectionPoint, true);
        if (potentialConnection == null)
        {
            return false;
        }
        Set<int[]> visitedNodes = new HashSet<>();
        int[] tempNode = potentialConnection.getPreviousNode();
        while (colonyConnections.containsKey(tempNode) && !visitedNodes.contains(tempNode))
        {
            tempNode = colonyConnections.get(tempNode).getPreviousNode();
            visitedNodes.add(tempNode);
        }

        if (tempNode == null && !gateHouses.contains(tempNode))
        {
            MessageUtils.format(COM_MINECOLONIES_SIGN_MISSING_LINK).withPriority(MessageUtils.MessagePriority.DANGER).sendTo(colony).forManagers();
            return false;
        }

        final PendingConnectionNode newNode = new PendingConnectionNode(connectionPoint, createSignPath(connectionPoint, potentialConnection.getPosition()), PendingConnectionNode.PendingConnectionType.DEFAULT);
        newNode.alterPreviousNode(potentialConnection.getPosition());
        if (potentialConnection.getTargetColonyId() != -1)
        {
            newNode.setTargetColonyId(potentialConnection.getTargetColonyId());
        }

        pendingColonyConnections.put(connectionPoint, newNode);
        return true;
    }

    @Override
    public void removeConnectionNode(final int[] connectionPoint)
    {
        final ColonyConnectionNode colonyConnectionNode = colonyConnections.remove(connectionPoint);
        if (colonyConnectionNode != null)
        {
            final ColonyConnectionNode previousNode = colonyConnections.get(colonyConnectionNode.getPreviousNode());
            if (previousNode != null)
            {
                previousNode.alterNextNode(new int[]{0,0,0});
                final int[] pnPos = previousNode.getPosition();
                MessageUtils.format(COM_MINECOLONIES_SIGN_DISRUPTED, "[" + pnPos[0] + "," + pnPos[1] + "," + pnPos[2] + "]").sendTo(this.colony).forManagers(); // [1.7.10] String.translatable
            }
            final ColonyConnectionNode nextNode = colonyConnections.get(colonyConnectionNode.getNextNode());
            if (nextNode != null)
            {
                nextNode.alterPreviousNode(new int[]{0,0,0});
                final int[] nnPos = nextNode.getPosition();
                MessageUtils.format(COM_MINECOLONIES_SIGN_DISRUPTED, "[" + nnPos[0] + "," + nnPos[1] + "," + nnPos[2] + "]").sendTo(this.colony).forManagers(); // [1.7.10] String.translatable
            }
        }
        pendingColonyConnections.remove(connectionPoint);
    }

    @Override
    public boolean attemptEstablishConnection(final int[] targetColonyConnectionPos, final IColony targetColony)
    {
        final ColonyConnectionNode thisColonyConnectionPos = getClosestNodeWithOpenConnection(targetColonyConnectionPos, true);
        if (thisColonyConnectionPos == null)
        {
            return false;
        }

        final PendingConnectionNode newNode = new PendingConnectionNode(thisColonyConnectionPos.getPosition(), createSignPath(thisColonyConnectionPos.getPosition(), targetColonyConnectionPos), PendingConnectionNode.PendingConnectionType.CONNECT_COLONY);
        newNode.alterPreviousNode(targetColonyConnectionPos);
        newNode.setTargetColonyId(targetColony.getID());

        pendingColonyConnections.put(thisColonyConnectionPos.getPosition(), newNode);
        return true;
    }

    @Override
    public void tick()
    {
        for (Map.Entry<int[], PendingConnectionNode> pendingConnection : new ArrayList<>(pendingColonyConnections.entrySet()))
        {
            if (pendingConnection.getValue().getCachedPathResult() == null)
            {
                if (WorldUtil.isBlockLoaded(colony.getWorld(), pendingConnection.getKey()[0], pendingConnection.getKey()[2])) // [1.7.10] isBlockLoaded(world, int[]) -> (world, x, z)
                {
                    pendingConnection.getValue().setCachedPathResult(createSignPath(pendingConnection.getValue().getPosition(), pendingConnection.getValue().getPreviousNode()));
                }
            }
            else if (pendingConnection.getValue().getCachedPathResult().isDone())
            {
                if (pendingConnection.getValue().getCachedPathResult().isPathReachingDestination())
                {
                    if (pendingColonyConnections.remove(pendingConnection.getKey()) == null)
                    {
                        return;
                    }
                    final ColonyConnectionNode connection = colonyConnections.get(pendingConnection.getValue().getPreviousNode());
                    if (pendingConnection.getValue().getPendingConnectionType() == PendingConnectionNode.PendingConnectionType.DEFAULT)
                    {
                        if (connection == null && !gateHouses.contains(pendingConnection.getValue().getPreviousNode()))
                        {
                            final int[] dk = pendingConnection.getKey();
                            colony.getWorld().func_147480_a(dk[0], dk[1], dk[2], true); // [1.7.10] destroyBlock -> func_147480_a (destroy with drops)
                            final int[] pn = pendingConnection.getValue().getPreviousNode();
                            MessageUtils.format(COM_MINECOLONIES_CONNECTION_PATH_FAILURE,
                                "[" + dk[0] + "," + dk[1] + "," + dk[2] + "]", // [1.7.10] toShortString not on int[]
                                "[" + pn[0] + "," + pn[1] + "," + pn[2] + "]").withPriority(MessageUtils.MessagePriority.DANGER).sendTo(colony).forManagers();
                            continue;
                        }
                        colonyConnections.put(pendingConnection.getKey(), pendingConnection.getValue());
                    }

                    final int[] pos0 = pendingConnection.getValue().getPosition();
                    final int[] pos1 = pendingConnection.getValue().getPreviousNode();
                    MessageUtils.format(COM_MINECOLONIES_SIGN_CONNECTED,
                            "[" + pos0[0] + "," + pos0[1] + "," + pos0[2] + "]", // [1.7.10] toShortString not on int[]
                            "[" + pos1[0] + "," + pos1[1] + "," + pos1[2] + "]")
                        .withPriority(MessageUtils.MessagePriority.IMPORTANT)
                        .sendTo(colony)
                        .forManagers();

                    if (connection != null)
                    {
                        connection.alterNextNode(pendingConnection.getValue().getPosition());
                        final ColonyConnectionNode thisNode = colonyConnections.get(pendingConnection.getValue().getPosition());
                        if (thisNode != null)
                        {
                            thisNode.alterPreviousNode(connection.getPosition());
                        }
                    }

                    if (gateHouses.contains(pendingConnection.getKey()))
                    {
                        final ColonyConnectionNode nextNode = colonyConnections.get(pendingConnection.getValue().getNextNode());
                        if (nextNode != null)
                        {
                            nextNode.alterPreviousNode(pendingConnection.getKey());
                            final int targetColonyId = pendingConnection.getValue().getTargetColonyId();
                            if (targetColonyId != -1)
                            {
                                final IColony connectedColony = IColonyManager.getInstance().getColonyByDimension(targetColonyId, colony.getDimension());
                                if (connectedColony != null)
                                {
                                    connectedColony.getConnectionManager().getDirectlyConnectedColonies().put(colony.getID(),
                                        new ColonyConnection(colony.getID(),
                                            colony.getName(),
                                            pendingConnection.getKey(),
                                            directlyConnectedColonies.get(targetColonyId).diplomacyStatus));
                                }
                            }
                        }
                    }

                    if (pendingConnection.getValue().getPendingConnectionType() == PendingConnectionNode.PendingConnectionType.CONNECT_COLONY)
                    {
                        connectToColony(pendingConnection.getKey(), pendingConnection.getValue().getTargetColonyId(), pendingConnection.getValue().getPreviousNode());
                    }
                    else if (pendingConnection.getValue().getPendingConnectionType() == PendingConnectionNode.PendingConnectionType.DEFAULT)
                    {
                        // After successful connection try to find a next connection to (for repair inbetween).
                        int distance = Integer.MAX_VALUE;
                        ColonyConnectionNode potentialConnection = null;
                        for (final ColonyConnectionNode node : colonyConnections.values())
                        {
                            // Only connect to a node with correct distance.
                            if (node.getPreviousNode().equals(new int[]{0,0,0}) && !node.getPosition().equals(pendingConnection.getKey()))
                            {
                                final int[] np2 = node.getPosition();
                                final int[] ck2 = pendingConnection.getKey();
                                final int localDistance = (int) BlockPosUtil.getDistanceSquared(np2[0], np2[1], np2[2], ck2[0], ck2[1], ck2[2]); // [1.7.10] int[].distSqr
                                if (localDistance <= 50 * 50 && localDistance < distance && !node.getPosition().equals(pendingConnection.getKey()))
                                {
                                    distance = localDistance;
                                    potentialConnection = node;
                                }
                            }
                        }
                        if (potentialConnection != null)
                        {
                            final PendingConnectionNode newNode = new PendingConnectionNode(potentialConnection.getPosition(), createSignPath(potentialConnection.getPosition(), pendingConnection.getKey()), PendingConnectionNode.PendingConnectionType.FIX_PATH);
                            newNode.alterPreviousNode(pendingConnection.getKey());
                            newNode.alterNextNode(potentialConnection.getNextNode());
                            if (pendingConnection.getValue().getTargetColonyId() != -1)
                            {
                                newNode.setTargetColonyId(pendingConnection.getValue().getTargetColonyId());
                            }
                            else if (potentialConnection.getTargetColonyId() != -1)
                            {
                                newNode.setTargetColonyId(potentialConnection.getTargetColonyId());
                            }

                            pendingColonyConnections.put(newNode.getPosition(), newNode);
                        }
                    }
                }
                else
                {
                    if (pendingConnection.getValue().getPendingConnectionType() != PendingConnectionNode.PendingConnectionType.DEFAULT)
                    {
                        continue;
                    }
                    final int[] dk2 = pendingConnection.getKey();
                    final int[] pn2 = pendingConnection.getValue().getPreviousNode();
                    colony.getWorld().func_147480_a(dk2[0], dk2[1], dk2[2], true); // [1.7.10] destroyBlock -> func_147480_a
                    pendingColonyConnections.remove(pendingConnection.getKey());
                    MessageUtils.format(COM_MINECOLONIES_CONNECTION_PATH_FAILURE,
                        "[" + dk2[0] + "," + dk2[1] + "," + dk2[2] + "]", // [1.7.10] toShortString
                        "[" + pn2[0] + "," + pn2[1] + "," + pn2[2] + "]").withPriority(MessageUtils.MessagePriority.DANGER).sendTo(colony).forManagers();
                }
            }
        }

        // Update connections.
        updateConnectedColonies(directlyConnectedColonies);
        updateConnectedColonies(indirectlyConnectedColoniesCache);
    }

    /**
     * Handle the connection between two colonies. Make sure both gates are reachable.
     * @param thisColonyConnectionPos the connection pos in this colony.
     * @param targetColonyId the target colony id.
     * @param targetColonyConnectionPos the connection pos in the target colony.
     */
    private void connectToColony(final int[] thisColonyConnectionPos, final int targetColonyId, final int[] targetColonyConnectionPos)
    {
        final IColony targetColony = IColonyManager.getInstance().getColonyByDimension(targetColonyId, colony.getDimension());
        if (targetColony == null)
        {
            MessageUtils.format(COM_MINECOLONIES_CONNECTION_NO_COLONY).sendTo(this.colony).forManagers(); // [1.7.10] String.translatable
            return;
        }
        // Make sure we're connected until the gate.
        int[] thisColonyGatePos = thisColonyConnectionPos;
        Set<int[]> visitedNodes = new HashSet<>();
        int[] lastPos = thisColonyGatePos;
        while (colonyConnections.containsKey(thisColonyGatePos))
        {
            lastPos = thisColonyGatePos;
            thisColonyGatePos = colonyConnections.get(thisColonyGatePos).getPreviousNode();
            if (!visitedNodes.add(thisColonyGatePos))
            {
                break;
            }
        }

        if (thisColonyGatePos == null || !gateHouses.contains(thisColonyGatePos))
        {
            MessageUtils.format(StatCollector.translateToLocal(COM_MINECOLONIES_CONNECTION_BROKEN) + " [" + lastPos[0] + "," + lastPos[1] + "," + lastPos[2] + "]").sendTo(this.colony).forManagers(); // [1.7.10] String.translatable; toShortString
            return;
        }

        final ColonyConnectionManager targetManager = (ColonyConnectionManager) targetColony.getConnectionManager();
        final ColonyConnectionNode targetNode = targetManager.colonyConnections.get(targetColonyConnectionPos);
        if ((targetNode != null && targetNode.hasNextNode()) && !targetManager.gateHouses.contains(targetColonyConnectionPos))
        {
            MessageUtils.format(COM_MINECOLONIES_CONNECTION_FAIL).sendTo(this.colony).forManagers(); // [1.7.10] String.translatable
            return;
        }

        // Make sure the target colony is also connected until the gate.
        int[] targetColonyGatePos = targetNode == null ? targetColonyConnectionPos : targetNode.getPreviousNode();
        visitedNodes = new HashSet<>();
        while (targetManager.colonyConnections.containsKey(targetColonyGatePos))
        {
            targetColonyGatePos = targetManager.colonyConnections.get(targetColonyGatePos).getPreviousNode();
            if (!visitedNodes.add(targetColonyGatePos))
            {
                break;
            }
        }

        if (targetColonyGatePos == null || !targetManager.gateHouses.contains(targetColonyGatePos))
        {
            MessageUtils.format(COM_MINECOLONIES_CONNECTION_FAIL).sendTo(this.colony).forManagers(); // [1.7.10] String.translatable
            return;
        }

        // Set gate houses as connected.
        directlyConnectedColonies.put(targetColony.getID(), new ColonyConnection(targetColony.getID(), targetColony.getName(), targetColonyGatePos, DiplomacyStatus.NEUTRAL));
        targetManager.directlyConnectedColonies.put(colony.getID(), new ColonyConnection(colony.getID(), colony.getName(), thisColonyGatePos, DiplomacyStatus.NEUTRAL));

        // Connect the two middle nodes.
        final ColonyConnectionNode intermediateNode = colonyConnections.get(thisColonyConnectionPos);
        intermediateNode.alterNextNode(targetColonyConnectionPos);
        intermediateNode.setTargetColonyId(targetColony.getID());

        if (targetNode != null)
        {
            targetNode.alterNextNode(thisColonyConnectionPos);
            targetNode.setTargetColonyId(colony.getID());

            targetColonyGatePos = targetNode.getPreviousNode();
            while (targetManager.colonyConnections.containsKey(targetColonyGatePos))
            {
                final ColonyConnectionNode node = targetManager.colonyConnections.get(targetColonyGatePos);
                node.setTargetColonyId(colony.getID());
                targetColonyGatePos = node.getPreviousNode();
            }
        }

        thisColonyGatePos = thisColonyConnectionPos;
        while (colonyConnections.containsKey(thisColonyGatePos))
        {
            final ColonyConnectionNode node = colonyConnections.get(thisColonyGatePos);
            node.setTargetColonyId(targetColony.getID());
            thisColonyGatePos = node.getPreviousNode();
        }

        MessageUtils.format(COM_MINECOLONIES_CONNECTION_SUCCESS, colony.getName(), targetColony.getName()).sendTo(this.colony).forManagers();
        MessageUtils.format(COM_MINECOLONIES_CONNECTION_SUCCESS, targetColony.getName(), colony.getName()).sendTo(targetColony).forManagers();

        colony.markDirty();
    }

    /**
     * Creates and starts the pathjob towards this spawnpoint
     *
     * @param originPos the origin position.
     * @param targetPos the target position.
     * @return the path result.
     */
    private PathResult createSignPath(final int[] originPos, final int[] targetPos)
    {
        final int[] lowestOriginPos = findLowestPoint(originPos);
        final int[] lowestTargetPos = findLowestPoint(targetPos);

        final PathJobSignConnection job = new PathJobSignConnection(colony.getWorld(), lowestOriginPos, lowestTargetPos, 16);
        job.getResult().startJob(Pathfinding.getExecutor());
        return job.getResult();
    }

    /**
     * Try to path to bottom of sign (to allow stacking signs and allow putting them on fences)
     * @param targetPos
     * @return
     */
    private int[] findLowestPoint(final int[] targetPos)
    {
        int range = 0;
        int[] lowestPoint = targetPos;
        // [1.7.10] getBlockState/BlockState.is(BlockTags.FENCES) -> getBlock, instanceof BlockFence
        net.minecraft.block.Block lowestBlock = colony.getWorld().getBlock(lowestPoint[0], lowestPoint[1] - 1, lowestPoint[2]);
        while ((lowestBlock instanceof net.minecraft.block.BlockFence || lowestBlock == ModBlocks.blockColonySign) && range < MAX_SIGN_RANGE)
        {
            lowestPoint = new int[]{lowestPoint[0], lowestPoint[1] - 1, lowestPoint[2]};
            lowestBlock = colony.getWorld().getBlock(lowestPoint[0], lowestPoint[1] - 1, lowestPoint[2]);
            range++;
        }
        return lowestPoint;
    }

    /**
     * Go through connected colonies and check for potential neighbors and update name, or remove if necessary.
     * @param connectedColonies the list of connected colonies to process.
     */
    private void updateConnectedColonies(final TreeMap<Integer, ColonyConnection> connectedColonies)
    {
        // Update name in cache.
        for (final ColonyConnection colonyEntry : new ArrayList<>(connectedColonies.values()))
        {
            final IColony connectedColony = IColonyManager.getInstance().getColonyByDimension(colonyEntry.id, colony.getDimension());
            if (connectedColony == null)
            {
                connectedColonies.remove(colonyEntry.id);
                continue;
            }

            if (!connectedColony.getName().equals(colonyEntry.name))
            {
                connectedColonies.put(colonyEntry.id,
                    new ColonyConnection(connectedColony.getID(), connectedColony.getName(), colonyEntry.pos, colonyEntry.diplomacyStatus));
            }

            if (colonyEntry.diplomacyStatus == DiplomacyStatus.ALLIES)
            {
                for (final ColonyConnection indirectConnectedColony : connectedColony.getConnectionManager().getDirectlyConnectedColonies().values())
                {
                    if (!directlyConnectedColonies.containsKey(indirectConnectedColony.id) && indirectConnectedColony.id != colony.getID())
                    {
                        indirectlyConnectedColoniesCache.put(indirectConnectedColony.id, indirectConnectedColony);
                    }
                }
            }
        }
    }

    @Override
    public TreeMap<Integer, ColonyConnection> getDirectlyConnectedColonies()
    {
        return directlyConnectedColonies;
    }

    @Override
    public TreeMap<Integer, ColonyConnection> getIndirectlyConnectedColonies()
    {
        return indirectlyConnectedColoniesCache;
    }

    @Override
    public ColonyConnectionNode getNode(final int[] blockPos)
    {
        return colonyConnections.get(blockPos);
    }

    @Override
    public void addNewGateHouse(final int[] gateHouseConnectionNode)
    {
        if (!gateHouses.contains(gateHouseConnectionNode))
        {
            gateHouses.add(gateHouseConnectionNode);
            for (final ColonyConnectionNode node : colonyConnections.values())
            {
                // Only connect to a node with correct distance.
                if (node.getPreviousNode().equals(new int[]{0,0,0}))
                {
                    final int[] gp = node.getPosition();
                    if (BlockPosUtil.getDistanceSquared(gp[0], gp[1], gp[2], gateHouseConnectionNode[0], gateHouseConnectionNode[1], gateHouseConnectionNode[2]) <= 50 * 50) // [1.7.10] distSqr
                    {
                        final PendingConnectionNode newNode = new PendingConnectionNode(gateHouseConnectionNode,
                            createSignPath(gateHouseConnectionNode, node.getPosition()),
                            PendingConnectionNode.PendingConnectionType.FIX_PATH);
                        newNode.setTargetColonyId(node.getTargetColonyId());
                        newNode.alterNextNode(node.getPosition());
                        pendingColonyConnections.put(newNode.getPosition(), newNode);
                    }
                }
            }
        }
    }

    @Override
    public void removeGateHouse(final int[] gateHousePosition)
    {
        for (final ColonyConnectionNode colonyConnectionNode : colonyConnections.values())
        {
            if (colonyConnectionNode.getPreviousNode().equals(gateHousePosition))
            {
                colonyConnectionNode.alterPreviousNode(new int[]{0,0,0});
                MessageUtils.format(COM_MINECOLONIES_SIGN_DISRUPTED, colonyConnectionNode.getPosition()).sendTo(this.colony).forManagers();
            }
        }

       gateHouses.remove(gateHousePosition);

        // Set connected pos to zero, can't teleport to gatehouse now.
        for (final ColonyConnection connectedColonyData : directlyConnectedColonies.values())
        {
            final IColony connectedColony = IColonyManager.getInstance().getColonyByDimension(connectedColonyData.id, colony.getDimension());
            if (connectedColony != null)
            {
                connectedColony.getConnectionManager().getDirectlyConnectedColonies().put(colony.getID(),
                    new ColonyConnection(colony.getID(), colony.getName(), new int[]{0,0,0}, connectedColonyData.diplomacyStatus));
            }
        }
    }

    @Override
    public void serializeToView(@NotNull final PacketBuffer buf)
    {
        buf.writeInt(directlyConnectedColonies.size());
        for (final Map.Entry<Integer, ColonyConnection> connectedColony : directlyConnectedColonies.entrySet())
        {
            connectedColony.getValue().serializeByteBuf(buf);
        }

        buf.writeInt(indirectlyConnectedColoniesCache.size());
        for (final Map.Entry<Integer, ColonyConnection> connectedColony : indirectlyConnectedColoniesCache.entrySet())
        {
            connectedColony.getValue().serializeByteBuf(buf);
        }

        buf.writeInt(connectionEvents.size());
        for (final ConnectionEvent connectionEventType : connectionEvents.values())
        {
            connectionEventType.serializeByteBuf(buf);
        }
    }

    @Override
    public void deserializeFromView(@NotNull final PacketBuffer buf)
    {
       final int directConnectionsSize = buf.readInt();
       for (int i = 0; i < directConnectionsSize; i++)
       {
           final ColonyConnection connectedColonyData = new ColonyConnection().deserializeByteBuf(buf);
           directlyConnectedColonies.put(connectedColonyData.id, connectedColonyData);
       }

        final int indirectConnectionsSize = buf.readInt();
        for (int i = 0; i < indirectConnectionsSize; i++)
        {
            final ColonyConnection connectedColonyData = new ColonyConnection().deserializeByteBuf(buf);
            indirectlyConnectedColoniesCache.put(connectedColonyData.id, connectedColonyData);
        }

        connectionEvents.clear();
        final int connectionEventSize = buf.readInt();
        for (int i = 0; i < connectionEventSize; i++)
        {
            final ConnectionEvent connectionEventData = ConnectionEvent.deserializeByteBuf(buf);
            connectionEvents.put(connectionEventData.id, connectionEventData);
        }
    }

    @Override
    public void deserializeNBT(final NBTTagCompound compound)
    {
        final NBTTagList connectionTagList = compound.getTagList(TAG_CONNECTIONS, 10); // [1.7.10] NBTBase.TAG_COMPOUND -> 10
        for (int i = 0; i < connectionTagList.tagCount(); i++) // [1.7.10] not iterable
        {
            final NBTTagCompound tag = connectionTagList.getCompoundTagAt(i);
            final int[] pos = BlockPosUtil.read(tag, TAG_POS);
            final ColonyConnectionNode connectionPoint = new ColonyConnectionNode(pos);
            connectionPoint.read(tag);
            colonyConnections.put(pos, connectionPoint);
        }

        final NBTTagList connectedColonyTagList = compound.getTagList(TAG_COLONIES, 10); // [1.7.10]
        for (int i = 0; i < connectedColonyTagList.tagCount(); i++) // [1.7.10] not iterable
        {
            final NBTTagCompound tag = connectedColonyTagList.getCompoundTagAt(i);
            final ColonyConnection colonyConnectionData = new ColonyConnection().deserializeNBT(tag);
            directlyConnectedColonies.put(colonyConnectionData.id, colonyConnectionData);
        }

        gateHouses.clear();
        final NBTTagList gateHouseTagList = compound.getTagList(TAG_GATEHOUSES, 10); // [1.7.10]
        for (int i = 0; i < gateHouseTagList.tagCount(); i++) // [1.7.10] not iterable
        {
            gateHouses.add(BlockPosUtil.read(gateHouseTagList.getCompoundTagAt(i), TAG_POS));
        }

        connectionEvents.clear();
        final NBTTagList connectionEventList = compound.getTagList(TAG_CONNECTION_EVENTS, 10); // [1.7.10]
        for (int i = 0; i < connectionEventList.tagCount(); i++) // [1.7.10] not iterable
        {
            final NBTTagCompound tag = connectionEventList.getCompoundTagAt(i);
            final ConnectionEvent connectionEventData = ConnectionEvent.deserializeNBT(tag);
            connectionEvents.put(connectionEventData.id, connectionEventData);
        }

        final NBTTagList pendingConnectionTagList = compound.getTagList(TAG_PENDING, 10); // [1.7.10]
        for (int i = 0; i < pendingConnectionTagList.tagCount(); i++) // [1.7.10] not iterable
        {
            final NBTTagCompound tag = pendingConnectionTagList.getCompoundTagAt(i);
            final int[] pos = BlockPosUtil.read(tag, TAG_POS);
            final PendingConnectionNode colonyConnectionData = new PendingConnectionNode(pos);
            colonyConnectionData.read(tag);
            pendingColonyConnections.put(pos, colonyConnectionData);
        }
    }

    @Override
    public NBTTagCompound serializeNBT()
    {
        final NBTTagCompound compound = new NBTTagCompound();
        @NotNull final NBTTagList connectionTagList = new NBTTagList();
        for (@NotNull final ColonyConnectionNode connectionPoint : colonyConnections.values())
        {
            connectionTagList.appendTag(connectionPoint.write()); // [1.7.10] add -> appendTag
        }
        compound.setTag(TAG_CONNECTIONS, connectionTagList);

        @NotNull final NBTTagList connectedColonyTagList = new NBTTagList();
        for (final Map.Entry<Integer, ColonyConnection> entry : directlyConnectedColonies.entrySet())
        {
            connectedColonyTagList.appendTag(entry.getValue().serializeNBT()); // [1.7.10] add -> appendTag
        }
        compound.setTag(TAG_COLONIES, connectedColonyTagList);

        @NotNull final NBTTagList gateHouseTagList = new NBTTagList();
        for (final int[] gateHouse : gateHouses)
        {
            gateHouseTagList.appendTag(BlockPosUtil.write(new NBTTagCompound(), TAG_POS, gateHouse)); // [1.7.10] add -> appendTag
        }
        compound.setTag(TAG_GATEHOUSES, gateHouseTagList);

        @NotNull final NBTTagList connectionEventTagList = new NBTTagList();
        for (final ConnectionEvent connectionEvent : connectionEvents.values())
        {
            connectionEventTagList.appendTag(connectionEvent.serializeNBT()); // [1.7.10] add -> appendTag
        }
        compound.setTag(TAG_CONNECTION_EVENTS, connectionEventTagList);

        @NotNull final NBTTagList pendingConnectionTagList = new NBTTagList();
        for (final PendingConnectionNode connectionEvent : pendingColonyConnections.values())
        {
            pendingConnectionTagList.appendTag(connectionEvent.write()); // [1.7.10] add -> appendTag
        }
        compound.setTag(TAG_PENDING, pendingConnectionTagList);
        return compound;
    }

    @Override
    public void triggerConnectionEvent(final ConnectionEvent connectionEventData)
    {
        final int originColonyId = connectionEventData.id;
        final IColony originColony = IColonyManager.getInstance().getColonyByDimension(originColonyId, colony.getDimension());
        if (originColony == null)
        {
            return;
        }

        connectionEvents.put(connectionEventData.id, connectionEventData);
        final ColonyConnection connectedColonyData;
        final TreeMap<Integer, ColonyConnection> affectedMap;
        if (directlyConnectedColonies.containsKey(originColonyId))
        {
            connectedColonyData = directlyConnectedColonies.get(originColonyId);
            affectedMap = directlyConnectedColonies;
        }
        else if (indirectlyConnectedColoniesCache.containsKey(originColonyId))
        {
            connectedColonyData = indirectlyConnectedColoniesCache.get(originColonyId);
            affectedMap = indirectlyConnectedColoniesCache;
        }
        else
        {
            return;
        }

        final DiplomacyStatus diplomacyStatus = switch (connectionEventData.connectionEventType)
        {
            case ALLY_CONFIRMED -> DiplomacyStatus.ALLIES;
            case FEUD_STARTED -> DiplomacyStatus.HOSTILE;
            case NEUTRAL_SET -> DiplomacyStatus.NEUTRAL;
            default -> connectedColonyData.diplomacyStatus;
        };

        affectedMap.put(originColonyId, new ColonyConnection(originColonyId, originColony.getName(), connectedColonyData.pos, diplomacyStatus));

        final ColonyConnection originConnectedColonyData;
        final TreeMap<Integer, ColonyConnection> originAffectedMap;
        final IColonyConnectionManager originColonyConnectionManager = originColony.getConnectionManager();
        if (originColonyConnectionManager.getDirectlyConnectedColonies().containsKey(colony.getID()))
        {
            originConnectedColonyData = originColonyConnectionManager.getDirectlyConnectedColonies().get(colony.getID());
            originAffectedMap = originColonyConnectionManager.getDirectlyConnectedColonies();
        }
        else if (originColonyConnectionManager.getIndirectlyConnectedColonies().containsKey(colony.getID()))
        {
            originConnectedColonyData = originColonyConnectionManager.getIndirectlyConnectedColonies().get(colony.getID());
            originAffectedMap = originColonyConnectionManager.getIndirectlyConnectedColonies();
        }
        else
        {
            return;
        }

        originAffectedMap.put(colony.getID(), new ColonyConnection(colony.getID(), colony.getName(), originConnectedColonyData.pos, diplomacyStatus));
        originColony.markDirty();
        colony.markDirty();
    }

    @Override
    public List<ConnectionEvent> getConnectionEvents()
    {
        return new ArrayList<>(connectionEvents.values());
    }

    @Override
    public DiplomacyStatus getColonyDiplomacyStatus(final int id)
    {
        if (directlyConnectedColonies.containsKey(id))
        {
            return directlyConnectedColonies.get(id).diplomacyStatus;
        }
        else if (indirectlyConnectedColoniesCache.containsKey(id))
        {
            return indirectlyConnectedColoniesCache.get(id).diplomacyStatus;
        }
        return DiplomacyStatus.NEUTRAL;
    }
}







