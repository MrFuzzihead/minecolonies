package com.minecolonies.core.entity.mobs.camp.barbarians;

import com.minecolonies.api.entity.mobs.barbarians.AbstractEntityBarbarian;
import com.minecolonies.api.entity.mobs.barbarians.IChiefBarbarianEntity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.world.World;

public class EntityChiefBarbarian extends AbstractEntityBarbarian implements IChiefBarbarianEntity
{
    public EntityChiefBarbarian(final World worldIn) { super(worldIn); }

    @Override
    public void initStatsFor(final double baseHealth, final double difficulty, final double baseDamage)
    {
        super.initStatsFor(baseHealth, difficulty, baseDamage);
        this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(baseDamage + 1.0);
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(baseHealth * 1.5);
        this.setHealth(this.getMaxHealth());
    }
}
