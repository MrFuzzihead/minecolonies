package com.minecolonies.api.entity.mobs.vikings;

import com.minecolonies.api.entity.mobs.AbstractEntityMinecoloniesMonster;
import com.minecolonies.api.entity.mobs.RaiderType;
import net.minecraft.world.World;

import static com.minecolonies.core.colony.events.raid.RaiderConstants.ONE;
import static com.minecolonies.core.colony.events.raid.RaiderConstants.OUT_OF_ONE_HUNDRED;

/**
 * Abstract for all norsemen entities.
 */
public abstract class AbstractEntityNorsemen extends AbstractEntityMinecoloniesMonster
{
    /**
     * Swim speed for pirates
     */
    private static final double NORSEMEN_SWIM_BONUS = 2.3;

    /**
     * Amount of unique norsemen textures.
     */
    private static final int NORSEMEN_TEXTURES = 3;

    /**
     * Constructor method for Abstract norsemen..
     *
     * @param world the world.
     */
    public AbstractEntityNorsemen(final World world)
    {
        super(world, NORSEMEN_TEXTURES);
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
    public float getSoundPitch()
    {
        return (this.rand.nextFloat() - this.rand.nextFloat()) * 0.1F + 1.0F;
    }

    @Override
    public RaiderType getRaiderType()
    {
        return RaiderType.NORSEMAN;
    }

    @Override
    public double getSwimSpeedFactor()
    {
        return NORSEMEN_SWIM_BONUS;
    }
}
