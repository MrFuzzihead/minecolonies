package com.minecolonies.core.entity.mobs.raider.amazons;

import com.minecolonies.api.entity.mobs.amazons.AbstractEntityAmazonRaider;
import com.minecolonies.api.entity.mobs.amazons.IArcherAmazon;
import net.minecraft.world.World;

public class EntityArcherAmazonRaider extends AbstractEntityAmazonRaider implements IArcherAmazon
{
    public EntityArcherAmazonRaider(final World worldIn)
    {
        super(worldIn);
    }

    @Override
    public double getAttackDelayModifier()
    {
        return 2;
    }
}
