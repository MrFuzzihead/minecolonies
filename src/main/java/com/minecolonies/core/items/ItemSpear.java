package com.minecolonies.core.items;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.Properties;
import net.minecraft.world.entity.player.Player;

import com.minecolonies.core.entity.other.SpearEntity;
import com.minecolonies.core.client.render.SpearItemTileEntityRenderer;
// [1.7.10] client removed (use @SideOnly)
// [1.7.10] sounds removed
// [1.7.10] sounds removed
// [1.7.10] sounds removed
import net.minecraft.stats.Stats;
// [1.7.10] int /* InteractionHand */ removed
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
// [1.7.10] world.entity removed
import net.minecraft.item.ItemStack;
import net.minecraft.world.item.TridentItem;
import net.minecraft.world.World;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class ItemSpear extends TridentItem
{
    public ItemSpear(final Properties properties)
    {
        super(properties.durability(250));
    }

    @Override
    public void initializeClient(final Consumer<IClientItemExtensions> consumer)
    {
        super.initializeClient(consumer);
        consumer.accept(new IClientItemExtensions() {
            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer()
            {
                return new SpearItemTileEntityRenderer();
            }
        });
    }

    @Override
    public void releaseUsing(@NotNull ItemStack stack, @NotNull World worldIn, @NotNull EntityLivingBase entityLiving, int timeLeft)
    {
        if (entityLiving instanceof Player)
        {
            Player playerEntity = (Player) entityLiving;
            int usedForDuration = this.getUseDuration(stack) - timeLeft;
            if (usedForDuration >= 10)
            {
                if (!worldIn.isClientSide)
                {
                    stack.hurtAndBreak(1, playerEntity, playerEntity1 -> playerEntity1.broadcastBreakEvent(entityLiving.getUsedItemHand()));
                    SpearEntity spearEntity = new SpearEntity(worldIn, playerEntity, stack);

                    if (playerEntity.getAbilities().instabuild)
                    {
                        spearEntity.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
                    }
                    else
                    {
                        playerEntity.getInventory().removeItem(stack);
                    }

                    worldIn.addFreshEntity(spearEntity);
                    worldIn.playSound(null, spearEntity, SoundEvents.TRIDENT_THROW, SoundSource.PLAYERS, 1.0F, 1.0F);
                }

                SoundEvent soundEvent = SoundEvents.TRIDENT_THROW;
                playerEntity.awardStat(Stats.ITEM_USED.get(this));
                worldIn.playSound(null, playerEntity, soundEvent, SoundSource.PLAYERS, 1.0F, 1.0F);
            }
        }
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull final World world, final Player playerEntity, @NotNull final int /* InteractionHand */ hand)
    {
        ItemStack itemstack = playerEntity.getItemInHand(hand);
        if (itemstack.getDamageValue() >= itemstack.getMaxDamage() - 1)
        {
            return InteractionResultHolder.fail(itemstack);
        }
        else
        {
            playerEntity.startUsingItem(hand);
            return InteractionResultHolder.consume(itemstack);
        }
    }
}




