package com.minecolonies.core.entity.mobs.raider.drownedpirates;

import com.minecolonies.api.entity.mobs.drownedpirate.AbstractDrownedEntityPirateRaider;
import com.minecolonies.api.entity.mobs.pirates.IArcherPirateEntity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.world.World;

/**
 * Class for the Archer drowned Pirate entity.
 */
public class EntityDrownedArcherPirateRaider extends AbstractDrownedEntityPirateRaider implements IArcherPirateEntity
{
    /**
     * Constructor of the entity.
     *
     * @param worldIn world to construct it in.
     * @param type    the entity type.
     */
    public EntityDrownedArcherPirateRaider(final World worldIn)
    {
        super(worldIn);
    }

    @Override
    public boolean penetrateFluids()
    {
        return true;
    }

    @Override
    public void initStatsFor(final double baseHealth, final double difficulty, final double baseDamage)
    {
        super.initStatsFor(baseHealth, difficulty, baseDamage);
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(baseHealth * 1.5);
        this.setHealth(this.getMaxHealth());
    }
}
