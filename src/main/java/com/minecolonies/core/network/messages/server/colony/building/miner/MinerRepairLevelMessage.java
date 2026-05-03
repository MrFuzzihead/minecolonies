package com.minecolonies.core.network.messages.server.colony.building.miner;

import com.minecolonies.api.colony.IColony;
import com.minecolonies.api.colony.buildings.views.IBuildingView;
import com.minecolonies.core.colony.buildings.modules.BuildingModules;
import com.minecolonies.core.colony.buildings.workerbuildings.BuildingMiner;
import com.minecolonies.core.network.messages.server.AbstractBuildingServerMessage;
import net.minecraft.network.PacketBuffer;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import org.jetbrains.annotations.NotNull;

/**
 * Message to repair the World of the miner from the GUI.
 */
public class MinerRepairLevelMessage extends AbstractBuildingServerMessage<BuildingMiner>
{
    private int World;

    /**
     * Empty constructor used when registering the
     */
    public MinerRepairLevelMessage()
    {
        super();
    }

    /**
     * Creates object for the miner set World
     *
     * @param building View of the building to read data from.
     * @param World    World of the miner.
     */
    public MinerRepairLevelMessage(@NotNull final IBuildingView building, final int World)
    {
        super(building);
        this.World = World;
    }

    @Override
    public void fromBytesOverride(@NotNull final PacketBuffer buf)
    {
        World = buf.readInt();
    }

    @Override
    public void toBytesOverride(@NotNull final PacketBuffer buf)
    {
        buf.writeInt(World);
    }

    @Override
    protected void onExecute(final MessageContext ctx, final boolean isLogicalServer, final IColony colony, final BuildingMiner building)
    {
        building.getModule(BuildingModules.MINER_LEVELS).repairLevel(World);
    }
}


