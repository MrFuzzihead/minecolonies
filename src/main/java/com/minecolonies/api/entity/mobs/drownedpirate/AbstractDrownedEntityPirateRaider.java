package com.minecolonies.api.entity.mobs.drownedpirate;

import com.minecolonies.api.entity.mobs.AbstractEntityMinecoloniesRaider;
import com.minecolonies.api.entity.mobs.RaiderType;
import net.minecraft.world.World;

import static com.minecolonies.core.colony.events.raid.RaiderConstants.ONE;
import static com.minecolonies.core.colony.events.raid.RaiderConstants.OUT_OF_ONE_HUNDRED;

/**
 * Abstract for all drowned pirate entities.
 */
public abstract class AbstractDrownedEntityPirateRaider extends AbstractEntityMinecoloniesRaider
{
    /**
     * Swim speed for pirates
     */
    private static final double PIRATE_SWIM_BONUS = 3.0;

    /**
     * Amount of unique pirate textures.
     */
    private static final int PIRATE_TEXTURES = 4;

    /**
     * Constructor method for Abstract Barbarians.
     *
     * @param world the world.
     */
    public AbstractDrownedEntityPirateRaider(final World world)
    {
        super(world, PIRATE_TEXTURES);
    }

    @Override
    public void playLivingSound()
    {
        if (worldObj.rand.nextInt(OUT_OF_ONE_HUNDRED) <= ONE)
        {
            // TODO: no SoundEvents.DROWNED_AMBIENT in 1.7.10; use placeholder
            this.playSound("EntityCreature.zombie.say", this.getSoundVolume(), this.getSoundPitch());
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

