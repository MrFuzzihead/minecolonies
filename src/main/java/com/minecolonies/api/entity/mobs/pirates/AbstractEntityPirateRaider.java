package com.minecolonies.api.entity.mobs.pirates;

import com.minecolonies.api.entity.mobs.AbstractEntityMinecoloniesRaider;
import com.minecolonies.api.entity.mobs.RaiderType;
import net.minecraft.world.World;

import java.util.Random;

import static com.minecolonies.core.colony.events.raid.RaiderConstants.ONE;
import static com.minecolonies.core.colony.events.raid.RaiderConstants.OUT_OF_ONE_HUNDRED;

/**
 * Abstract for all pirate entities.
 */
public abstract class AbstractEntityPirateRaider extends AbstractEntityMinecoloniesRaider
{
    /**
     * Swim speed for pirates
     */
    private static final double PIRATE_SWIM_BONUS = 2.3;

    /**
     * Amount of unique pirate textures.
     */
    private static final int PIRATE_TEXTURES = 4;

    /**
     * Constructor method for Abstract Barbarians.
     *
     * @param world the world.
     */
    public AbstractEntityPirateRaider(final World world)
    {
        super(world, PIRATE_TEXTURES);
    }

    @Override
    public void playLivingSound()
    {
        final String sound = getAmbientSoundName();
        if (sound != null && worldObj.rand.nextInt(OUT_OF_ONE_HUNDRED) <= ONE)
        {
            this.playSound(sound, this.getSoundVolume(), this.getSoundPitch());
        }
    }

    @Override
    public RaiderType getRaiderType()
    {
        return RaiderType.PIRATE;
    }

    @Override
    public double getSwimSpeedFactor()
    {
        return PIRATE_SWIM_BONUS;
    }
}
