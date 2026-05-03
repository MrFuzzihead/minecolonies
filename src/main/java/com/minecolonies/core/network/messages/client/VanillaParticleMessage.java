package com.minecolonies.core.network.messages.client;

import com.minecolonies.api.network.IMessage;
// [1.7.10] client removed (use @SideOnly)
// [1.7.10] client removed (use @SideOnly)
import net.minecraft.network.PacketBuffer;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
// [1.7.10] registries removed
import org.jetbrains.annotations.Nullable;

import java.util.Random;

import static com.minecolonies.api.util.constant.CitizenConstants.CITIZEN_HEIGHT;
import static com.minecolonies.api.util.constant.CitizenConstants.CITIZEN_WIDTH;

/**
 * Message for vanilla particles around a citizen, in villager-like shape.
 */
public class VanillaParticleMessage implements IMessage
{
    /**
     * Citizen Position
     */
    private double x;
    private double y;
    private double z;

    /**
     * Particle id
     */
    private SimpleParticleType type;

    public VanillaParticleMessage() {super();}

    public VanillaParticleMessage(final double x, final double y, final double z, final SimpleParticleType type)
    {
        this.x = x;
        this.y = y;
        this.z = z;
        this.type = type;
    }

    @Override
    public void fromBytes(final PacketBuffer byteBuf)
    {
        x = byteBuf.readDouble();
        y = byteBuf.readDouble();
        z = byteBuf.readDouble();
        this.type = (SimpleParticleType) ForgeRegistries.PARTICLE_TYPES.getValue(byteBuf.readResourceLocation());
    }

    @Override
    public void toBytes(final PacketBuffer byteBuf)
    {
        byteBuf.writeDouble(x);
        byteBuf.writeDouble(y);
        byteBuf.writeDouble(z);
        byteBuf.writeResourceLocation(ForgeRegistries.PARTICLE_TYPES.getKey(this.type));
    }

    @Nullable
    @Override
    public Boolean getExecutionSide()
    {
        return Boolean.FALSE;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void onExecute(final MessageContext ctx, final boolean isLogicalServer)
    {
        final ClientLevel world = Minecraft.getInstance().World;

        spawnParticles(type, world, x, y, z);
    }

    /**
     * Spawns the given particle randomly around the position.
     *
     * @param particleType praticle to spawn
     * @param world        world to use
     * @param x            x pos
     * @param y            y pos
     * @param z            z pos
     */
    private void spawnParticles(SimpleParticleType particleType, World world, double x, double y, double z)
    {
        final Random rand = new Random();
        for (int i = 0; i < 5; ++i)
        {
            double d0 = rand.nextGaussian() * 0.02D;
            double d1 = rand.nextGaussian() * 0.02D;
            double d2 = rand.nextGaussian() * 0.02D;
            world.addParticle(particleType,
              x + (rand.nextFloat() * CITIZEN_WIDTH * 2.0F) - CITIZEN_WIDTH,
              y + 1.0D + (rand.nextFloat() * CITIZEN_HEIGHT),
              z + (rand.nextFloat() * CITIZEN_WIDTH * 2.0F) - CITIZEN_WIDTH,
              d0,
              d1,
              d2);
        }
    }
}




