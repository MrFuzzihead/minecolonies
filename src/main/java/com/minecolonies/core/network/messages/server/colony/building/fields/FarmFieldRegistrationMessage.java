package com.minecolonies.core.network.messages.server.colony.building.fields;

import com.minecolonies.api.colony.IColony;
import com.minecolonies.api.colony.buildingextensions.IBuildingExtension;
import com.minecolonies.api.colony.buildingextensions.registry.BuildingExtensionRegistries;
import com.minecolonies.core.colony.buildingextensions.FarmField;
import com.minecolonies.core.network.messages.server.AbstractColonyServerMessage;
// [1.7.10] int[] -> int x,y,z
import net.minecraft.network.PacketBuffer;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

import java.util.Optional;

/**
 * TODO: Remove in 1.20.2
 */
public class FarmFieldRegistrationMessage extends AbstractColonyServerMessage
{
    /**
     * The field position.
     */
    private int[] position;

    /**
     * Forge default constructor
     */
    public FarmFieldRegistrationMessage()
    {
        super();
    }

    /**
     * @param position the field position.
     */
    public FarmFieldRegistrationMessage(IColony colony, int[] position)
    {
        super(colony);
        this.position = position;
    }

    @Override
    public void onExecute(final MessageContext ctx, final boolean isLogicalServer, final IColony colony)
    {
        if (!isLogicalServer || ctx.getServerHandler().playerEntity == null)
        {
            return;
        }

        final Optional<IBuildingExtension> field = colony.getServerBuildingManager()
                                         .getBuildingExtensions(f -> f.getBuildingExtensionType().equals(BuildingExtensionRegistries.farmField.get()) && f.getPosition().equals(position))
                                         .stream()
                                         .findFirst();

        if (field.isEmpty())
        {
            colony.getServerBuildingManager().addBuildingExtension(FarmField.create(position, ctx.getServerHandler().playerEntity.World));
        }
    }

    @Override
    public void toBytesOverride(final PacketBuffer buf)
    {
        buf.writeBlockPos(position);
    }

    @Override
    public void fromBytesOverride(final PacketBuffer buf)
    {
        position = buf.readBlockPos();
    }
}


