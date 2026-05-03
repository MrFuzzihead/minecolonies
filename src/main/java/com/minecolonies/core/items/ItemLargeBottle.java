package com.minecolonies.core.items;

import com.minecolonies.api.items.ModItems;
import com.minecolonies.api.util.InventoryUtils;
// [1.7.10] int[] -> int x,y,z
// [1.7.10] sounds removed
// [1.7.10] sounds removed
// [1.7.10] tags removed
// [1.7.10] int /* InteractionHand */ removed
// [1.7.10] InteractionResult -> boolean
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.entity.EntityLivingBase;
// [1.7.10] world.entity removed
// [1.7.10] world.entity removed
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World.ClipContext;
import net.minecraft.world.World;
// [1.7.10] world.phys removed
// [1.7.10] world.phys removed
// [1.7.10] items shim in com.minecolonies.api.shim
import org.jetbrains.annotations.NotNull;

import static com.minecolonies.api.util.constant.Constants.TICKS_SECOND;

/**
 * A custom item class for jug items.
 */
public class ItemLargeBottle extends Item
{
    /**
     * Creates a new jug item.
     *
     * @param builder the item properties to use.
     */
    public ItemLargeBottle(@NotNull final Properties builder)
    {
        super(builder);
    }

    @NotNull
    @Override
    public InteractionResult interactLivingEntity(
        @NotNull final ItemStack stack,
        @NotNull final Player player,
        @NotNull final EntityLivingBase entity,
        @NotNull final int /* InteractionHand */ hand)
    {
        if (this != ModItems.large_empty_bottle)
        {
            return super.interactLivingEntity(stack, player, entity, hand);
        }

        if (player.getCooldowns().isOnCooldown(this))
        {
            return super.interactLivingEntity(stack, player, entity, hand);
        }

        if (entity instanceof Cow && !entity.isBaby())
        {
            player.playSound(SoundEvents.COW_MILK, 1.0F, 1.0F);
            if (!InventoryUtils.addItemStackToItemHandler(new PlayerMainInvWrapper(player.getInventory()), ModItems.large_milk_bottle.getDefaultInstance()))
            {
                player.drop(ModItems.large_milk_bottle.getDefaultInstance(), false);
            }
            stack.shrink(1);
            player.getCooldowns().addCooldown(this, TICKS_SECOND * 10);
            return InteractionResult.SUCCESS;
        }
        else if (entity instanceof final Goat goat && !entity.isBaby())
        {
            player.playSound(goat.getMilkingSound(), 1.0F, 1.0F);
            if (!InventoryUtils.addItemStackToItemHandler(new PlayerMainInvWrapper(player.getInventory()), ModItems.large_milk_bottle.getDefaultInstance()))
            {
                player.drop(ModItems.large_milk_bottle.getDefaultInstance(), false);
            }
            stack.shrink(1);
            player.getCooldowns().addCooldown(this, TICKS_SECOND * 10);
            return InteractionResult.SUCCESS;
        }

        return super.interactLivingEntity(stack, player, entity, hand);
    }

    @NotNull
    @Override
    public InteractionResultHolder<ItemStack> use(@NotNull final World World, final Player player, @NotNull final int /* InteractionHand */ hand)
    {
        final ItemStack itemstack = player.getItemInHand(hand);
        if (this != ModItems.large_empty_bottle)
        {
            return InteractionResultHolder.pass(itemstack);
        }

        BlockHitResult blockhitresult = getPlayerPOVHitResult(World, player, ClipContext.Fluid.SOURCE_ONLY);
        if (blockhitresult.getType() != HitResult.Type.MISS)
        {
            if (blockhitresult.getType() == HitResult.Type.BLOCK)
            {
                int[] blockPos = blockhitresult.getBlockPos();
                if (!World.mayInteract(player, blockPos))
                {
                    return InteractionResultHolder.pass(itemstack);
                }

                if (World.getFluidState(blockPos).is(FluidTags.WATER))
                {
                    World.playSound(player, player.getX(), player.getY(), player.getZ(), SoundEvents.BOTTLE_FILL, SoundSource.NEUTRAL, 1.0F, 1.0F);
                    if (!InventoryUtils.addItemStackToItemHandler(new PlayerMainInvWrapper(player.getInventory()), ModItems.large_water_bottle.getDefaultInstance()))
                    {
                        player.drop(ModItems.large_water_bottle.getDefaultInstance(), false);
                    }
                    itemstack.shrink(1);
                    player.getCooldowns().addCooldown(this, TICKS_SECOND);
                    return InteractionResultHolder.success(itemstack);
                }
            }
        }
        return InteractionResultHolder.pass(itemstack);
    }
}






