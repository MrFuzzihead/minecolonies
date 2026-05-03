package com.minecolonies.core.entity.other;

import com.minecolonies.core.entity.citizen.EntityCitizen;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

/**
 * Custom fishing bobber for citizen fishers.
 * [1.7.10] Stub — full port deferred; extends EntityFishHook as closest 1.7.10 equivalent.
 * TODO: Port fishing logic from 1.21 NewBobberEntity.
 */
public class NewBobberEntity extends EntityFishHook
{
    public static final int XP_PER_CATCH = 2;

    private EntityCitizen angler;

    public NewBobberEntity(final World world)
    {
        super(world);
    }

    public NewBobberEntity(final World world, final EntityCitizen angler)
    {
        super(world, angler);
        this.angler = angler;
    }

    // TODO: Port fishing entity logic (loot tables, catchable delay, water detection) from 1.21
}
