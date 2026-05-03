package com.minecolonies.core.entity.mobs.camp.norsemen;

import com.minecolonies.api.entity.mobs.vikings.AbstractEntityNorsemen;
import com.minecolonies.api.entity.mobs.vikings.INorsemenChiefEntity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.world.World;

/**
 * Class for the Chief norsemen entity.
 */
public class EntityNorsemenChief extends AbstractEntityNorsemen implements INorsemenChiefEntity
{
    /**
     * Constructor of the entity.
     *
     * @param worldIn world to construct it in.
     */
    public EntityNorsemenChief(final World worldIn) { super(worldIn); }

    @Override
    public void initStatsFor(final double baseHealth, final double difficulty, final double baseDamage)
    {
        super.initStatsFor(baseHealth, difficulty, baseDamage);
        this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(baseDamage + 1.0);
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(baseHealth * 1.5);
        this.setHealth(this.getMaxHealth());
    }
}
