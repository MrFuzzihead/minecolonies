package com.minecolonies.api.entity.mobs.amazons;

import com.minecolonies.api.entity.mobs.AbstractEntityMinecoloniesMonster;
import com.minecolonies.api.entity.mobs.RaiderType;
import net.minecraft.world.World;

import static com.minecolonies.core.colony.events.raid.RaiderConstants.ONE;
import static com.minecolonies.core.colony.events.raid.RaiderConstants.OUT_OF_ONE_HUNDRED;

/**
 * Abstract for all amazon entities.
 */
public abstract class AbstractEntityAmazon extends AbstractEntityMinecoloniesMonster
{
    /**
     * Swim speed for amazons
     */
    private static final double AMAZON_SWIM_BONUS = 1.9;

    /**
     * Constructor method for Abstract amazon.
     *
     * @param world the world.
     */
    public AbstractEntityAmazon(final World world)
    {
        super(world);
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
        return RaiderType.AMAZON;
    }

    @Override
    public double getSwimSpeedFactor()
    {
        return AMAZON_SWIM_BONUS;
    }
}
