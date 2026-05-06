package com.minecolonies.core.network.messages.client;

import com.minecolonies.api.network.IMessage;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.World;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

import static com.minecolonies.api.util.constant.CitizenConstants.CITIZEN_HEIGHT;
import static com.minecolonies.api.util.constant.CitizenConstants.CITIZEN_WIDTH;

/**
 * Message for vanilla particles around a citizen, in villager-like shape.
 * [1.7.10] Uses string-based particle names (world.spawnParticle).
 */
public class VanillaParticleMessage implements IMessage
{
    private double x;
    private double y;
    private double z;
    private String particleName;

    public VanillaParticleMessage() { super(); }

    public VanillaParticleMessage(final double x, final double y, final double z, final String particleName)
    {
        this.x = x;
        this.y = y;
        this.z = z;
        this.particleName = particleName;
    }

    @Override
    public void fromBytes(final PacketBuffer byteBuf)
    {
        x = byteBuf.readDouble();
        y = byteBuf.readDouble();
        z = byteBuf.readDouble();
        try { particleName = byteBuf.readStringFromBuffer(256); } catch (java.io.IOException e) { particleName = "happyVillager"; }
    }

    @Override
    public void toBytes(final PacketBuffer byteBuf)
    {
        byteBuf.writeDouble(x);
        byteBuf.writeDouble(y);
        byteBuf.writeDouble(z);
        try { byteBuf.writeStringToBuffer(particleName != null ? particleName : "happyVillager"); } catch (java.io.IOException e) { /* ignore */ }
    }

    @Nullable
    @Override
    public Boolean getExecutionSide()
    {
        return Boolean.FALSE;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void onExecute(final MessageContext ctx, final boolean isLogicalServer)
    {
        final World world = net.minecraft.client.Minecraft.getMinecraft().theWorld;
        if (world == null || particleName == null) return;
        final Random rand = new Random();
        for (int i = 0; i < 5; ++i)
        {
            double d0 = rand.nextGaussian() * 0.02D;
            double d1 = rand.nextGaussian() * 0.02D;
            double d2 = rand.nextGaussian() * 0.02D;
            world.spawnParticle(particleName,
              x + (rand.nextFloat() * CITIZEN_WIDTH * 2.0F) - CITIZEN_WIDTH,
              y + 1.0D + (rand.nextFloat() * CITIZEN_HEIGHT),
              z + (rand.nextFloat() * CITIZEN_WIDTH * 2.0F) - CITIZEN_WIDTH,
              d0, d1, d2);
        }
    }
}
