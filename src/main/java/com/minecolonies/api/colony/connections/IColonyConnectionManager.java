package com.minecolonies.api.colony.connections;

import com.minecolonies.api.colony.IColony;
// [1.7.10] int[] -> int x,y,z
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
// [1.7.10] INBTSerializable -> manual read/write
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.TreeMap;

import static com.minecolonies.api.util.constant.NbtTagConstants.*;
import static com.minecolonies.api.util.constant.NbtTagConstants.TAG_STATUS;

/**
 * Connection manager interface.
 */
public interface IColonyConnectionManager {


    /**
     * Add a new connection point and connect to neighbors.
     *
     * @param connectionPoint the node position.
     * @return
     */
    boolean addNewConnectionNode(final int[] connectionPoint);

    /**
     * Remove a connection point and update neighbors.
     * @param connectionPoint the node position.
     */
    void removeConnectionNode(final int[] connectionPoint);

    /**
     * Tick to process work.
     */
    void tick();

    /**
     * Get all directly connected colonies.
     * @return the map of directly connected colonies.
     */
    TreeMap<Integer, ColonyConnection> getDirectlyConnectedColonies();


    /**
     * Get all indirectly connected colonies.
     * @return the map of them.
     */
    TreeMap<Integer, ColonyConnection> getIndirectlyConnectedColonies();

    /**
     * Get a connection node.
     *
     * @param int[] its position.
     * @return the node object.
     */
    ColonyConnectionNode getNode(final int[] blockPos);

    /**
     * Add a new gatehouse.
     * @param gateHousePosition the int[].
     */
    void addNewGateHouse(final int[] gateHousePosition);

    /**
     * Remove a gatehouse.
     * @param gateHousePosition the int[].
     */
    void removeGateHouse(final int[] gateHousePosition);

    /**
     * Attempt to establish a connection.
     * @param clickedPos the clicked position.
     * @param targetColony, the colony we're trying to connect to.
     */
    boolean attemptEstablishConnection(final int[] clickedPos, final IColony targetColony);

    /**
     * Serialize connection manager to view.
     * @param buf the buf to serialize it to.
     */
    void serializeToView(@NotNull PacketBuffer buf);

    /**
     * Deserialize connection manager from buffer for client side usage.
     * @param buf the buf to read it from.
     */
    void deserializeFromView(@NotNull PacketBuffer buf);

    /**
     * Trigger a connection event at a colony.
     * @param connectionEventData the source colony data.
     */
    void triggerConnectionEvent(ConnectionEvent connectionEventData);

    /**
     * Get the list of connection events.
     * @return the connection events.
     */
    List<ConnectionEvent> getConnectionEvents();

    /**
     * Get colony diplomacy status by id.
     * @param id the id to query from.
     * @return the diplomacy status.
     */
    DiplomacyStatus getColonyDiplomacyStatus(int id);
}







