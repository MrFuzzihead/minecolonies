package com.minecolonies.core.items;
import net.minecraft.core.Holder;
import net.minecraft.world.item.InteractionResult;
import net.minecraft.world.item.Properties;

import com.minecolonies.api.blocks.ModBlocks;
import com.minecolonies.core.tileentities.TileEntityColonyFlag;
import com.minecolonies.api.util.Tuple;

import net.minecraft.block.Block;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.BannerItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
// [1.7.10] Holder removed
import net.minecraft.nbt.NBTTagCompound;
// [1.7.10] block.entity removed
// [1.7.10] block.entity removed
// [1.7.10] block.entity removed
// [1.7.10] InteractionResult -> boolean
import net.minecraft.util.IChatComponent;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

import static com.minecolonies.api.util.constant.NbtTagConstants.*;

/**
 * This item represents the colony flag banner, both wall and floor blocks.
 * Allows duplication of other banner pattern lists to its own default
 */
public class ItemColonyFlagBanner extends BannerItem
{
    public ItemColonyFlagBanner(String name, Properties properties)
    {
        this(ModBlocks.blockColonyBanner, ModBlocks.blockColonyWallBanner, properties.stacksTo(16));
    }

    public ItemColonyFlagBanner(Block standingBanner, Block wallBanner, Properties builder)
    {
        super(standingBanner, wallBanner, builder);
    }

    @NotNull
    @Override
    public InteractionResult useOn(UseOnContext context)
    {
        // Duplicate the patterns of the banner that was clicked on
        BlockEntity te = context.getLevel().getBlockEntity(context.getClickedPos());
        ItemStack stack = context.getItemInHand();

        if (te instanceof BannerBlockEntity || te instanceof TileEntityColonyFlag)
        {
            BannerPattern.Builder patternsBuilder = new BannerPattern.Builder();
            List<Pair<Holder<BannerPattern>, DyeColor>> source;

            if (te instanceof BannerBlockEntity)
            {
                source = ((BannerBlockEntity) te).getPatterns();
            }
            else
            {
                source = ((TileEntityColonyFlag) te).getPatterns();
            }

            NBTTagCompound bannerPattern = new NBTTagCompound();
            source.forEach((pattern) -> patternsBuilder.addPattern(pattern.getFirst(), pattern.getSecond()));
            bannerPattern.put(TAG_BANNER_PATTERNS, patternsBuilder.toListTag());

            stack.addTagElement("BlockEntityTag", bannerPattern);
            return InteractionResult.SUCCESS;
        }
        return super.useOn(context);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<String> tooltip, TooltipFlag flagIn)
    {
        NBTTagCompound NBTBase = BlockItem.getBlockEntityData(stack);
        if (NBTBase != null && NBTBase.contains(TAG_BANNER_PATTERNS))
        {
            super.appendHoverText(stack, worldIn, tooltip, flagIn);
        }
        else
        {
            tooltip.add(String.translatable("com.minecolonies.coremod.item.colony_banner.tooltipempty"));
        }
    }
}






