package com.minecolonies.core.network.messages.client.colony;

import com.minecolonies.api.colony.IColony;
import com.minecolonies.api.colony.IColonyManager;
import com.minecolonies.api.network.IMessage;
import com.minecolonies.core.Network;
import com.minecolonies.core.client.gui.map.WindowColonyMap;
// [1.7.10] int[] -> int x,y,z
import net.minecraft.network.PacketBuffer;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Add or Update a AbstractBuilding.View to a ColonyView on the client.
 */
public class ColonyListMessage implements IMessage
{
    /**
     * List of colonies
     */
    List<IColony>    colonies   = new ArrayList<>();
    List<ColonyInfo> colonyInfo = new ArrayList<>();

    /**
     * Empty constructor used when registering the
     */
    public ColonyListMessage()
    {
        super();
    }

    /**
     * Creates a message to handle colony views.
     */
    public ColonyListMessage(final List<IColony> colonies)
    {
        super();
        this.colonies = colonies;
    }

    @Override
    public void fromBytes(@NotNull final PacketBuffer buf)
    {
        colonyInfo = new ArrayList<>();
        final int count = buf.readInt();
        for (int i = 0; i < count; i++)
        {
            final ColonyInfo info = new ColonyInfo(buf.readInt());
            info.center = buf.readBlockPos();
            info.name = buf.readUtf(32767);
            info.citizencount = buf.readInt();
            info.owner = buf.readUtf(32767);
            info.prestige = buf.readInt();
            colonyInfo.add(info);
        }
    }

    @Override
    public void toBytes(@NotNull final PacketBuffer buf)
    {
        buf.writeInt(colonies.size());
        for (final IColony colony : colonies)
        {
            buf.writeInt(colony.getID());
            buf.writeBlockPos(colony.getCenter());
            buf.writeUtf(colony.getName());
            buf.writeInt(colony.getCitizenManager().getCurrentCitizenCount());
            buf.writeUtf(colony.getPermissions().getOwnerName());
            buf.writeInt(colony.getServerBuildingManager().getColonyPrestige());
        }
    }

    @Override
    public void onExecute(final MessageContext ctx, final boolean isLogicalServer)
    {
        if (!isLogicalServer)
        {
            WindowColonyMap.setColonies(colonyInfo);
        }
        else if (ctx.getServerHandler().playerEntity != null)
        {
            Network.getNetwork().sendToPlayer(new ColonyListMessage(IColonyManager.getInstance().getColonies(ctx.getServerHandler().playerEntity.World)), ctx.getServerHandler().playerEntity);
        }
    }

    public static class ColonyInfo
    {
        private final int      id;
        private       int[] center;
        private       String   name;
        private       int      citizencount;
        private       String   owner;
        private       int      prestige;

        public ColonyInfo(final int id)
        {
            this.id = id;
        }

        public int getId()
        {
            return id;
        }

        public int[] getCenter()
        {
            return center;
        }

        public String getName()
        {
            return name;
        }

        public int getCitizencount()
        {
            return citizencount;
        }

        public String getOwner()
        {
            return owner;
        }

        public int getPrestige() {
            return prestige;
        }
    }
}


