package com.minecolonies.api.colony;

// [1.7.10] int[] -> int x,y,z
// [1.7.10] int /* ResourceKey */ -> int dimensionId
import net.minecraft.world.World;

/**
 * Compact colony for allies and feud data.
 */
public class CompactColonyReference
{
    /**
     * The name of the colony.
     */
    public final String name;

    /**
     * The position of the colony.
     */
    public final int[] center;

    /**
     * The id of the colony.
     */
    public final int id;

    /**
     * If it has a townhall or not.
     */
    public final boolean hasTownHall;

    /**
     * The dimension the colony is in.
     */
    public final int /* ResourceKey */ dimension;

    /**
     * Create a new compact colony object.
     *
     * @param name        the name of the colony.
     * @param center      the center.
     * @param id          the id.
     * @param hasTownHall if it has a town hall.
     * @param dimension   the dimension it is in.
     */
    public CompactColonyReference(final String name, final int[] center, final int id, final boolean hasTownHall, final int /* ResourceKey */ dimension)
    {
        this.name = name;
        this.center = center;
        this.id = id;
        this.hasTownHall = hasTownHall;
        this.dimension = dimension;
    }
}


