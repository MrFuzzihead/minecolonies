package com.minecolonies.core.entity.mobs.raider.pirates;

import com.minecolonies.api.entity.mobs.pirates.AbstractEntityPirateRaider;
import com.minecolonies.api.entity.mobs.pirates.IMeleePirateEntity;
import net.minecraft.world.World;

/**
 * Class for the Pirate entity.
 */
public class EntityPirateRaider extends AbstractEntityPirateRaider implements IMeleePirateEntity
{

    /**
     * Constructor of the entity.
     *
     * @param worldIn world to construct it in.
     */
    public EntityPirateRaider(final World worldIn)
    {
        super(worldIn);
        // TODO: MovementHandler equivalent for 1.7.10 if needed
    }
}
