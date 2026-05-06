package com.minecolonies.api.util;
import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;

import com.minecolonies.api.colony.ICitizenData;
import com.minecolonies.api.colony.ICivilianData;
import com.minecolonies.api.colony.IVisitorData;
import com.minecolonies.api.colony.jobs.IJob;
import com.minecolonies.api.sounds.EventType;
// [1.7.10] int[] -> int x,y,z
// [1.7.10] Holder removed
// [1.7.10] protocol.game removed
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.sounds.SoundEvent; // [1.7.10] SoundEvent shim
// [1.7.10] SoundSource/SoundEvents removed
import java.util.Random;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
// [1.7.10] forge event removed
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

import static com.minecolonies.api.sounds.ModSoundEvents.CITIZEN_SOUND_EVENTS;

/**
 * Utilities for playing sounds.
 */
public final class SoundUtils
{
    /**
     * Get a random between 1 and 100.
     */
    private static final int ONE_HUNDRED = 100;

    /**
     * Standard pitch value.
     */
    public static final float PITCH = 1.0f;

    /**
     * Random object.
     */
    private static final Random rand = new Random();

    /**
     * Volume to play at.
     */
    public static final double VOLUME = 0.5D;

    /**
     * The base pitch, add more to this to change the sound.
     */
    private static final double BASE_PITCH = 0.8D;

    /**
     * The pitch will be divided by this to calculate it for the arrow sound.
     */
    private static final double PITCH_DIVIDER = 1.0D;

    /**
     * Random is multiplied by this to get a random sound.
     */
    private static final double PITCH_MULTIPLIER = 0.4D;

    /**
     * A much less chaotic scale (D major pentatonic) for random pitches.
     * [1.7.10] Note enum removed; using int indices 0-11 (semitones within octave)
     */
    public static final int[] PENTATONIC = {
      // First Octave: A=9, B=11, D=2, E=4, F#=6
      9, 11, 2, 4, 6,
      // Second Octave
      9, 11, 2
    };

    /**
     * The minimum required distance to play a sound to a player
     */
    private static final double MIN_REQUIRED_SOUND_DIST = 10 * 10;

    /**
     * Private constructor to hide the implicit public one.
     */
    private SoundUtils()
    {
        /*
         * Intentionally left empty.
         */
    }

    /**
     * Play a random sound at the citizen.
     *
     * @param worldIn the world to play it in.
     * @param pos     the pos to play it at.
     * @param citizen the citizen to play it for.
     */
    public static void playRandomSound(@NotNull final World worldIn, @NotNull final int[] pos, @NotNull final ICitizenData citizen)
    {
        boolean playerCloseEnough = false;
        for (final EntityPlayer player : citizen.getColony().getPackageManager().getCloseSubscribers())
        {
            final double dx = player.posX - pos[0];
            final double dy = player.posY - pos[1];
            final double dz = player.posZ - pos[2];
            if (dx * dx + dy * dy + dz * dz < MIN_REQUIRED_SOUND_DIST)
            {
                playerCloseEnough = true;
                break;
            }
        }

        if (!playerCloseEnough)
        {
            return;
        }

        if (citizen.isAsleep())
        {
            playSoundAtCitizenWith(worldIn, pos, EventType.OFF_TO_BED, citizen);
            return;
        }

        final double v = rand.nextDouble();
        if (v <= 0.1)
        {
            if (citizen.getSaturation() < 2)
            {
                playSoundAtCitizenWith(worldIn, pos, EventType.SATURATION_LOW, citizen);
            }
            else
            {
                playSoundAtCitizenWith(worldIn, pos, EventType.SATURATION_HIGH, citizen);
            }
        }
        else if (v <= 0.2)
        {
            if (citizen.getCitizenHappinessHandler().getHappiness(citizen.getColony(), citizen) < 5)
            {
                playSoundAtCitizenWith(worldIn, pos, EventType.UNHAPPY, citizen);
            }
            else
            {
                playSoundAtCitizenWith(worldIn, pos, EventType.HAPPY, citizen);
            }
        }
        else if (v <= 0.3)
        {
            playSoundAtCitizenWith(worldIn, pos, EventType.GENERAL, citizen);
        }
        else if (v <= 0.4 && citizen.getEntity().isPresent() && citizen.getCitizenDiseaseHandler().isSick())
        {
            playSoundAtCitizenWith(worldIn, pos, EventType.SICKNESS, citizen);
        }
        else if (v <= 0.5 && (citizen.getHomeBuilding() == null || citizen.getHomeBuilding().getBuildingLevelEquivalent() <= 2))
        {
            playSoundAtCitizenWith(worldIn, pos, EventType.BAD_HOUSING, citizen);
        }
        else if (v <= 0.6 && worldIn.isRaining())
        {
            playSoundAtCitizenWith(worldIn, pos, EventType.BAD_WEATHER, citizen);
        }
        else if (v <= 0.8 && citizen.isIdleAtJob())
        {
            playSoundAtCitizenWith(worldIn, pos, EventType.MISSING_EQUIPMENT, citizen);
        }
        else
        {
            playSoundAtCitizenWith(worldIn, pos, EventType.NOISE, citizen, EventType.NOISE.getChance(), VOLUME/2);
        }
    }

    /**
     * Play a sound at a certain position.
     *
     * @param worldIn  the world to play the sound in.
     * @param position the position to play the sound at.
     * @param event    sound to play.
     */
    public static void playSoundAtCitizen(@NotNull final World worldIn, @NotNull final int[] position, @NotNull final SoundEvent event)
    {
        // [1.7.10] world.playSoundEffect uses string sound name
        worldIn.playSoundEffect(position[0] + 0.5, position[1] + 0.5, position[2] + 0.5,
          event.getSoundName(),
          (float) VOLUME,
          (float) PITCH);
    }

    /**
     * Play a success sound.
     * @param player the player to play it for.
     * @param position the position it is played at.
     */
    public static void playSuccessSound(@NotNull final EntityPlayer player, @NotNull final int[] position)
    {
        // [1.7.10] Use playSoundAtEntity for note block bell equivalent
        player.worldObj.playSoundAtEntity(player, "note.harp", (float) VOLUME * 2, 1.0f);
    }

    /**
     * Play an error sound.
     * @param player the player to play it for.
     * @param position the position it is played at.
     */
    public static void playErrorSound(@NotNull final EntityPlayer player, @NotNull final int[] position)
    {
        // [1.7.10] Use playSoundAtEntity for didgeridoo equivalent
        player.worldObj.playSoundAtEntity(player, "note.bass", (float) VOLUME * 2, 0.3f);
    }

    /**
     * Plays a sound with a certain chance at a certain position.
     *
     * @param worldIn     the world to play the sound in.
     * @param position    position to play the sound at.
     * @param type        sound to play.
     * @param citizenData the citizen.
     */
    public static void playSoundAtCitizenWith(
      @NotNull final World worldIn,
      @NotNull final int[] position,
      @Nullable final EventType type,
      @Nullable final ICivilianData citizenData)
    {
        playSoundAtCitizenWith(worldIn, position, type, citizenData, type.getChance());
    }

    /**
     * Plays a sound with a certain chance at a certain position.
     *
     * @param worldIn     the world to play the sound in.
     * @param position    position to play the sound at.
     * @param type        sound to play.
     * @param citizenData the citizen.
     */
    public static void playSoundAtCitizenWith(
      @NotNull final World worldIn,
      @NotNull final int[] position,
      @Nullable final EventType type,
      @Nullable final ICivilianData citizenData, final double chance, final double volume)
    {
        if (citizenData == null)
        {
            return;
        }

        // Always call job specific, we put all sounds into job specific. So, call here visitor, job, or if no job general
        final String jobDesc;
        if (citizenData instanceof IVisitorData)
        {
            jobDesc = "visitor";
        }
        else if (citizenData.isChild())
        {
            jobDesc = "child";
        }
        else if (citizenData instanceof ICitizenData)
        {
            final IJob<?> job = ((ICitizenData) citizenData).getJob();
            jobDesc = job == null ? "unemployed" : job.getJobRegistryEntry().getKey().getResourcePath();
        }
        else
        {
            jobDesc = "unemployed";
        }

        final SoundEvent event = citizenData.isFemale() ? CITIZEN_SOUND_EVENTS.get(jobDesc).get(type).get(citizenData.getVoiceProfile()).getB() : CITIZEN_SOUND_EVENTS.get(jobDesc).get(type).get(citizenData.getVoiceProfile()).getA();
        if (chance > rand.nextDouble() * ONE_HUNDRED)
        {
            if (worldIn.isRemote || !citizenData.getEntity().isPresent())
            {
                worldIn.playSoundEffect(
                  position[0] + 0.5, position[1] + 0.5, position[2] + 0.5,
                  event.getSoundName(), (float) volume, PITCH);
            }
            else
            {
              citizenData.getEntity().get().queueSound(event.getSoundName(), position[0], position[1], position[2], 60, 0);
            }
        }
    }

    /**
     * Plays a sound with a certain chance at a certain position.
     *
     * @param worldIn     the world to play the sound in.
     * @param position    position to play the sound at.
     * @param type        sound to play.
     * @param citizenData the citizen.
     */
    public static void playSoundAtCitizenWith(
      @NotNull final World worldIn,
      @NotNull final int[] position,
      @Nullable final EventType type,
      @Nullable final ICivilianData citizenData, final double chance)
    {
        playSoundAtCitizenWith(worldIn, position, type, citizenData, chance, VOLUME);
    }

    /**
     * Get a random pitch for a sound.
     *
     * @param random the random method.
     * @return a random double for the pitch.
     */
    public static double getRandomPitch(final Random random)
    {
        return PITCH_DIVIDER / (random.nextDouble() * PITCH_MULTIPLIER + BASE_PITCH);
    }

    /**
     * Generates a random tone from the D major pentatonic scale
     *
     * @param random the RNG instance
     * @return a number representing the pitch to the sound engine
     */
    public static double getRandomPentatonic(final Random random)
    {
        int index = random.nextInt(PENTATONIC.length);
        int tone = PENTATONIC[index] + Math.floorDiv(index, 5) * 12;
        return Math.pow(2.0D, (double)(tone - 12) / 12.0D);
    }

    /**
     * Plays a sound for the given player, but not for surrounding entities
     */
    public static void playSoundForPlayer(final EntityPlayerMP playerEntity, final SoundEvent sound, float volume, final float pitch)
    {
        // [1.7.10] Use worldObj.playSoundAtEntity instead of custom packet
        playerEntity.worldObj.playSoundAtEntity(playerEntity, sound.getSoundName(), 16.0F * volume, pitch);
    }
}





