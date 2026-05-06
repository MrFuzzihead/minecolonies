package com.minecolonies.core.items;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.Properties;

import com.minecolonies.api.entity.ModEntities;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
// [1.7.10] world.entity removed
import net.minecraft.world.item.ArrowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

/**
 * Class handling the Scepter for the Pharao.
 */
public class ItemFireArrow extends ArrowItem
{
    /**
     * Constructor method for the Chief Sword Item
     *
     * @param properties the properties.
     */
    public ItemFireArrow(final Properties properties)
    {
        super(properties);
    }

    @Override
    public boolean hasCustomEntity(final ItemStack stack)
    {
        return true;
    }

    @NotNull
    @Override
    public AbstractArrow createArrow(@NotNull final World worldIn, @NotNull final ItemStack stack, final EntityLivingBase shooter)
    {
        AbstractArrow entity = ModEntities.FIREARROW.create(worldIn);
        entity.setOwner(shooter);
        return entity;
    }

    @Nullable
    @Override
    public Entity createEntity(final World world, final Entity location, final ItemStack itemstack)
    {
        return ModEntities.FIREARROW.create(world);
    }
}



