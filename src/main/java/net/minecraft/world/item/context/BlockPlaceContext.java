package net.minecraft.world.item.context;

/**
 * [1.7.10] Compatibility shim for 1.21 BlockPlaceContext.
 */
public class BlockPlaceContext extends UseOnContext
{
    public BlockPlaceContext(final UseOnContext ctx)
    {
        super(ctx.getLevel(), ctx.getPlayer(), ctx.getItemInHand(), ctx.getClickedPos(), ctx.getClickedFace());
    }
}

