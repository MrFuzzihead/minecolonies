package com.minecolonies.core.network.messages.client;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.level.block.state.BlockState;

import com.minecolonies.api.network.IMessage;
// [1.7.10] BlockState -> int metadata
// [1.7.10] client removed (use @SideOnly)
// [1.7.10] client removed (use @SideOnly)
import net.minecraft.network.PacketBuffer;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import net.minecraft.core.particles.ParticleTypes;
// [1.7.10] int[] -> int x,y,z
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

/**
 * Handles the server causing compost particle effects.
 */
public class CompostParticleMessage implements IMessage
{
    /**
     * Random obj for values.
     */
    public static final Random random = new Random();

    /**
     * The position.
     */
    private int[] pos;

    /**
     * Empty constructor used when registering the
     */
    public CompostParticleMessage()
    {
        super();
    }

    /**
     * Sends a message for particle effect.
     *
     * @param pos Coordinates
     */
    public CompostParticleMessage(final int[] pos)
    {
        super();
        this.pos = pos;
    }

    @Override
    public void fromBytes(@NotNull final PacketBuffer buf)
    {
        pos = buf.readBlockPos();
    }

    @Override
    public void toBytes(@NotNull final PacketBuffer buf)
    {
        buf.writeBlockPos(pos);
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
        final ClientLevel world = Minecraft.getInstance().World;
        final int amount = random.nextInt(15) + 1;
        final BlockState state = world.getBlockState(pos);
        double d0;
        double d1;
        double d2;
        if (!state.isAir())
        {
            for (int i = 0; i < amount; ++i)
            {
                d0 = random.nextGaussian() * 0.02D;
                d1 = random.nextGaussian() * 0.02D;
                d2 = random.nextGaussian() * 0.02D;
                world.addParticle(ParticleTypes.HAPPY_VILLAGER,
                  (double) ((float) pos.getX() + random.nextFloat()),
                  (double) pos.getY() + (double) random.nextFloat() * state.getShape(world, pos).bounds().maxY,
                  (double) ((float) pos.getZ() + random.nextFloat()),
                  d0,
                  d1,
                  d2
                );
            }
        }
        else
        {
            for (int i = 0; i < amount; ++i)
            {
                d0 = random.nextGaussian() * 0.02D;
                d1 = random.nextGaussian() * 0.02D;
                d2 = random.nextGaussian() * 0.02D;
                world.addParticle(ParticleTypes.HAPPY_VILLAGER,
                  (double) ((float) pos.getX() + random.nextFloat()),
                  (double) pos.getY() + (double) random.nextFloat() * 1.0D,
                  (double) ((float) pos.getZ() + random.nextFloat()),
                  d0,
                  d1,
                  d2
                );
            }
        }
    }
}



