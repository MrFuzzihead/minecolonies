package com.minecolonies.core.network.messages.client.colony;

import com.minecolonies.api.network.IMessage;
// [1.7.10] client removed (use @SideOnly)
// [1.7.10] Registries removed
import net.minecraft.network.PacketBuffer;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
// [1.7.10] int /* ResourceKey */ -> int dimensionId
import net.minecraft.util.ResourceLocation;
// [1.7.10] sounds removed
// [1.7.10] sounds removed
// [1.7.10] int[] -> int x,y,z
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
// [1.7.10] registries removed
import org.jetbrains.annotations.Nullable;

/**
 * Asks the client to play a specific music
 */
public class PlayMusicAtPosMessage implements IMessage
{
    /**
     * The sound event to play.
     */
    private SoundEvent soundEvent;

    /**
     * The position to play at
     */
    private int[] pos;

    /**
     * The dimension id to play in
     */
    private int /* ResourceKey */ dimensionID;

    /**
     * The volume to use
     */
    private float volume;

    /**
     * pitch to use
     */
    private float pitch;

    /**
     * Default constructor.
     */
    public PlayMusicAtPosMessage()
    {
        super();
    }

    /**
     * Create a play music message with a specific sound event.
     *
     * @param event the sound event.
     */
    public PlayMusicAtPosMessage(final SoundEvent event, final int[] pos, final World world, final float volume, final float pitch)
    {
        super();
        this.soundEvent = event;
        this.pos = pos;
        this.dimensionID = world.dimension();
        this.volume = volume;
        this.pitch = pitch;
    }

    @Override
    public void toBytes(final PacketBuffer buf)
    {
        buf.writeResourceLocation(ForgeRegistries.SOUND_EVENTS.getKey(this.soundEvent));
        buf.writeBlockPos(pos);
        buf.writeUtf(dimensionID.location().toString());
        buf.writeFloat(volume);
        buf.writeFloat(pitch);
    }

    @Override
    public void fromBytes(final PacketBuffer buf)
    {
        this.soundEvent = ForgeRegistries.SOUND_EVENTS.getValue(buf.readResourceLocation());
        this.pos = buf.readBlockPos();
        this.dimensionID = ResourceKey.create(Registries.DIMENSION, new ResourceLocation(buf.readUtf(32767)));
        this.volume = buf.readFloat();
        this.pitch = buf.readFloat();
    }

    @Nullable
    @Override
    public Boolean getExecutionSide()
    {
        return Boolean.FALSE;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void onExecute(final MessageContext ctx, final boolean isLogicalServer)
    {
        if (Minecraft.getInstance().World.dimension() == dimensionID)
        {
            Minecraft.getInstance().World.playSound(Minecraft.getInstance().player, pos.getX(), pos.getY(), pos.getZ(), soundEvent, SoundSource.AMBIENT, volume, pitch);
        }
    }
}




