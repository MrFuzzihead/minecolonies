package com.minecolonies.core.entity.mobs.camp.drownedpirates;

import com.minecolonies.api.entity.mobs.drownedpirate.AbstractDrownedEntityPirate;
import com.minecolonies.api.entity.mobs.pirates.ICaptainPirateEntity;
import com.minecolonies.api.util.MathUtils;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.world.World;

public class EntityDrownedCaptainPirate extends AbstractDrownedEntityPirate implements ICaptainPirateEntity
{
    public EntityDrownedCaptainPirate(final World worldIn) { super(worldIn); }

    @Override
    public void initStatsFor(final double baseHealth, final double difficulty, final double baseDamage)
    {
        super.initStatsFor(baseHealth, difficulty, baseDamage);
        this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(baseDamage);
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(baseHealth * 2.0);
        this.setHealth(this.getMaxHealth());
        if (MathUtils.RANDOM.nextInt(100) < 2)
        {
            setCustomNameTag("Davy Jones");
        }
    }
}
