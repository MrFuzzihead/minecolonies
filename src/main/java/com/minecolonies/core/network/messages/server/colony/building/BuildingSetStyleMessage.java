package com.minecolonies.core.network.messages.server.colony.building;
import net.minecraft.network.chat.Style;

import com.minecolonies.api.colony.IColony;
import com.minecolonies.api.colony.buildings.IBuilding;
import com.minecolonies.api.colony.buildings.views.IBuildingView;
import com.minecolonies.core.network.messages.server.AbstractBuildingServerMessage;
import net.minecraft.network.PacketBuffer;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import org.jetbrains.annotations.NotNull;

/**
 * Message to set the style of a building.
 */
public class BuildingSetStyleMessage extends AbstractBuildingServerMessage<IBuilding>
{
    /**
     * The style to set.
     */
    private String structurePack;

    /**
     * Empty constructor used when registering the
     */
    public BuildingSetStyleMessage()
    {
        super();
    }

    /**
     * Creates object for the style of a building.
     *
     * @param building View of the building to read data from.
     * @param structurePack    style of the building.
     */
    public BuildingSetStyleMessage(@NotNull final IBuildingView building, final String structurePack)
    {
        super(building);
        this.structurePack = structurePack;
    }

    @Override
    public void fromBytesOverride(@NotNull final PacketBuffer buf)
    {
        structurePack = buf.readUtf(32767);
    }

    @Override
    public void toBytesOverride(@NotNull final PacketBuffer buf)
    {
        buf.writeUtf(structurePack);
    }

    @Override
    public void onExecute(final MessageContext ctx, final boolean isLogicalServer, final IColony colony, final IBuilding building)
    {
        if (building.getBuildingLevel() > 0 && !building.isDeconstructed())
        {
            return;
        }

        building.setStructurePack(structurePack);
    }
}


