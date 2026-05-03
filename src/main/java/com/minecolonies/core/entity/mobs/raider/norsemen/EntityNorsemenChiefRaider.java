package com.minecolonies.core.entity.mobs.raider.norsemen;

import com.minecolonies.api.entity.mobs.vikings.AbstractEntityNorsemenRaider;
import com.minecolonies.api.entity.mobs.vikings.INorsemenChiefEntity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.world.World;

/**
 * Class for the Chief norsemen entity.
 */
public class EntityNorsemenChiefRaider extends AbstractEntityNorsemenRaider implements INorsemenChiefEntity
{
    /**
     * Constructor of the entity.
     *
     * @param worldIn world to construct it in.
     */
    public EntityNorsemenChiefRaider(final World worldIn)
    {
        super(worldIn);
    }

    @Override
    public void initStatsFor(final double baseHealth, final double difficulty, final double baseDamage)
    {
        super.initStatsFor(baseHealth, difficulty, baseDamage);
        // No armor attribute in 1.7.10
        this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(baseDamage + 1.0);
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(baseHealth * 1.5);
        this.setHealth(this.getMaxHealth());
    }
}
