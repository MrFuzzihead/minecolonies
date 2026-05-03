package com.minecolonies.api.entity.mobs.drownedpirate;

import com.minecolonies.api.MinecoloniesAPIProxy;
import com.minecolonies.api.entity.mobs.AbstractEntityMinecoloniesMonster;
import com.minecolonies.api.entity.mobs.RaiderType;
import com.minecolonies.api.entity.pathfinding.registry.IPathNavigateRegistry;
import com.minecolonies.core.entity.pathfinding.navigation.AbstractAdvancedPathNavigate;
import com.minecolonies.core.entity.pathfinding.navigation.PathingStuckHandler;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import static com.minecolonies.core.colony.events.raid.RaiderConstants.ONE;
import static com.minecolonies.core.colony.events.raid.RaiderConstants.OUT_OF_ONE_HUNDRED;

/**
 * Abstract for all drowned pirate entities.
 */
public abstract class AbstractDrownedEntityPirate extends AbstractEntityMinecoloniesMonster
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
    public AbstractDrownedEntityPirate(final World world)
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

