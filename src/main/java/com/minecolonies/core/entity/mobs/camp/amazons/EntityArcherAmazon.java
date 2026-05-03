package com.minecolonies.core.entity.mobs.camp.amazons;

import com.minecolonies.api.entity.mobs.amazons.AbstractEntityAmazon;
import com.minecolonies.api.entity.mobs.amazons.IArcherAmazon;
import net.minecraft.world.World;

public class EntityArcherAmazon extends AbstractEntityAmazon implements IArcherAmazon
{
    public EntityArcherAmazon(final World worldIn) { super(worldIn); }

    @Override
    public double getAttackDelayModifier() { return 2; }
}
