package com.minecolonies.core.entity.mobs.raider.drownedpirates;

import com.minecolonies.api.entity.mobs.drownedpirate.AbstractDrownedEntityPirateRaider;
import com.minecolonies.api.entity.mobs.pirates.IMeleePirateEntity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.world.World;

/**
 * Class for the Pirate entity.
 */
public class EntityDrownedPirateRaider extends AbstractDrownedEntityPirateRaider implements IMeleePirateEntity
{

    /**
     * Constructor of the entity.
     *
     * @param worldIn world to construct it in.
     */
    public EntityDrownedPirateRaider(final World worldIn)
    {
        super(worldIn);
    }

    @Override
    public void initStatsFor(final double baseHealth, final double difficulty, final double baseDamage)
    {
        super.initStatsFor(baseHealth, difficulty, baseDamage);
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(baseHealth * 1.5);
        this.setHealth(this.getMaxHealth());
    }
}
