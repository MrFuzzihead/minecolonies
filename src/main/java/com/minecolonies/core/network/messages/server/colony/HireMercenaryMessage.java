package com.minecolonies.core.network.messages.server.colony;

import com.minecolonies.api.colony.IColony;
import com.minecolonies.api.colony.permissions.Action;
import com.minecolonies.core.entity.mobs.EntityMercenary;
import com.minecolonies.core.network.messages.server.AbstractColonyServerMessage;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
// [1.7.10] sounds removed
import org.jetbrains.annotations.Nullable;

/**
 * The message sent when activating mercenaries
 */
public class HireMercenaryMessage extends AbstractColonyServerMessage
{
    public HireMercenaryMessage()
    {
    }

    public HireMercenaryMessage(final IColony colony)
    {
        super(colony);
    }

    @Nullable
    @Override
    public Action permissionNeeded()
    {
        return super.permissionNeeded();
    }

    @Override
    protected void onExecute(final MessageContext ctx, final boolean isLogicalServer, final IColony colony)
    {
        final Player player = ctx.getServerHandler().playerEntity;
        if (player == null)
        {
            return;
        }

        EntityMercenary.spawnMercenariesInColony(colony);
        colony.getWorld()
          .playLocalSound(player.getX(), player.getY(), player.getZ(), SoundEvents.ILLUSIONER_CAST_SPELL, null, 1.0f, 1.0f, true);
    }

    @Override
    protected void toBytesOverride(final PacketBuffer buf)
    {

    }

    @Override
    protected void fromBytesOverride(final PacketBuffer buf)
    {

    }
}



