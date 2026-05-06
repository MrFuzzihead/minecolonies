package com.minecolonies.api.colony.permissions;
import net.minecraft.world.entity.player.Player;

import com.minecolonies.api.network.PacketUtils;
// [1.7.10] int[] -> int x,y,z
import net.minecraft.network.PacketBuffer;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.UUID;

/**
 * Permission event class, used to store events happening in the colony.
 */
public class PermissionEvent
{
    /**
     * Player UUID.
     */
    @Nullable
    private final UUID id;

    /**
     * Player name.
     */
    private final String name;

    /**
     * Action happening.
     */
    private final Action action;

    /**
     * Impact permission.
     */
    private final int[] position;

    /**
     * Constructor for permission events.
     *
     * @param id       the player UUID.
     * @param name     the player name.
     * @param action   the action happening.
     * @param position the position of the action.
     */
    public PermissionEvent(final UUID id, final String name, final Action action, final int[] position)
    {
        this.id = id;
        this.name = name;
        this.action = action;
        this.position = position;
    }

    /**
     * Constructor for permission events. to load them from a ByteBuf.
     *
     * @param buf the ByteBuf.
     */
    public PermissionEvent(final PacketBuffer buf)
    {
        final UUID uuid = PacketUtils.readUUID(buf);
        if (uuid.equals(UUID.fromString("1-2-3-4-5")))
        {
            this.id = null;
        }
        else
        {
            this.id = uuid;
        }
        try
        {
            this.name = buf.readStringFromBuffer(32767);
            this.action = Action.valueOf(buf.readStringFromBuffer(32767));
        }
        catch (java.io.IOException e)
        {
            throw new RuntimeException(e);
        }
        this.position = new int[]{buf.readInt(), buf.readInt(), buf.readInt()};
    }

    /**
     * The UUID of the player causing the event.
     *
     * @return the UUID.
     */
    @Nullable
    public UUID getId()
    {
        return id;
    }

    /**
     * The name of the player causing the event.
     *
     * @return the name String.
     */
    public String getName()
    {
        return name;
    }

    /**
     * The action causing the event.
     *
     * @return the Action
     */
    public Action getAction()
    {
        return action;
    }

    /**
     * The position at which the event had happened.
     *
     * @return the int[].
     */
    public int[] getPosition()
    {
        return position;
    }

    /**
     * Serialize the permission event to a ByteBuf.
     *
     * @param buf the buffer.
     */
    public void serialize(final PacketBuffer buf)
    {
        if (id == null)
        {
            PacketUtils.writeUUID(buf, UUID.fromString("1-2-3-4-5"));
        }
        else
        {
            PacketUtils.writeUUID(buf, id);
        }
        try
        {
            buf.writeStringToBuffer(name);
            buf.writeStringToBuffer(action.toString());
        }
        catch (java.io.IOException e)
        {
            throw new RuntimeException(e);
        }
        buf.writeInt(position[0]); buf.writeInt(position[1]); buf.writeInt(position[2]);
    }

    @Override
    public boolean equals(final Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (o == null || getClass() != o.getClass())
        {
            return false;
        }
        final PermissionEvent that = (PermissionEvent) o;
        return Objects.equals(id, that.id) &&
                 Objects.equals(name, that.name) &&
                 action == that.action &&
                 Objects.equals(position, that.position);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(id, name, action, position);
    }
}


