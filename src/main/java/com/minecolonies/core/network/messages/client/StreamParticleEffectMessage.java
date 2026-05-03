package com.minecolonies.core.network.messages.client;

import com.minecolonies.api.network.IMessage;
// [1.7.10] client removed (use @SideOnly)
// [1.7.10] client removed (use @SideOnly)
import net.minecraft.network.PacketBuffer;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import net.minecraft.core.particles.SimpleParticleType;
// [1.7.10] world.phys removed
// [1.7.10] registries removed
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

/**
 * Handles spawning item particle effects in a stream between two targets.
 */
public class StreamParticleEffectMessage implements IMessage
{
    /**
     * Random obj.
     */
    private static final Random RAND = new Random();

    /**
     * The itemStack for the particles.
     */
    private SimpleParticleType type;

    /**
     * The start position.
     */
    private double sPosX;
    private double sPosY;
    private double sPosZ;

    /**
     * The end position.
     */
    private double ePosX;
    private double ePosY;
    private double ePosZ;

    /**
     * The stage of the transfer.
     */
    private int stage;

    /**
     * The max stage of the transfer.
     */
    private int maxStage;

    /**
     * Empty constructor used when registering the
     */
    public StreamParticleEffectMessage()
    {
        super();
    }

    /**
     * Start a particle stream.
     *
     * @param start    the starting position.
     * @param end      the end position.
     * @param type     the particle type.
     * @param stage    the stage we're at
     * @param maxStage the max stage
     */
    public StreamParticleEffectMessage(final Vec3 start, final Vec3 end, final SimpleParticleType type, final int stage, final int maxStage)
    {
        super();
        this.sPosX = start.x;
        this.sPosY = start.y - 0.5;
        this.sPosZ = start.z;

        this.ePosX = end.x;
        this.ePosY = end.y - 0.5;
        this.ePosZ = end.z;

        this.stage = stage;
        this.maxStage = maxStage;

        this.type = type;
    }

    @Override
    public void fromBytes(@NotNull final PacketBuffer buf)
    {
        this.sPosX = buf.readDouble();
        this.sPosY = buf.readDouble();
        this.sPosZ = buf.readDouble();

        this.ePosX = buf.readDouble();
        this.ePosY = buf.readDouble();
        this.ePosZ = buf.readDouble();

        this.stage = buf.readInt();
        this.maxStage = buf.readInt();
        this.type = (SimpleParticleType) ForgeRegistries.PARTICLE_TYPES.getValue(buf.readResourceLocation());
    }

    @Override
    public void toBytes(@NotNull final PacketBuffer buf)
    {
        buf.writeDouble(this.sPosX);
        buf.writeDouble(this.sPosY);
        buf.writeDouble(this.sPosZ);

        buf.writeDouble(this.ePosX);
        buf.writeDouble(this.ePosY);
        buf.writeDouble(this.ePosZ);

        buf.writeInt(this.stage);
        buf.writeInt(this.maxStage);
        buf.writeResourceLocation(ForgeRegistries.PARTICLE_TYPES.getKey(this.type));
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

        final Vec3 end = new Vec3(ePosX, ePosY, ePosZ);

        double xDif = (sPosX - ePosX) / maxStage;
        double yDif = (sPosY - ePosY) / maxStage;
        double zDif = (sPosZ - ePosZ) / maxStage;

        final double curve = maxStage / 3.0;

        for (int step = Math.max(0, stage - 1); step <= Math.min(maxStage, stage + 1); step++)
        {
            double minDif = Math.min(step, Math.abs(step - maxStage)) / curve;

            for (int i = 0; i < 10; ++i)
            {
                final Vec3 randomPos = new Vec3(RAND.nextDouble() * 0.1D + 0.1D, RAND.nextDouble() * 0.1D + 0.1D, RAND.nextDouble() * 0.1D + 0.1D);
                final Vec3 randomOffset = new Vec3((RAND.nextDouble() - 0.5D) * 0.1D, (RAND.nextDouble() - 0.5D) * 0.1D, (RAND.nextDouble() - 0.5D) * 0.1D);
                world.addParticle(type,
                  end.x + randomOffset.x + xDif * step,
                  end.y + randomOffset.y + yDif * step + minDif,
                  end.z + randomOffset.z + zDif * step,
                  randomPos.x,
                  randomPos.y + 0.05D,
                  randomPos.z);
            }
        }
    }
}




