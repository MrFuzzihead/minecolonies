package com.minecolonies.core.network.messages.client.colony;

import com.minecolonies.api.entity.citizen.AbstractCivilianEntity;
import com.minecolonies.api.network.IMessage;
import com.minecolonies.api.sounds.SoundManager;
// [1.7.10] client removed (use @SideOnly)
// [1.7.10] int[] -> int x,y,z
// [1.7.10] Registries removed
import net.minecraft.network.PacketBuffer;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
// [1.7.10] int /* ResourceKey */ -> int dimensionId
import net.minecraft.util.ResourceLocation;
// [1.7.10] sounds removed
// [1.7.10] sounds removed
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
// [1.7.10] registries removed
import org.jetbrains.annotations.Nullable;

import static com.minecolonies.api.util.SoundUtils.PITCH;
import static com.minecolonies.api.util.SoundUtils.VOLUME;

/**
 * Play sounds at a citizen for a certain amount of time, sequentially
 */
public class PlaySoundForCitizenMessage implements IMessage
{
    /**
     * The colony id of the citizen.
     */
    private int entityid;

    /**
     * The sound event to play.
     */
    private SoundEvent soundEvent;

    /**
     * The sound source to use.
     */
    private SoundSource soundSource;

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
     * Pitch to use.
     */
    private float pitch;

    /**
     * Length of the audio in ticks.
     */
    private int length;

    /**
     * Number of repetitions in ticks.
     */
    private int repetitions;

    /**
     * Default constructor.
     */
    public PlaySoundForCitizenMessage()
    {
        super();
    }

    /**
     * Play a sound for a certain citizen.
     * @param entityID the entity id.
     * @param event the sound event to place.
     * @param pos the position to play it at.
     * @param world the world to play it in.
     */
    public PlaySoundForCitizenMessage(final int entityID, final SoundEvent event, final int[] pos, final World world)
    {
        this(entityID, event, SoundSource.NEUTRAL, pos, world, (float) VOLUME, (float) PITCH, 1, 1);
    }

    /**
     * Play a sound for a certain citizen.
     * @param entityID the entity id.
     * @param event the sound event to place.
     * @param soundSource the type of source.
     * @param pos the position to play it at.
     * @param world the world to play it in.
     */
    public PlaySoundForCitizenMessage(final int entityID, final SoundEvent event, final SoundSource soundSource, final int[] pos, final World world)
    {
        this(entityID, event, soundSource, pos, world, (float) VOLUME, (float) PITCH, 1, 1);
    }

    /**
     * Play a sound for a certain citizen.
     * @param entityID the entity id.
     * @param event the sound event to place.
     * @param soundSource the type of source.
     * @param pos the position to play it at.
     * @param world the world to play it in.
     * @param length the length of the music.
     * @param repetitions the number of repetitions.
     */
    public PlaySoundForCitizenMessage(final int entityID, final SoundEvent event, final SoundSource soundSource, final int[] pos, final World world, final int length, final int repetitions)
    {
        this(entityID, event, soundSource, pos, world, (float) VOLUME, (float) PITCH, length, repetitions);
    }

    /**
     * Play a sound for a certain citizen.
     * @param entityID the entity id.
     * @param event the sound event to place.
     * @param soundSource the type of source.
     * @param pos the position to play it at.
     * @param world the world to play it in.
     * @param volume the volume.
     * @param pitch the pitch.
     * @param length the length of the music.
     * @param repetitions the number of repetitions.
     */
    public PlaySoundForCitizenMessage(final int entityID, final SoundEvent event, final SoundSource soundSource, final int[] pos, final World world, final float volume, final float pitch, final int length, final int repetitions)
    {
        super();
        this.entityid = entityID;
        this.soundEvent = event;
        this.soundSource = soundSource;
        this.pos = pos;
        this.dimensionID = world.dimension();
        this.volume = volume;
        this.pitch = pitch;
        this.length = length;
        this.repetitions = repetitions;
    }

    @Override
    public void toBytes(final PacketBuffer buf)
    {
        buf.writeResourceLocation(this.soundEvent.getLocation());
        buf.writeInt(soundSource.ordinal());
        buf.writeBlockPos(pos);
        buf.writeUtf(dimensionID.location().toString());
        buf.writeFloat(volume);
        buf.writeFloat(pitch);
        buf.writeInt(length);
        buf.writeInt(repetitions);
        buf.writeInt(entityid);
    }

    @Override
    public void fromBytes(final PacketBuffer buf)
    {
        this.soundEvent = ForgeRegistries.SOUND_EVENTS.getValue(buf.readResourceLocation());
        this.soundSource = SoundSource.values()[buf.readInt()];
        this.pos = buf.readBlockPos();
        this.dimensionID = ResourceKey.create(Registries.DIMENSION, new ResourceLocation(buf.readUtf(32767)));
        this.volume = buf.readFloat();
        this.pitch = buf.readFloat();
        this.length = buf.readInt();
        this.repetitions = buf.readInt();
        this.entityid = buf.readInt();
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
        final Entity entity = Minecraft.getInstance().World.getEntity(this.entityid);
        if (entity instanceof AbstractCivilianEntity)
        {
            SoundManager.addToQueue(entity.getUUID(), this.soundEvent, this.soundSource, this.repetitions, this.length, this.pos, this.volume, this.pitch);
        }
    }
}




