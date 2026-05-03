package com.minecolonies.core.entity.mobs.camp.amazons;

import com.minecolonies.api.entity.mobs.amazons.AbstractEntityAmazon;
import com.minecolonies.api.entity.mobs.amazons.IAmazonChief;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.world.World;

/**
 * Class for the Amazon Chief entity.
 */
public class EntityAmazonChief extends AbstractEntityAmazon implements IAmazonChief
{
    /**
     * Constructor of the entity.
     *
     * @param worldIn world to construct it in.
     */
    public EntityAmazonChief(final World worldIn) { super(worldIn); }

    @Override
    public void initStatsFor(final double baseHealth, final double difficulty, final double baseDamage)
    {
        super.initStatsFor(baseHealth, difficulty, baseDamage);
        this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(baseDamage + 1.0);
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(baseHealth * 1.5);
        this.setHealth(this.getMaxHealth());
    }
}
