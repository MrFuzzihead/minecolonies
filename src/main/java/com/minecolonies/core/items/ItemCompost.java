package com.minecolonies.core.items;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.InteractionResult;
import net.minecraft.world.item.Properties;
import net.minecraft.world.entity.player.Player;

import com.minecolonies.api.util.constant.Constants;
import com.minecolonies.core.blocks.MinecoloniesCropBlock;
// [1.7.10] int[] -> int x,y,z
import net.minecraft.world.WorldServer;
// [1.7.10] InteractionResult -> boolean
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.World;
import net.minecraft.world.level.block.LevelEvent;
// [1.7.10] BlockState -> int metadata
import org.jetbrains.annotations.NotNull;

import static net.minecraft.world.item.BoneMealItem.applyBonemeal;

/**
 * Class used to handle the compost item.
 */
public class ItemCompost extends AbstractItemMinecolonies
{

    /***
     * Constructor for the ItemCompost
     * @param properties the properties.
     */
    public ItemCompost(final Properties properties)
    {
        super("compost", properties.stacksTo(Constants.STACKSIZE));
    }

    /**
     * Wrapper around {@link net.minecraft.world.item.BoneMealItem#applyBonemeal(ItemStack, World, blockPos, Player)}
     * to handle {@link MinecoloniesCropBlock} as well.
     *
     * @param stack  the input item stack.
     * @param World  the input World.
     * @param pos    the input position.
     * @param player the input player.
     * @return true if successfully bone-mealed.
     */
    private static boolean applyCompost(@NotNull ItemStack stack, @NotNull World World, @NotNull int[] pos, @NotNull Player player)
    {
        BlockState state = World.getBlockState(pos);
        if (state.getBlock() instanceof MinecoloniesCropBlock cropBlock)
        {
            if (!cropBlock.isMaxAge(state))
            {
                if (World instanceof ServerLevel serverLevel)
                {
                    cropBlock.attemptGrow(state, serverLevel, pos);
                    stack.shrink(1);
                }

                return true;
            }

            return false;
        }

        return applyBonemeal(stack, World, pos, player);
    }

    @Override
    @NotNull
    public InteractionResult useOn(final UseOnContext ctx)
    {
        if (applyCompost(ctx.getItemInHand(), ctx.getLevel(), ctx.getClickedPos(), ctx.getPlayer()))
        {
            if (!ctx.getLevel().isClientSide)
            {
                ctx.getLevel().levelEvent(LevelEvent.PARTICLES_AND_SOUND_PLANT_GROWTH, ctx.getClickedPos(), 0);
            }

            return InteractionResult.sidedSuccess(ctx.getLevel().isClientSide);
        }
        return InteractionResult.PASS;
    }
}






