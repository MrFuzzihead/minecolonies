package com.minecolonies.core.items;

import com.minecolonies.api.blocks.AbstractBlockHut;
import com.minecolonies.api.util.MessageUtils;
import com.minecolonies.api.util.constant.TranslationConstants;
import com.minecolonies.core.blocks.MinecoloniesCropBlock;
import com.minecolonies.core.blocks.MinecoloniesFarmland;
import net.minecraft.util.EnumChatFormatting;
// [1.7.10] client removed (use @SideOnly)
// [1.7.10] int[] -> int x,y,z
// [1.7.10] Holder removed
import net.minecraft.util.IChatComponent;
// [1.7.10] tags removed
import net.minecraft.entity.player.EntityPlayer;
// [1.7.10] food removed
import net.minecraft.world.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.level.block.FarmBlock;
// [1.7.10] BlockState -> int metadata
// [1.7.10] world.phys removed
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

/**
 * A custom item class for crop blocks.
 */
public class ItemCrop extends BlockItem
{
    /**
     * The preferred biome.
     */
    @Nullable
    private final TagKey<Biome> preferredBiome;

    /**
     * Creates a new Crop item.
     *
     * @param cropBlock   the {@link AbstractBlockHut} this item represents.
     * @param builder the item properties to use.
     */
    public ItemCrop(@NotNull final MinecoloniesCropBlock cropBlock, @NotNull final Properties builder, @Nullable final TagKey<Biome> preferredBiome)
    {
        super(cropBlock, builder.food(new FoodProperties.Builder().nutrition(1).saturationMod(0.3F).build()));
        this.preferredBiome = preferredBiome;
    }

    @Override
    protected boolean canPlace(BlockPlaceContext ctx, @NotNull BlockState state)
    {
        Player player = ctx.getPlayer();
        if (!player.isCreative())
        {
            final int[] clickedPos = ctx.getClickedPos().below();
            final BlockState worldState = ctx.getLevel().getBlockState(clickedPos);
            if (ctx.getLevel().isClientSide && (worldState.getBlock() instanceof MinecoloniesFarmland || worldState.getBlock() instanceof FarmBlock))
            {
                MessageUtils.format(String.translatable("com.minecolonies.core.crop.cantplant")).sendTo(player);
            }
            return false;
        }
        CollisionContext collisioncontext = player == null ? CollisionContext.empty() : CollisionContext.of(player);
        return (!this.mustSurvive() || state.canSurvive(ctx.getLevel(), ctx.getClickedPos())) && ctx.getLevel().isUnobstructed(state, ctx.getClickedPos(), collisioncontext);
    }

    @Override
    public void appendHoverText(@NotNull final ItemStack stack, @Nullable final World worldIn, @NotNull final List<String> tooltip, @NotNull final TooltipFlag flagIn)
    {
        tooltip.add(String.translatable(TranslationConstants.CROP_TOOLTIP).withStyle(ChatFormatting.GRAY));
        if (preferredBiome != null && worldIn != null)
        {
            tooltip.add(String.translatable(TranslationConstants.BIOME_TOOLTIP + "." + preferredBiome.location().getPath()));
            if (worldIn.getBiome(Minecraft.getInstance().player.blockPosition()).is(preferredBiome))
            {
                tooltip.add(String.translatable("com.minecolonies.core.item.crop.tooltip.biome.match").withStyle(ChatFormatting.GREEN));
            }
            else
            {
                tooltip.add(String.translatable("com.minecolonies.core.item.crop.tooltip.biome.nomatch").withStyle(ChatFormatting.RED));
            }
        }
        tooltip.add(String.translatable(TranslationConstants.CROP_TOOLTIP_HOE).withStyle(ChatFormatting.DARK_AQUA).withStyle(ChatFormatting.ITALIC));
    }

    /**
     * Check if this can planted in a given biome.
     * @param biome the biome to check.
     * @return true if so.
     */
    public boolean canBePlantedIn(final Holder<Biome> biome)
    {
        return preferredBiome == null ||  biome.is(preferredBiome);
    }
}




