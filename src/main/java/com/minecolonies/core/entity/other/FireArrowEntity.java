package com.minecolonies.core.entity.other;

import com.minecolonies.api.items.ModItems;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

/**
 * Custom arrow entity for the fire arrows.
 */
public class FireArrowEntity extends CustomArrowEntity
{
    public FireArrowEntity(final World world)
    {
        super(world);
    }

    public FireArrowEntity(final World world, final EntityLivingBase shooter)
    {
        super(world, shooter);
        this.setPosition(shooter.posX, shooter.posY + shooter.getEyeHeight() - 0.1, shooter.posZ);
        this.setFire(100);
    }

    @NotNull
    @Override
    public ItemStack func_145748_c_()
    {
        return new ItemStack(ModItems.firearrow, 1);
    }
}
