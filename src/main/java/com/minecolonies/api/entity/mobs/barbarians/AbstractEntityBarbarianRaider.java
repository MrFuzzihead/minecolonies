package com.minecolonies.api.entity.mobs.barbarians;

import com.minecolonies.api.entity.mobs.AbstractEntityMinecoloniesRaider;
import com.minecolonies.api.entity.mobs.RaiderType;
import net.minecraft.world.World;

import static com.minecolonies.core.colony.events.raid.RaiderConstants.ONE;
import static com.minecolonies.core.colony.events.raid.RaiderConstants.OUT_OF_ONE_HUNDRED;

/**
 * Abstract for all Barbarian entities.
 */
public abstract class AbstractEntityBarbarianRaider extends AbstractEntityMinecoloniesRaider
{
    /**
     * Swim speed for barbarians
     */
    private static final double BARBARIAN_SWIM_BONUS = 2.0;

    /**
     * Constructor method for Abstract Barbarians.
     *
     * @param world the world.
     */
    public AbstractEntityBarbarianRaider(final World world)
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
        return RaiderType.BARBARIAN;
    }

    @Override
    public double getSwimSpeedFactor()
    {
        return BARBARIAN_SWIM_BONUS;
    }
}
