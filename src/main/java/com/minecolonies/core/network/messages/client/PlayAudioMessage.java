package com.minecolonies.core.network.messages.client;

import com.minecolonies.api.colony.IColony;
import com.minecolonies.api.network.IMessage;
import com.minecolonies.core.Network;
// [1.7.10] client removed (use @SideOnly)
// [1.7.10] client removed (use @SideOnly)
// [1.7.10] client removed (use @SideOnly)
import net.minecraft.network.PacketBuffer;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import net.minecraft.util.ResourceLocation;
import net.minecraft.entity.player.EntityPlayerMP;
// [1.7.10] sounds removed
// [1.7.10] sounds removed
import java.util.Random;
import net.minecraft.entity.player.EntityPlayer;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Asks the client to play a specific music
 */
public class PlayAudioMessage implements IMessage
{
    /**
     * The sound event to play.
     */
    private ResourceLocation soundEvent;
    private SoundSource      category;

    /**
     * Default constructor.
     */
    public PlayAudioMessage()
    {
        super();
    }

    /**
     * Create a play music message with a specific sound event.
     *
     * @param event the sound event.
     */
    public PlayAudioMessage(final SoundEvent event)
    {
        super();
        this.soundEvent = event.getLocation();
    }

    /**
     * Create a play music message with a specific sound event.
     *
     * @param event the sound event.
     * @param category the sound category to play on
     */
    public PlayAudioMessage(final SoundEvent event, final SoundSource category)
    {
        super();
        this.soundEvent = event.getLocation();
        this.category = category;
    }

    @Override
    public void toBytes(final PacketBuffer buf)
    {
        buf.writeVarInt(category.ordinal());
        buf.writeResourceLocation(soundEvent);
    }

    @Override
    public void fromBytes(final PacketBuffer buf)
    {
        this.category = SoundSource.values()[buf.readVarInt()];
        this.soundEvent = buf.readResourceLocation();
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
        final Player player = Minecraft.getInstance().player;

        if (player == null)
        {
            return;
        }

        Minecraft.getInstance().getSoundManager().play(new SimpleSoundInstance(
          soundEvent, category,
            1.0F, 1.0F, RandomSource.create(), false, 0, SoundInstance.Attenuation.NONE, player.getX(), player.getY(), player.getZ(), true));
    }

    /**
     * Plays a sound event to everyone in the colony
     * @param col the colony
     * @param important if the audio is sent to important message players only
     * @param stop if all other sounds should be stopped first
     * @param messages one or more messages to send to each player.
     */
    public static void sendToAll(IColony col, boolean important, boolean stop, PlayAudioMessage... messages)
    {
        List<Player> players = important
          ? col.getImportantMessageEntityPlayers()
          : col.getMessagePlayerEntities();

        for (Player player : players)
        {
            if (stop)
            {
                Network.getNetwork().sendToPlayer(new StopMusicMessage(), (EntityPlayerMP) player);
            }

            for (PlayAudioMessage pam : messages)
            {
                Network.getNetwork().sendToPlayer(pam, (EntityPlayerMP) player);
            }
        }
    }
}




