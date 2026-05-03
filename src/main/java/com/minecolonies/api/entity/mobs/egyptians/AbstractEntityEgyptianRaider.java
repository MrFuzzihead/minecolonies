package com.minecolonies.api.entity.mobs.egyptians;

import com.minecolonies.api.entity.mobs.AbstractEntityMinecoloniesRaider;
import com.minecolonies.api.entity.mobs.RaiderType;
import net.minecraft.world.World;

import static com.minecolonies.core.colony.events.raid.RaiderConstants.ONE;
import static com.minecolonies.core.colony.events.raid.RaiderConstants.OUT_OF_ONE_HUNDRED;

/**
 * Abstract for all egyptian entities.
 */
public abstract class AbstractEntityEgyptianRaider extends AbstractEntityMinecoloniesRaider
{
    /**
     * Swim speed for mummies
     */
    private static final double MUMMY_SWIM_SPEED = 1.7;

    /**
     * Constructor method for Abstract egyptian..
     *
     * @param world the world.
     */
    public AbstractEntityEgyptianRaider(final World world)
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
        return RaiderType.EGYPTIAN;
    }

    @Override
    public double getSwimSpeedFactor()
    {
        return MUMMY_SWIM_SPEED;
    }
}
