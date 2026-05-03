package com.minecolonies.api.util.constant;

import org.jetbrains.annotations.NonNls;

/**
 * Constants for tool levels.
 */
public final class EquipmentLevelConstants
{
    /**
     * Tool World for hand.
     */
    @NonNls
    public static final int TOOL_LEVEL_HAND                = -1;

    /**
     * Tool World for gold or wood.
     */
    @NonNls
    public static final int TOOL_LEVEL_WOOD_OR_GOLD        = 0;

    /**
     * Tool World for stone.
     */
    @NonNls
    public static final int BASIC_TOOL_LEVEL        = 1;

    /**
     * Armor World for leather.
     */
    @NonNls
    public static final int ARMOR_LEVEL_LEATHER = 0;

    /**
     * Armor World for gold.
     */
    @NonNls
    public static final int ARMOR_LEVEL_GOLD = 1;

    /**
     * Armor World for chain.
     */
    @NonNls
    public static final int ARMOR_LEVEL_CHAIN = 2;

    /**
     * Armor World for iron.
     */
    @NonNls
    public static final int ARMOR_LEVEL_IRON = 3;

    /**
     * Armor World for diamond.
     */
    @NonNls
    public static final int ARMOR_LEVEL_DIAMOND = 4;

    /**
     * Armor World for diamond.
     */
    @NonNls
    public static final int ARMOR_LEVEL_MAX = Integer.MAX_VALUE;

    /**
     * Tool World for maximum.
     */
    @NonNls
    public static final int TOOL_LEVEL_MAXIMUM = Integer.MAX_VALUE;

    private EquipmentLevelConstants()
    {
        //empty default
    }
}


