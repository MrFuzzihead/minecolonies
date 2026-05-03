package com.minecolonies.core.network.messages.server.colony.building.enchanter;

import com.minecolonies.api.colony.IColony;
import com.minecolonies.api.colony.buildings.views.IBuildingView;
import com.minecolonies.core.colony.buildings.modules.EnchanterStationsModule;
import com.minecolonies.core.colony.buildings.workerbuildings.BuildingEnchanter;
import com.minecolonies.core.network.messages.server.AbstractBuildingServerMessage;
import net.minecraft.network.PacketBuffer;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
// [1.7.10] int[] -> int x,y,z
import org.jetbrains.annotations.NotNull;

/**
 * Message to set add or remove a worker to gather from.
 */
public class EnchanterWorkerSetMessage extends AbstractBuildingServerMessage<BuildingEnchanter>
{
    /**
     * The worker to add/remove.
     */
    private int[] worker;

    /**
     * true if add, false if remove.
     */
    private boolean add;

    /**
     * Empty constructor used when registering the
     */
    public EnchanterWorkerSetMessage()
    {
        super();
    }

    /**
     * Create the enchanter worker
     *
     * @param building the building of the enchanter.
     * @param worker   the worker to add/remove.
     * @param add      true if add, else false
     */
    public EnchanterWorkerSetMessage(@NotNull final IBuildingView building, final int[] worker, final boolean add)
    {
        super(building);
        this.worker = worker;
        this.add = add;
    }

    @Override
    public void fromBytesOverride(@NotNull final PacketBuffer buf)
    {
        worker = buf.readBlockPos();
        add = buf.readBoolean();
    }

    @Override
    public void toBytesOverride(@NotNull final PacketBuffer buf)
    {
        buf.writeBlockPos(worker);
        buf.writeBoolean(add);
    }

    @Override
    protected void onExecute(final MessageContext ctx, final boolean isLogicalServer, final IColony colony, final BuildingEnchanter building)
    {
        if (add)
        {
            building.getFirstModuleOccurance(EnchanterStationsModule.class).addWorker(worker);
        }
        else
        {
            building.getFirstModuleOccurance(EnchanterStationsModule.class).removeWorker(worker);
        }
    }
}


