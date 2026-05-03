package net.minecraft.tileentity;

/**
 * [1.7.10] Shim: BlockEntity is the 1.21 name for TileEntity.
 * All uses should be treated as TileEntity.
 */
public class BlockEntity extends TileEntity
{
    /**
     * [1.7.10] Stub for BlockEntity.loadStatic(pos, state, nbt).
     * In 1.7.10, tile entities are loaded via TileEntity.createAndLoadEntity(nbt).
     *
     * @param pos           block position (unused, for API compatibility)
     * @param state         block state (unused)
     * @param nbt           the NBT data
     * @return loaded TileEntity or null
     */
    public static TileEntity loadStatic(final int[] pos, final Object state, final net.minecraft.nbt.NBTTagCompound nbt)
    {
        if (nbt == null) return null;
        try
        {
            return TileEntity.createAndLoadEntity(nbt);
        }
        catch (final Exception e)
        {
            return null;
        }
    }
}

