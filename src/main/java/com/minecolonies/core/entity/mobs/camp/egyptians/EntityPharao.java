package com.minecolonies.core.entity.mobs.camp.egyptians;

import com.minecolonies.api.entity.mobs.egyptians.AbstractEntityEgyptian;
import com.minecolonies.api.entity.mobs.egyptians.IPharaoEntity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.world.World;

/**
 * Class for the Pharao entity.
 */
public class EntityPharao extends AbstractEntityEgyptian implements IPharaoEntity
{

    /**
     * Constructor of the entity.
     *
     * @param worldIn world to construct it in.
     */
    public EntityPharao(final World worldIn) { super(worldIn); }

    @Override
    public void initStatsFor(final double baseHealth, final double difficulty, final double baseDamage)
    {
        super.initStatsFor(baseHealth, difficulty, baseDamage);
        this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(baseDamage + 1.0);
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(baseHealth * 4.5);
        this.setHealth(this.getMaxHealth());
    }
}
