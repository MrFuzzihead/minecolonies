package com.minecolonies.api.entity.other;

// [1.7.10 BACKPORT STUB] MinecoloniesMinecart uses many 1.21-specific APIs (RailShape, Vec3i, NetworkHooks, etc.).
// This class is stubbed to extend EntityMinecart with no collision.
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.world.World;

/**
 * Special minecolonies minecart that doesn't collide.
 * [1.7.10 BACKPORT STUB]
 */
public class MinecoloniesMinecart extends EntityMinecart
{
    public MinecoloniesMinecart(final World world)
    {
        super(world);
    }

    public MinecoloniesMinecart(final World world, final double x, final double y, final double z)
    {
        super(world, x, y, z);
    }

    @Override
    public int getMinecartType()
    {
        return 0;
    }

    @Override
    public boolean canBeCollidedWith()
    {
        return false;
    }

    @Override
    public boolean canBePushed()
    {
        return false;
    }
}
