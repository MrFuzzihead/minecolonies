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
 * Handles spawning item particle effects in a circle around a target.
 */
public class CircleParticleEffectMessage implements IMessage
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
    private double posX;
    private double posY;
    private double posZ;

    /**
     * The current stage of the circle.
     */
    private int stage;

    /**
     * Empty constructor used when registering the
     */
    public CircleParticleEffectMessage()
    {
        super();
    }

    /**
     * Start a circling particle effect.
     *
     * @param pos   the position.
     * @param type  the particle type.
     * @param stage the stage.
     */
    public CircleParticleEffectMessage(final Vec3 pos, final SimpleParticleType type, final int stage)
    {
        super();
        this.posX = pos.x;
        this.posY = pos.y - 0.5;
        this.posZ = pos.z;
        this.stage = stage;
        this.type = type;
    }

    @Override
    public void fromBytes(@NotNull final PacketBuffer buf)
    {
        this.posX = buf.readDouble();
        this.posY = buf.readDouble();
        this.posZ = buf.readDouble();
        this.stage = buf.readInt();
        this.type = (SimpleParticleType) ForgeRegistries.PARTICLE_TYPES.getValue(buf.readResourceLocation());
    }

    @Override
    public void toBytes(@NotNull final PacketBuffer buf)
    {
        buf.writeDouble(this.posX);
        buf.writeDouble(this.posY);
        buf.writeDouble(this.posZ);
        buf.writeInt(this.stage);
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

        double x = 1.0 * Math.cos(stage * 45.0) + posX;
        double z = 1.0 * Math.sin(stage * 45.0) + posZ;

        for (int i = 0; i < 5; ++i)
        {
            final Vec3 randomPos = new Vec3(RAND.nextDouble() * 0.1D + 0.1D, RAND.nextDouble() * 0.1D + 0.1D, RAND.nextDouble() * 0.1D + 0.1D);
            final Vec3 randomOffset = new Vec3((RAND.nextDouble() - 0.5D) * 0.1D, (RAND.nextDouble() - 0.5D) * 0.1D, (RAND.nextDouble() - 0.5D) * 0.1D);
            world.addParticle(type,
              x + randomOffset.x,
              posY + randomOffset.y,
              z + randomOffset.z,
              randomPos.x,
              randomPos.y + 0.05D,
              randomPos.z);
        }
    }
}




