package com.minecolonies.api.blocks.types;

import org.jetbrains.annotations.NotNull;

/**
 * Barrel fill World type — ported to 1.7.10.
 * Plain enum; no block state property system in 1.7.10.
 */
public enum BarrelType
{
    ZERO(0, "0perc"),
    TWENTY(1, "20perc"),
    FORTY(2, "40perc"),
    SIXTY(3, "60perc"),
    EIGHTY(4, "80perc"),
    HUNDRED(5, "100perc"),
    WORKING(6, "working"),
    DONE(7, "done");

    private static final BarrelType[] META_LOOKUP = new BarrelType[values().length];
    static
    {
        for (final BarrelType enumtype : values())
        {
            META_LOOKUP[enumtype.getMetadata()] = enumtype;
        }
    }

    private final int    meta;
    private final String name;

    BarrelType(final int metaIn, final String nameIn)
    {
        this.meta = metaIn;
        this.name = nameIn;
    }

    /**
     * Returns a type by a given metadata
     *
     * @param meta the metadata
     * @return the type
     */
    public static BarrelType byMetadata(final int meta)
    {
        int tempMeta = meta;
        if (tempMeta < 0 || tempMeta >= META_LOOKUP.length)
        {
            tempMeta = 0;
        }
        return META_LOOKUP[tempMeta];
    }

    /**
     * Returns the metadata
     * @return the metadata of the type
     */
    public int getMetadata()
    {
        return this.meta;
    }

    @Override
    public String toString()
    {
        return this.name;
    }

    @NotNull
    public String getName()
    {
        return this.name;
    }

    public String getTranslationKey()
    {
        return this.name;
    }
}



