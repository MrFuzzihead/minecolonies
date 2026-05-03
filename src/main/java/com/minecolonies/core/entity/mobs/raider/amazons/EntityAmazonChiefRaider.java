package com.minecolonies.core.entity.mobs.raider.amazons;

import com.minecolonies.api.entity.mobs.amazons.AbstractEntityAmazonRaider;
import com.minecolonies.api.entity.mobs.amazons.IAmazonChief;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.world.World;

import static com.minecolonies.core.colony.events.raid.RaiderConstants.CHIEF_BONUS_ARMOR;

public class EntityAmazonChiefRaider extends AbstractEntityAmazonRaider implements IAmazonChief
{
    public EntityAmazonChiefRaider(final World worldIn)
    {
        super(worldIn);
    }

    @Override
    public void initStatsFor(final double baseHealth, final double difficulty, final double baseDamage)
    {
        super.initStatsFor(baseHealth, difficulty, baseDamage);
        // No armor attribute in 1.7.10; adjust health and damage only
        this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(baseDamage + 1.0);
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(baseHealth * 1.5);
        this.setHealth(this.getMaxHealth());
    }
}
