package com.minecolonies.core.network.messages.client;

import com.minecolonies.api.network.IMessage;
import com.minecolonies.apiimp.initializer.ModParticleTypesInitializer;
// [1.7.10] client removed (use @SideOnly)
import net.minecraft.network.PacketBuffer;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import org.jetbrains.annotations.Nullable;

/**
 * Message for sleeping particles
 */
public class SleepingParticleMessage implements IMessage
{
    /**
     * Position the particles spawn at
     */
    private double x;
    private double y;
    private double z;

    public SleepingParticleMessage()
    {
        super();
    }

    public SleepingParticleMessage(final double x, final double y, final double z)
    {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public void fromBytes(final PacketBuffer byteBuf)
    {
        x = byteBuf.readDouble();
        y = byteBuf.readDouble();
        z = byteBuf.readDouble();
    }

    @Override
    public void toBytes(final PacketBuffer byteBuf)
    {
        byteBuf.writeDouble(x);
        byteBuf.writeDouble(y);
        byteBuf.writeDouble(z);
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
        Minecraft.getInstance().World.addParticle(ModParticleTypesInitializer.SLEEPINGPARTICLE_TYPE,
          x,
          y,
          z,
          1.0f,
          1.0f,
          1.0f);
    }
}



