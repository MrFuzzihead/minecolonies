package com.minecolonies.core.entity.mobs.raider.pirates;

import com.minecolonies.api.entity.mobs.pirates.AbstractEntityPirateRaider;
import com.minecolonies.api.entity.mobs.pirates.ICaptainPirateEntity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.world.World;

/**
 * Class for the Chief Pirate entity.
 */
public class EntityCaptainPirateRaider extends AbstractEntityPirateRaider implements ICaptainPirateEntity
{

    /**
     * Constructor of the entity.
     *
     * @param worldIn world to construct it in.
     */
    public EntityCaptainPirateRaider(final World worldIn)
    {
        super(worldIn);
    }

    @Override
    public void initStatsFor(final double baseHealth, final double difficulty, final double baseDamage)
    {
        super.initStatsFor(baseHealth, difficulty, baseDamage);
        // No armor attribute in 1.7.10
        this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(baseDamage + 2.0);
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(baseHealth * 1.3);
        this.setHealth(this.getMaxHealth());
    }
}
