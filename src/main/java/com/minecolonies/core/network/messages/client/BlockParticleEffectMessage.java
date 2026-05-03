package com.minecolonies.core.network.messages.client;

import com.minecolonies.api.network.IMessage;
import net.minecraft.block.Block;
// [1.7.10] BlockState -> int metadata
// [1.7.10] client removed (use @SideOnly)
import net.minecraft.network.PacketBuffer;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
// [1.7.10] Direction -> net.minecraft.util.EnumFacing
// [1.7.10] int[] -> int x,y,z
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Handles the server telling nearby clients to render a particle effect. Created: February 10, 2016
 *
 * @author Colton
 */
public class BlockParticleEffectMessage implements IMessage
{
    public static final int BREAK_BLOCK = -1;

    private int[]   pos;
    private BlockState block;
    private int        side;

    /**
     * Empty constructor used when registering the
     */
    public BlockParticleEffectMessage()
    {
        super();
    }

    /**
     * Sends a message for particle effect.
     *
     * @param pos   Coordinates
     * @param state Block State
     * @param side  Side of the block causing effect
     */
    public BlockParticleEffectMessage(final int[] pos, @NotNull final BlockState state, final int side)
    {
        this.pos = pos;
        this.block = state;
        this.side = side;
    }

    @Override
    public void fromBytes(@NotNull final PacketBuffer buf)
    {
        pos = buf.readBlockPos();
        block = Block.stateById(buf.readInt());
        side = buf.readInt();
    }

    @Override
    public void toBytes(@NotNull final PacketBuffer buf)
    {
        buf.writeBlockPos(pos);
        buf.writeInt(Block.getId(block));
        buf.writeInt(side);
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
        if (side == BREAK_BLOCK)
        {
            Minecraft.getInstance().particleEngine.destroy(pos, block);
        }
        else
        {
            Minecraft.getInstance().particleEngine.crack(pos, Direction.from3DDataValue(side));
        }
    }
}



