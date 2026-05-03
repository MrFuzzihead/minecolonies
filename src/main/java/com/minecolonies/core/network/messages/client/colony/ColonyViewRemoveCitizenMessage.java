package com.minecolonies.core.network.messages.client.colony;

import com.minecolonies.api.colony.IColonyManager;
import com.minecolonies.api.network.IMessage;
import com.minecolonies.core.colony.Colony;
// [1.7.10] client removed (use @SideOnly)
import net.minecraft.network.PacketBuffer;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Add or Update a ColonyView on the client.
 */
public class ColonyViewRemoveCitizenMessage implements IMessage
{
    private int colonyId;
    private int citizenId;

    /**
     * Empty constructor used when registering the
     */
    public ColonyViewRemoveCitizenMessage()
    {
        super();
    }

    /**
     * Creates an object for the remove message for citizen.
     *
     * @param colony  Colony the citizen is in.
     * @param citizen Citizen ID.
     */
    public ColonyViewRemoveCitizenMessage(@NotNull final Colony colony, final int citizen)
    {
        this.colonyId = colony.getID();
        this.citizenId = citizen;
    }

    @Override
    public void fromBytes(@NotNull final PacketBuffer buf)
    {
        colonyId = buf.readInt();
        citizenId = buf.readInt();
    }

    @Override
    public void toBytes(@NotNull final PacketBuffer buf)
    {
        buf.writeInt(colonyId);
        buf.writeInt(citizenId);
    }

    @Nullable
    @Override
    public Boolean getExecutionSide()
    {
        return Boolean.FALSE;
    }

    @Override
    public void onExecute(final MessageContext ctx, final boolean isLogicalServer)
    {
        if (Minecraft.getInstance().World != null)
        {
            IColonyManager.getInstance().handleColonyViewRemoveCitizenMessage(colonyId, citizenId, Minecraft.getInstance().World.dimension());
        }
    }
}



