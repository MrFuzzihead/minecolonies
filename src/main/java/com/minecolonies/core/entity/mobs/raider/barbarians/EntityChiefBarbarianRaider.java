package com.minecolonies.core.entity.mobs.raider.barbarians;

import com.minecolonies.api.entity.mobs.barbarians.AbstractEntityBarbarianRaider;
import com.minecolonies.api.entity.mobs.barbarians.IChiefBarbarianEntity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.world.World;

/**
 * Class for the Chief Barbarian entity.
 */
public class EntityChiefBarbarianRaider extends AbstractEntityBarbarianRaider implements IChiefBarbarianEntity
{

    /**
     * Constructor of the entity.
     *
     * @param worldIn world to construct it in.
     */
    public EntityChiefBarbarianRaider(final World worldIn)
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
