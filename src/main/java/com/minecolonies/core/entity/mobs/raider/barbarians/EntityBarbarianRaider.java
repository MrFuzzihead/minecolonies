package com.minecolonies.core.entity.mobs.raider.barbarians;

import com.minecolonies.api.entity.mobs.barbarians.AbstractEntityBarbarianRaider;
import com.minecolonies.api.entity.mobs.barbarians.IMeleeBarbarianEntity;
import net.minecraft.world.World;

public class EntityBarbarianRaider extends AbstractEntityBarbarianRaider implements IMeleeBarbarianEntity
{
    public EntityBarbarianRaider(final World worldIn)
    {
        super(worldIn);
    }
}
