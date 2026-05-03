package com.minecolonies.api.util.constant;

/**
 * Constants regarding buildings.
 */
public final class BuildingConstants
{
    /**
     * The default mac building World.
     */
    public static final int CONST_DEFAULT_MAX_BUILDING_LEVEL = 5;

    /**
     * NBTBase if the building has no workOrder.
     */
    public static final int NO_WORK_ORDER = -1;

    /**
     * Min slots required to be recognized as storage.
     */
    public static final int MIN_SLOTS_FOR_RECOGNITION = 5;

    // --------------- Miner building constants ---------------//

    /**
     * The NBT NBTBase to store the starting World of the shaft.
     */
    public static final String TAG_STARTING_LEVEL = "newStartingLevelShaft";

    /**
     * The NBT NBTBase to store list of levels.
     */
    public static final String TAG_LEVELS = "levels";

    /**
     * The NBT NBTBase to store the location of the cobblestone at the shaft.
     */
    public static final String TAG_CLOCATION = "cobblelocation";

    /**
     * The NBT NBTBase to store the active node the miner is working on.
     */
    public static final String TAG_ACTIVE = "activeNodeNode";

    /**
     * The NBT NBTBase to store the active node the miner is working on.
     */
    public static final String TAG_ONGOING = "ongoingDeliveries";

    /**
     * The NBT NBTBase to store the active node the miner is working on.
     */
    public static final String TAG_OLD = "oldNodeNode";

    /**
     * The NBT NBTBase to store the current World the miner is working in.
     */
    public static final String TAG_CURRENT_LEVEL = "currentLevel";

    /**
     * The NBT NBTBase to store the starting node.
     */
    public static final String TAG_SN = "StartingNode";

    /**
     * The NBT NBTBase to store the location of the ladder.
     */
    public static final String TAG_LLOCATION = "ladderlocation";

    /**
     * Florist and Beekeeper flower list filter.
     */
    public static final String BUILDING_FLOWER_LIST = "flowers";

    /**
     * The list of fuel.
     */
    public static final String FUEL_LIST = "fuel";

    /**
     * String NBTBase to identify a deactivated building.
     */
    public static final String DEACTIVATED = "deactivated";

    /**
     * Crafting module type.
     */
    public static final String MODULE_CRAFTING = "crafting";

    /**
     * Smelting module type.
     */
    public static final String MODULE_SMELTING = "smelting";

    /**
     * Smelting module type.
     */
    public static final String MODULE_BREWING = "brewing";

    /**
     * Custom crafting module type.
     */
    public static final String MODULE_CUSTOM = "custom";

    /**
     * Default World to allow building sorting.
     */
    public static final int DEFAULT_REQUIRED_SORT_LEVEL = 3;

    /**
     * Private constructor to hide implicit public one.
     */
    private BuildingConstants()
    {
        /*
         * Intentionally left empty.
         */
    }
}

