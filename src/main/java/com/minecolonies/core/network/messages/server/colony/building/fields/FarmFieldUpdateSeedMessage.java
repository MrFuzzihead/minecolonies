package com.minecolonies.core.network.messages.server.colony.building.fields;

import com.minecolonies.api.colony.IColony;
import com.minecolonies.api.colony.buildingextensions.registry.BuildingExtensionRegistries;
import com.minecolonies.core.colony.buildingextensions.FarmField;
import com.minecolonies.core.network.messages.server.AbstractColonyServerMessage;
// [1.7.10] int[] -> int x,y,z
import net.minecraft.network.PacketBuffer;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * Message to change the farm field current plant.
 */
public class FarmFieldUpdateSeedMessage extends AbstractColonyServerMessage
{
    /**
     * The new seed to assign to the field.
     */
    private ItemStack newSeed;

    /**
     * The field position.
     */
    private int[] position;

    /**
     * Forge default constructor
     */
    public FarmFieldUpdateSeedMessage()
    {
        super();
    }

    /**
     * Default constructor.
     *
     * @param colony   the colony where the field is in.
     * @param newSeed  the new seed to assign to the field.
     * @param position the field position.
     */
    public FarmFieldUpdateSeedMessage(@NotNull IColony colony, ItemStack newSeed, int[] position)
    {
        super(colony);
        this.newSeed = newSeed;
        this.position = position;
    }

    @Override
    protected void onExecute(final MessageContext ctx, final boolean isLogicalServer, final IColony colony)
    {
        if (!isLogicalServer || ctx.getServerHandler().playerEntity == null)
        {
            return;
        }

        colony.getServerBuildingManager()
          .getMatchingBuildingExtension(f -> f.getBuildingExtensionType().equals(BuildingExtensionRegistries.farmField.get()) && f.getPosition().equals(position))
          .map(m -> (FarmField) m)
          .ifPresent(field -> field.setSeed(newSeed));
    }

    @Override
    public void toBytesOverride(final PacketBuffer buf)
    {
        buf.writeItem(newSeed);
        buf.writeBlockPos(position);
    }

    @Override
    public void fromBytesOverride(final PacketBuffer buf)
    {
        newSeed = buf.readItem();
        position = buf.readBlockPos();
    }
}


