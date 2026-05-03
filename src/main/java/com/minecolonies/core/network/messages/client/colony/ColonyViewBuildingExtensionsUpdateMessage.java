package com.minecolonies.core.network.messages.client.colony;

import com.minecolonies.api.colony.IColony;
import com.minecolonies.api.colony.IColonyManager;
import com.minecolonies.api.colony.IColonyView;
import com.minecolonies.api.colony.buildingextensions.IBuildingExtension;
import com.minecolonies.api.network.IMessage;
import com.minecolonies.api.util.Log;
import com.minecolonies.core.colony.buildingextensions.registry.BuildingExtensionDataManager;
import io.netty.buffer.Unpooled;
// [1.7.10] Registries removed
import net.minecraft.network.PacketBuffer;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
// [1.7.10] int /* ResourceKey */ -> int dimensionId
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Update message for auto syncing the entire building extension list.
 */
public class ColonyViewBuildingExtensionsUpdateMessage implements IMessage
{
    /**
     * The colony this building extension belongs to.
     */
    private int colonyId;

    /**
     * Dimension of the colony.
     */
    private int /* ResourceKey */ dimension;

    /**
     * The list of building extension items.
     */
    private Map<IBuildingExtension, IBuildingExtension> extensions;

    /**
     * Empty constructor used when registering the
     */
    public ColonyViewBuildingExtensionsUpdateMessage()
    {
        super();
    }

    /**
     * Creates a message to handle colony all building extension views.
     *
     * @param colony the colony this building extension is in.
     * @param extensions the complete list of building extensions of this colony.
     */
    public ColonyViewBuildingExtensionsUpdateMessage(@NotNull final IColony colony, @NotNull final Collection<IBuildingExtension> extensions)
    {
        super();
        this.colonyId = colony.getID();
        this.dimension = colony.getDimension();
        this.extensions = new HashMap<>();
        extensions.forEach(extension -> this.extensions.put(extension, extension));
    }

    @Override
    public void toBytes(@NotNull final PacketBuffer buf)
    {
        buf.writeInt(colonyId);
        buf.writeUtf(dimension.location().toString());
        buf.writeInt(extensions.size());
        for (final IBuildingExtension extension : extensions.keySet())
        {
            final PacketBuffer buffer = BuildingExtensionDataManager.extensionToBuffer(extension);
            buf.writeInt(buffer.readableBytes());
            buf.writeBytes(buffer);
        }
    }

    @Override
    public void fromBytes(@NotNull final PacketBuffer buf)
    {
        colonyId = buf.readInt();
        dimension = ResourceKey.create(Registries.DIMENSION, new ResourceLocation(buf.readUtf(32767)));
        extensions = new HashMap<>();
        final int extensionCount = buf.readInt();
        for (int i = 0; i < extensionCount; i++)
        {
            final int readableBytes = buf.readInt();
            final PacketBuffer data = new PacketBuffer(Unpooled.buffer(readableBytes));
            buf.readBytes(data, readableBytes);
            final IBuildingExtension extension = BuildingExtensionDataManager.bufferToExtension(data);
            extensions.put(extension, extension);
        }
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
        final IColonyView view = IColonyManager.getInstance().getColonyView(colonyId, dimension);
        if (view != null)
        {
            final Set<IBuildingExtension> extensions = new HashSet<>();
            view.getClientBuildingManager().getBuildingExtensions(extension -> true).forEach(existingExtension -> {
                if (this.extensions.containsKey(existingExtension))
                {
                    final PacketBuffer copyBuffer = new PacketBuffer(Unpooled.buffer());
                    this.extensions.get(existingExtension).serialize(copyBuffer);
                    existingExtension.deserialize(copyBuffer);
                    extensions.add(existingExtension);
                }
            });
            extensions.addAll(this.extensions.keySet());

            view.getClientBuildingManager().handleColonyBuildingExtensionViewUpdateMessage(extensions);
        }
        else
        {
            Log.getLogger().error("Colony view does not exist for ID #{}", colonyId);
        }
    }
}



