package com.minecolonies.core.entity.mobs.camp.pirates;

import com.minecolonies.api.entity.mobs.pirates.AbstractEntityPirate;
import com.minecolonies.api.entity.mobs.pirates.IMeleePirateEntity;
import net.minecraft.world.World;

public class EntityPirate extends AbstractEntityPirate implements IMeleePirateEntity
{
    public EntityPirate(final World worldIn) { super(worldIn); }
}
