package com.minecolonies.core.network.messages.client;

import com.minecolonies.api.network.IMessage;
import com.minecolonies.core.client.gui.WindowBuildDecoration;
// [1.7.10] int[] -> int x,y,z
import net.minecraft.network.PacketBuffer;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import net.minecraft.world.Mirror;
import net.minecraft.world.Rotation;
import org.jetbrains.annotations.NotNull;

/**
 * Message to open the build window, for example for decorations.
 */
public abstract class OpenBuildWindowMessage implements IMessage
{
    /**
     * Town hall position to create building on.
     */
    protected int[] pos;

    /**
     * The colony name.
     */
    protected String path;

    /**
     * The structure pack name.
     */
    protected String packName;

    /**
     * The rotation.
     */
    protected Rotation rotation;

    /**
     * If mirrored.
     */
    protected boolean mirror;

    protected OpenBuildWindowMessage()
    {
        super();
    }

    /**
     * Create a new message.
     *
     * @param pos      the position the deco will be anchored at.
     * @param packName the pack of the deco.
     * @param path     the path in the pack.
     */
    protected OpenBuildWindowMessage(final int[] pos, final String packName, final String path, final Rotation rotation, final Mirror mirror)
    {
        this.pos = pos;
        this.path = path;
        this.packName = packName;
        this.rotation = rotation;
        this.mirror = mirror != Mirror.NONE;
    }

    @Override
    public void toBytes(final PacketBuffer buf)
    {
        buf.writeBlockPos(this.pos);
        buf.writeUtf(this.path);
        buf.writeUtf(this.packName);
        buf.writeBoolean(this.mirror);
        buf.writeInt(this.rotation.ordinal());
    }

    @Override
    public void fromBytes(final PacketBuffer buf)
    {
        this.pos = buf.readBlockPos();
        this.path = buf.readUtf(32767);
        this.packName = buf.readUtf(32767);
        this.mirror = buf.readBoolean();
        this.rotation = Rotation.values()[buf.readInt()];
    }

    @Override
    public final @NotNull Boolean getExecutionSide()
    {
        return Boolean.FALSE;
    }

    @Override
    public final void onExecute(final MessageContext ctx, final boolean isLogicalServer)
    {
        new WindowBuildDecoration(this.pos, this.packName, this.path, this.rotation, this.mirror, this::createWorkOrderMessage).open();
    }

    protected abstract IMessage createWorkOrderMessage(int[] builder);
}


